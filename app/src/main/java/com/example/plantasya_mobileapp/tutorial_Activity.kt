package com.example.plantasya_mobileapp

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ViewFlipper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlin.math.abs

class tutorial_Activity : AppCompatActivity() {

    private lateinit var viewFlipper: ViewFlipper
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tutorial)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        viewFlipper = findViewById(R.id.viewFlipper)

        // Initialize GestureDetector to handle swipes
        gestureDetector = GestureDetector(this, SwipeGestureListener())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event) || super.onTouchEvent(event)
    }

    private inner class SwipeGestureListener : GestureDetector.SimpleOnGestureListener() {
        private val SWIPE_THRESHOLD = 100
        private val SWIPE_VELOCITY_THRESHOLD = 100

        override fun onFling(
            e1: MotionEvent?,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {
            if (e1 == null) return false
            val diffX = e2.x - e1.x
            val diffY = e2.y - e1.y

            if (abs(diffX) > abs(diffY)) {
                if (abs(diffX) > SWIPE_THRESHOLD && abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe Right (Previous)
                        if (viewFlipper.displayedChild > 0) {
                            viewFlipper.setInAnimation(this@tutorial_Activity, android.R.anim.slide_in_left)
                            viewFlipper.setOutAnimation(this@tutorial_Activity, android.R.anim.slide_out_right)
                            viewFlipper.showPrevious()
                        }
                    } else {
                        // Swipe Left (Next)
                        if (viewFlipper.displayedChild < viewFlipper.childCount - 1) {
                            viewFlipper.setInAnimation(this@tutorial_Activity, R.anim.slide_in_right)
                            viewFlipper.setOutAnimation(this@tutorial_Activity, R.anim.slide_out_left)
                            viewFlipper.showNext()
                        } else {
                            // End of tutorial - navigate to MainActivity showing SettingsFrag
                            navigateToSettings()
                        }
                    }
                    return true
                }
            }
            return false
        }
    }

    private fun navigateToSettings() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("SHOW_SETTINGS", true)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()
    }
}
