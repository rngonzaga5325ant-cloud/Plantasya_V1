package com.example.plantasya_mobileapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
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

        // Hide the navigation bar automatically (Immersive Mode)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etConfirmPassword = findViewById<EditText>(R.id.etConfirmPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

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

                // Create new user
                val newUser = User(
                    username = username,
                    password = hashedPassword,
                    role = "user"
                )

                userViewModel.insertUser(newUser)
                Toast.makeText(this@SignUp_Activity, "Account created successfully", Toast.LENGTH_SHORT).show()
                
                // Navigate back to login
                val intent = Intent(this@SignUp_Activity, Login_Activity::class.java)
                startActivity(intent)
                finish()
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
