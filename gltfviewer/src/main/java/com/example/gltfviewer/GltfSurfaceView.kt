package com.example.gltfviewer

import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector

class GltfSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private val renderer: GltfRenderer
    private val scaleDetector: ScaleGestureDetector

    private var previousX = 0f
    private var previousY = 0f

    init {
        // Prefer ES 3.0 but fall back to 2.0 if unavailable
        setEGLContextClientVersion(3)
        renderer = GltfRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY

        scaleDetector = ScaleGestureDetector(context, object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                renderer.onScale(detector.scaleFactor)
                return true
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)

        val pointerCount = event.pointerCount

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                previousX = event.getX(0)
                previousY = event.getY(0)
            }

            MotionEvent.ACTION_MOVE -> {
                if (!scaleDetector.isInProgress) {
                    if (pointerCount == 1) {
                        val x = event.x
                        val y = event.y
                        val dx = x - previousX
                        val dy = y - previousY
                        renderer.onRotate(dx, dy)
                        previousX = x
                        previousY = y
                    } else if (pointerCount >= 2) {
                        // Two-finger pan
                        val x = (0 until pointerCount).map { event.getX(it) }.average().toFloat()
                        val y = (0 until pointerCount).map { event.getY(it) }.average().toFloat()
                        val dx = x - previousX
                        val dy = y - previousY
                        renderer.onPan(dx, dy)
                        previousX = x
                        previousY = y
                    }
                }
            }
        }

        return true
    }

    /** Public API to load a glTF/GLB model from assets (path relative to assets/) */
    fun loadModelFromAssets(path: String) {
        renderer.loadModelFromAssets(path)
    }
}
