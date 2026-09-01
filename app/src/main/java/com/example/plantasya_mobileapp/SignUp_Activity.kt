package com.example.plantasya_mobileapp

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import android.view.View
import android.widget.FrameLayout
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.plantasya_mobileapp.database.User
import com.example.plantasya_mobileapp.database.UserViewModel
import kotlinx.coroutines.launch
import java.security.MessageDigest

class SignUp_Activity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup_page)

        // Hide the system bars automatically (Immersive Mode)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)
        val signupContent = findViewById<ConstraintLayout>(R.id.signup_content)
        val fragmentContainer = findViewById<FrameLayout>(R.id.fragment_container)
        val cbShowPassword = findViewById<CheckBox>(R.id.cbShowPassword)

        cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                etPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                etConfirmPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                etPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                etConfirmPassword.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            etPassword.setSelection(etPassword.text.length)
            etConfirmPassword.setSelection(etConfirmPassword.text.length)
        }

        btnSignUp.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // Check if username already exists using UserViewModel
                val existingUser = userViewModel.getUserByUsername(username)
                if (existingUser != null) {
                    Toast.makeText(this@SignUp_Activity, "Username already taken", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Hash the password
                val hashedPassword = hashPassword(password)

                // Instead of creating user, go to Profile Setup
                signupContent.visibility = View.GONE
                fragmentContainer.visibility = View.VISIBLE
                
                val profileSetupFrag = ProfileSetupFrag.newInstance(username, hashedPassword)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, profileSetupFrag)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
