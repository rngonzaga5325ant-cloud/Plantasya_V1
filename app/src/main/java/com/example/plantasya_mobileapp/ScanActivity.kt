package com.example.plantasya_mobileapp

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import com.example.plantasya_mobileapp.ml.ModelUnquant
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity() {

    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null
    private var model: ModelUnquant? = null
    
    private lateinit var viewFinder: PreviewView
    private lateinit var scanContainer: FrameLayout
    private lateinit var fragmentContainer: FrameLayout
    private lateinit var captureButton: ImageButton
    private lateinit var btnBack: ImageButton

    // Labels based on the model provided
    private val labels = listOf("Aglaonema", "Pothos", "Calathea", "Money Tree", "Orchid")

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ScanActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan)

        // Initialize Views
        viewFinder = findViewById(R.id.viewFinder)
        scanContainer = findViewById(R.id.scan_container)
        fragmentContainer = findViewById(R.id.fragment_container)
        captureButton = findViewById(R.id.capture_button)
        btnBack = findViewById(R.id.btn_back)

        cameraExecutor = Executors.newSingleThreadExecutor()

        // Load TFLite model
        try {
            model = ModelUnquant.newInstance(this)
        } catch (e: Exception) {
            Toast.makeText(this, "Failed to load model: ${e.message}", Toast.LENGTH_LONG).show()
        }

        // Start camera
        startCamera()

        // Capture button
        captureButton.setOnClickListener {
            takePhoto()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(viewFinder.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture)
            } catch (e: Exception) {
                Toast.makeText(this, "Camera start failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val photoFile = File(externalCacheDir, "temp_plant_${System.currentTimeMillis()}.jpg")
        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    processCapturedImage(photoFile)
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@ScanActivity, "Capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    private fun processCapturedImage(file: File) {
        try {
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            
            // Run inference
            val prediction = classifyImage(bitmap)
            
            // Convert to bytes for fragment
            val imageBytes = BitmapConverter.bitmapToByteArray(bitmap)
            
            // Show preview fragment
            showPreview(imageBytes, prediction)

        } catch (e: Exception) {
            Toast.makeText(this, "Processing error: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun classifyImage(bitmap: Bitmap): String {
        if (model == null) return "Unknown"

        // Pre-process Image (assuming 224x224 input size common for these models)
        val imageProcessor = ImageProcessor.Builder()
            .add(ResizeOp(224, 224, ResizeOp.ResizeMethod.BILINEAR))
            .build()

        var tensorImage = TensorImage(DataType.FLOAT32)
        tensorImage.load(bitmap)
        tensorImage = imageProcessor.process(tensorImage)

        // Run Inference
        val outputs = model!!.process(tensorImage.tensorBuffer)
        val outputBuffer = outputs.getOutputFeature0AsTensorBuffer()

        // Find best match
        val probs = outputBuffer.floatArray
        var maxIndex = 0
        var maxProb = 0.0f
        for (i in probs.indices) {
            if (probs[i] > maxProb) {
                maxProb = probs[i]
                maxIndex = i
            }
        }

        return if (maxIndex < labels.size) labels[maxIndex] else "Unknown"
    }

    private fun showPreview(imageBytes: ByteArray, plantName: String) {
        scanContainer.visibility = View.GONE
        fragmentContainer.visibility = View.VISIBLE
        
        val fragment = ScanPreviewFragment.newInstance(imageBytes, plantName)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    fun showCamera() {
        scanContainer.visibility = View.VISIBLE
        fragmentContainer.visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        model?.close()
    }
}
