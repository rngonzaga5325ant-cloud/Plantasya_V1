package com.example.plantasya_mobileapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.plantasya_mobileapp.database.UserViewModel
import kotlinx.coroutines.launch
import java.security.MessageDigest

class ChangePass : Fragment() {

    private val userViewModel: UserViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_changepass, container, false)
        
        sessionManager = SessionManager(requireContext())

        val etCurrentUsername = view.findViewById<EditText>(R.id.etCurrentUsername)
        val etNewUsername = view.findViewById<EditText>(R.id.etNewUsername)
        val etCurrentPassword = view.findViewById<EditText>(R.id.etCurrentPassword)
        val etNewPassword = view.findViewById<EditText>(R.id.etNewPassword)
        val btnSaveChanges = view.findViewById<Button>(R.id.btnSaveChanges)

        btnSaveChanges.setOnClickListener {
            val currentUsernameInput = etCurrentUsername.text.toString().trim()
            val newUsernameInput = etNewUsername.text.toString().trim()
            val currentPasswordInput = etCurrentPassword.text.toString().trim()
            val newPasswordInput = etNewPassword.text.toString().trim()

            if (currentUsernameInput.isEmpty() || currentPasswordInput.isEmpty() || 
                newUsernameInput.isEmpty() || newPasswordInput.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = userViewModel.getUserByUsername(sessionManager.getUsername() ?: "")
                
                if (user == null) {
                    Toast.makeText(requireContext(), "User session error", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                val hashedCurrentPassword = hashPassword(currentPasswordInput)
                
                // Validate current credentials
                if (user.username != currentUsernameInput || user.password != hashedCurrentPassword) {
                    Toast.makeText(requireContext(), "Current credentials incorrect", Toast.LENGTH_SHORT).show()
                    return@launch
                }

                // Check if new username is already taken by someone else
                if (newUsernameInput != user.username) {
                    val existing = userViewModel.getUserByUsername(newUsernameInput)
                    if (existing != null) {
                        Toast.makeText(requireContext(), "New username already taken", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                }

                // Update user details
                val updatedUser = user.copy(
                    username = newUsernameInput,
                    password = hashPassword(newPasswordInput)
                )

                userViewModel.updateUser(updatedUser)
                
                // Update session manager to keep user logged in with new name
                val newSessionId = userViewModel.startSession(user.user_id)
                sessionManager.startSession(user.user_id, newUsernameInput, newSessionId.toInt())
                
                Toast.makeText(requireContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show()
                
                // Navigate back
                parentFragmentManager.popBackStack()
            }
        }

        return view
    }

    private fun hashPassword(password: String): String {
        val bytes = password.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
