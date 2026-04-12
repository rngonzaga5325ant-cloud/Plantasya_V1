package com.example.plantasya_mobileapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment

class MainActivity : AppCompatActivity() {

    private lateinit var btnHome: ImageButton
    private lateinit var btnLib: ImageButton
    private lateinit var btnHistory: ImageButton
    private lateinit var btnUser: ImageButton
    private lateinit var btnScan: ImageButton

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(this, "Camera permission is required to scan plants", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // Hide the navigation bar automatically (Immersive Mode)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize buttons
        btnHome = findViewById(R.id.btn_home)
        btnLib = findViewById(R.id.btn_lib)
        btnHistory = findViewById(R.id.btn_history)
        btnUser = findViewById(R.id.btn_user)
        btnScan = findViewById(R.id.btnScan)

        // Set DashboardFrag as the default appearing fragment and highlight Home button
        if (intent.getBooleanExtra("SHOW_SETTINGS", false)) {
            replaceFragment(SettingsFrag(), btnUser.id)
        } else {
            replaceFragment(DashboardFrag(), btnHome.id)
        }

        // Set OnClickListeners for each button
        btnHome.setOnClickListener {
            replaceFragment(DashboardFrag(), it.id)
        }

        btnLib.setOnClickListener {
            replaceFragment(LibraryFrag(), it.id)
        }

        btnHistory.setOnClickListener {
            replaceFragment(HistoryFrag(), it.id)
        }

        btnUser.setOnClickListener {
            replaceFragment(SettingsFrag(), it.id)
        }

        btnScan.setOnClickListener {
            checkCameraPermission()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra("SHOW_SETTINGS", false)) {
            replaceFragment(SettingsFrag(), btnUser.id)
        }
    }

    /**
     * Updates the appearance of navigation buttons based on selection.
     * Selected: Background = btn_c, Icon = white
     * Neutral/Unselected: Background = white, Icon = btn_c
     */
    private fun updateNavColors(selectedId: Int) {
        val navButtons = listOf(btnHome, btnLib, btnHistory, btnUser)
        
        val colorBtnC = ContextCompat.getColor(this, R.color.btn_c)
        val colorWhite = ContextCompat.getColor(this, R.color.white)

        navButtons.forEach { button ->
            if (button.id == selectedId) {
                // Selected state
                button.backgroundTintList = ColorStateList.valueOf(colorBtnC)
                button.imageTintList = ColorStateList.valueOf(colorWhite)
            } else {
                // Neutral state
                button.backgroundTintList = ColorStateList.valueOf(colorWhite)
                button.imageTintList = ColorStateList.valueOf(colorBtnC)
            }
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(this, ScanActivity::class.java)
        startActivity(intent)
    }

    private fun replaceFragment(fragment: Fragment, buttonId: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.FrameHandler, fragment)
        fragmentTransaction.commit()
        
        // Update navigation button colors
        updateNavColors(buttonId)
    }
}
