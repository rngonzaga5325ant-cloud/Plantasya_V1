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
import com.example.plantasya_mobileapp.database.UserViewModel
import kotlinx.coroutines.launch
import java.security.MessageDigest

class Login_Activity : AppCompatActivity() {

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sessionManager = SessionManager(this)
        
        if (sessionManager.isSessionValid()) {
            startMainActivity()
            return
        }

        enableEdgeToEdge()
        setContentView(R.layout.activity_login_page)

        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        val etUsername = findViewById<EditText>(R.id.etUsername)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = userViewModel.getUserByUsername(username)
                val hashedPassword = hashPassword(password)
                
                if (user != null && user.password == hashedPassword) {
                    // Record login in database
                    val sessionId = userViewModel.startSession(user.user_id)
                    
                    // Start session in manager
                    sessionManager.startSession(user.user_id, user.username, sessionId.toInt())

                    Toast.makeText(this@Login_Activity, "Login Successful", Toast.LENGTH_SHORT).show()
                    startMainActivity()
                } else {
                    Toast.makeText(this@Login_Activity, "Invalid Username or Password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
