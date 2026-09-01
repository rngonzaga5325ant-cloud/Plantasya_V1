package com.example.gltfviewer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class SampleViewerActivity : AppCompatActivity() {

    private lateinit var surfaceView: GltfSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        surfaceView = GltfSurfaceView(this)
        setContentView(surfaceView)

        // Example: if you add a model into this module's assets folder, load it like:
        // surfaceView.loadModelFromAssets("Models/Duck.gltf")
    }

    override fun onResume() {
        super.onResume()
        surfaceView.onResume()
    }

    override fun onPause() {
        surfaceView.onPause()
        super.onPause()
    }
}
