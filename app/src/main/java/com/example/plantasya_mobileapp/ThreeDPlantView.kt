package com.example.plantasya_mobileapp

import android.os.Bundle
import android.view.Choreographer
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.WindowManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.filament.EntityManager
import com.google.android.filament.LightManager
import com.google.android.filament.Skybox
import com.google.android.filament.utils.ModelViewer
import com.google.android.filament.utils.Utils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer

class ThreeDPlantView : AppCompatActivity(), Choreographer.FrameCallback {

    companion object {
        init {
            Utils.init()
        }
    }

    private lateinit var surfaceView: SurfaceView
    private lateinit var choreographer: Choreographer
    private lateinit var modelViewer: ModelViewer
    
    // Light entities
    private val lightEntities = mutableListOf<Int>()
    private var loadJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContentView(R.layout.activity_3d_viewer)
        surfaceView = findViewById(R.id.modelSurfaceView)
        surfaceView.alpha = 0f

        val guideOverlay = findViewById<android.view.View>(R.id.guideOverlay)
        val dismissGuide = {
            guideOverlay.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction { guideOverlay.visibility = android.view.View.GONE }
                .start()
        }
        
        findViewById<Button>(R.id.btnGotIt).setOnClickListener { dismissGuide() }
        guideOverlay.setOnClickListener { dismissGuide() }

        modelViewer = ModelViewer(surfaceView)

        surfaceView.setOnTouchListener { v, event ->
            modelViewer.onTouchEvent(event)
            if (event.action == MotionEvent.ACTION_UP) {
                v.performClick()
            }
            true
        }

        choreographer = Choreographer.getInstance()

        // Set background to pure white
        modelViewer.view.blendMode = com.google.android.filament.View.BlendMode.OPAQUE
        modelViewer.scene.skybox = Skybox.Builder()
            .color(1.0f, 1.0f, 1.0f, 1.0f)
            .build(modelViewer.engine)

        setupStudioLighting()

        val plantName = intent.getStringExtra("PLANT_NAME") ?: ""
        if (plantName.isNotEmpty()) {
            loadModelAsync(plantName)
        }
    }

    private fun setupStudioLighting() {
        val engine = modelViewer.engine
        val scene = modelViewer.scene
        val entityManager = EntityManager.get()

        // 10-Point "Omni-Directional" Studio Lighting
        // This wraps the model in light from every primary axis and diagonal corner
        // to ensure zero dark spots regardless of rotation.
        val lights = listOf(
            // Primary Axes
            floatArrayOf(0.0f, -1.0f, 0.0f) to 100000.0f,  // Top
            floatArrayOf(0.0f, 1.0f, 0.0f) to 60000.0f,   // Bottom
            floatArrayOf(0.0f, 0.0f, -1.0f) to 120000.0f, // Front
            floatArrayOf(0.0f, 0.0f, 1.0f) to 60000.0f,   // Back
            floatArrayOf(1.0f, 0.0f, 0.0f) to 80000.0f,   // Left
            floatArrayOf(-1.0f, 0.0f, 0.0f) to 80000.0f,  // Right
            
            // Diagonals for corner filling
            floatArrayOf(0.7f, -0.7f, -0.7f) to 50000.0f,  // Front-Top-Left
            floatArrayOf(-0.7f, -0.7f, -0.7f) to 50000.0f, // Front-Top-Right
            floatArrayOf(0.7f, 0.7f, 0.7f) to 40000.0f,    // Back-Bottom-Left
            floatArrayOf(-0.7f, 0.7f, 0.7f) to 40000.0f    // Back-Bottom-Right
        )

        lights.forEach { (dir, intensity) ->
            val light = entityManager.create()
            LightManager.Builder(LightManager.Type.DIRECTIONAL)
                .color(1.0f, 1.0f, 1.0f)
                .intensity(intensity)
                .direction(dir[0], dir[1], dir[2])
                .castShadows(false)
                .build(engine, light)
            scene.addEntity(light)
            lightEntities.add(light)
        }
    }

    private fun loadModelAsync(plantName: String) {
        // Ensure only one model loads at a time to reduce lag and memory usage
        loadJob?.cancel()
        loadJob = lifecycleScope.launch {
            val modelName = plantName.trim().lowercase().replace(" ", "_") + ".glb"
            try {
                val buffer = withContext(Dispatchers.IO) {
                    readAsset("models/$modelName")
                }
                
                // Clear any existing model resources before loading a new one
                // ModelViewer.loadModelGlb replaces the scene content
                modelViewer.loadModelGlb(buffer)
                modelViewer.transformToUnitCube()
                
                // Final check to make sure the view is ready
                withContext(Dispatchers.Main) {
                    surfaceView.animate().alpha(1.0f).setDuration(500).start()
                }
                
            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ThreeDPlantView, "Model for $plantName not found", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }

    private fun readAsset(assetName: String): ByteBuffer {
        val inputStream = assets.open(assetName)
        val bytes = ByteArray(inputStream.available())
        inputStream.read(bytes)
        return ByteBuffer.wrap(bytes)
    }

    override fun doFrame(frameTimeNanos: Long) {
        choreographer.postFrameCallback(this)
        modelViewer.render(frameTimeNanos)
    }

    override fun onResume() {
        super.onResume()
        choreographer.postFrameCallback(this)
    }

    override fun onPause() {
        super.onPause()
        choreographer.removeFrameCallback(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        choreographer.removeFrameCallback(this)
        
        // Cleanup Filament resources
        val engine = modelViewer.engine
        modelViewer.scene.skybox?.let { engine.destroySkybox(it) }
        
        // Destroy all studio lights
        lightEntities.forEach { engine.destroyEntity(it) }
        lightEntities.clear()
        
        loadJob?.cancel()
    }
}
