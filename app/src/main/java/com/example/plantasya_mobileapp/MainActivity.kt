package com.example.plantasya_mobileapp

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
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
    private lateinit var sessionManager: SessionManager

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
        
        sessionManager = SessionManager(this)
        checkSession()

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
            replaceFragment(ProfileFrag(), btnUser.id)
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
            replaceFragment(ProfileFrag(), it.id)
        }

        btnScan.setOnClickListener {
            checkCameraPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        checkSession()
    }

    private fun checkSession() {
        if (!sessionManager.isSessionValid()) {
            Toast.makeText(this, "Session expired. Please log in again.", Toast.LENGTH_LONG).show()
            sessionManager.clearSession()
            val intent = Intent(this, LandPage_Activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra("SHOW_SETTINGS", false)) {
            replaceFragment(ProfileFrag(), btnUser.id)
        }
    }

    private fun updateNavColors(selectedId: Int) {
        val navButtons = listOf(btnHome, btnLib, btnHistory, btnUser)
        
        val colorBtnC = ContextCompat.getColor(this, R.color.btn_c)
        val colorWhite = ContextCompat.getColor(this, R.color.white)

        navButtons.forEach { button ->
            if (button.id == selectedId) {
                button.backgroundTintList = ColorStateList.valueOf(colorBtnC)
                button.imageTintList = ColorStateList.valueOf(colorWhite)
            } else {
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

    fun replaceFragment(fragment: Fragment, buttonId: Int) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        
        // Add slide up/down animation for PlantDetailsFragment
        if (fragment is PlantDetailsFragment) {
            fragmentTransaction.setCustomAnimations(
                R.anim.slide_up,
                R.anim.slide_down,
                R.anim.slide_up,
                R.anim.slide_down
            )
            fragmentTransaction.addToBackStack(null)
        }
        
        fragmentTransaction.replace(R.id.FrameHandler, fragment)
        fragmentTransaction.commit()
        if (buttonId != -1) updateNavColors(buttonId)
    }

    fun setScanButtonVisibility(visibility: Int) {
        btnScan.visibility = visibility
    }
}
