package com.example.plantasya_mobileapp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.plantasya_mobileapp.database.UserViewModel
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        
        sessionManager = SessionManager(requireContext())

        val btnChangeCredentials = view.findViewById<TextView>(R.id.btnChangeCredentials)
        val btnLogout = view.findViewById<TextView>(R.id.btnLogout)
        val btnDeleteAccount = view.findViewById<TextView>(R.id.btnDeleteAccount)

        btnChangeCredentials.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.FrameHandler, ChangePass())
                .addToBackStack(null)
                .commit()
        }
        
        btnLogout.setOnClickListener {
            showLogoutDialog()
        }

        btnDeleteAccount.setOnClickListener {
            showDeleteAccountDialog()
        }

        return view
    }

    private fun showLogoutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnYes = dialogView.findViewById<Button>(R.id.btnYes)
        val btnNo = dialogView.findViewById<Button>(R.id.btnNo)

        btnYes.setOnClickListener {
            val sessionId = sessionManager.getSessionId()
            if (sessionId != -1) {
                userViewModel.endSession(sessionId)
            }
            sessionManager.clearSession()
            val intent = Intent(requireContext(), LandPage_Activity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            dialog.dismiss()
        }

        btnNo.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteAccountDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete_account, null)
        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnConfirm = dialogView.findViewById<Button>(R.id.btnConfirmDelete)
        val btnCancel = dialogView.findViewById<Button>(R.id.btnCancelDelete)

        btnConfirm.setOnClickListener {
            val username = sessionManager.getUsername()
            if (username != null) {
                lifecycleScope.launch {
                    val user = userViewModel.getUserByUsername(username)
                    if (user != null) {
                        val sessionId = sessionManager.getSessionId()
                        if (sessionId != -1) {
                            userViewModel.endSession(sessionId)
                        }
                        userViewModel.deleteUser(user)
                        sessionManager.clearSession()
                        Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show()
                        val intent = Intent(requireContext(), LandPage_Activity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                    }
                }
            }
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
