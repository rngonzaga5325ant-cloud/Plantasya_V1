package com.example.plantasya_mobileapp

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity() {

    private lateinit var viewFinder: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_scan)
        
        viewFinder = findViewById(R.id.viewFinder)
        val captureButton = findViewById<ImageButton>(R.id.capture_button)
        val btnBack = findViewById<ImageButton>(R.id.btn_back)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        captureButton.setOnClickListener {
            takePhoto()
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startCamera()

        cameraExecutor = Executors.newSingleThreadExecutor()
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()
                .also {
                    it.surfaceProvider = viewFinder.surfaceProvider
                }

            imageCapture = ImageCapture.Builder()
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
                Toast.makeText(this, "Failed to open camera", Toast.LENGTH_SHORT).show()
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        imageCapture.takePicture(
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    val bitmap = imageProxyToBitmap(image)
                    image.close()
                    
                    if (bitmap != null) {
                        showPreview(bitmap)
                    }
                }

                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                }
            }
        )
    }

    private fun imageProxyToBitmap(image: ImageProxy): Bitmap? {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }

    private fun showPreview(bitmap: Bitmap) {
        val byteArray = BitmapConverter.bitmapToByteArray(bitmap)
        
        findViewById<View>(R.id.scan_container).visibility = View.GONE
        findViewById<View>(R.id.fragment_container).visibility = View.VISIBLE

        val previewFragment = ScanPreviewFragment.newInstance(byteArray)
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, previewFragment)
            .commit()
    }

    fun showCamera() {
        findViewById<View>(R.id.scan_container).visibility = View.VISIBLE
        findViewById<View>(R.id.fragment_container).visibility = View.GONE
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    companion object {
        private const val TAG = "Plantasya_ScanActivity"
    }
}
