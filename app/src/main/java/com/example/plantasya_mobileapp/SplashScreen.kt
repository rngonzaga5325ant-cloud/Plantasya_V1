package com.example.plantasya_mobileapp

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        // The ID in your layout is actually plantasya_logo after my previous check
        val logo = findViewById<ImageView>(R.id.plantasya_load)
        val pulsate = AnimationUtils.loadAnimation(this, R.anim.pulsate)
        logo.startAnimation(pulsate)

        Handler(Looper.getMainLooper()).postDelayed({
            val sessionManager = SessionManager(this)
            val targetActivity = if (sessionManager.isSessionValid()) {
                MainActivity::class.java
            } else {
                LandPage_Activity::class.java
            }
            
            val intent = Intent(this, targetActivity)
            startActivity(intent)
            finish()
        }, 3000) // 3 second delay to allow for pulsing animation
    }
}