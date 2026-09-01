package com.example.plantasya_mobileapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.plantasya_mobileapp.database.UserViewModel
import kotlinx.coroutines.launch

class ProfileFrag : Fragment() {

    private lateinit var sessionManager: SessionManager
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        
        sessionManager = SessionManager(requireContext())

        // Update Account Name and PFP
        val accountName = view.findViewById<TextView>(R.id.accountName)
        val imageView4 = view.findViewById<ImageView>(R.id.imageView4)

        val userId = sessionManager.getUserId()
        if (userId != -1) {
            lifecycleScope.launch {
                val user = userViewModel.getUserById(userId)
                user?.let {
                    accountName.text = it.displayName ?: it.username
                    it.profilePic?.let { pic ->
                        val bitmap = BitmapConverter.byteArrayToBitmap(pic)
                        imageView4.setImageBitmap(bitmap)
                    }
                }
            }
        } else {
            accountName.text = "Guest"
        }

        val btnSettings = view.findViewById<Button>(R.id.btnSettings)
        btnSettings.setOnClickListener {
            // Navigate to SettingsFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.FrameHandler, SettingsFragment())
                .addToBackStack(null)
                .commit()
        }

        val btnTutorial = view.findViewById<Button>(R.id.btnTutorial)
        btnTutorial.setOnClickListener {
            openTutorial()
        }

        val btnAboutApp = view.findViewById<Button>(R.id.btnAboutApp)
        btnAboutApp.setOnClickListener {
            showAboutDialog()
        }
        return view
    }

    private fun showAboutDialog() {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext(), R.style.CustomDialog)
        builder.setTitle("About Plantasya")
        builder.setMessage("Version: 9.1.26 (Build 1.0.0)\nDate Completed: February 20, 2025")
        builder.setPositiveButton("Close") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun openTutorial() {
        val intent = Intent(requireContext(), tutorial_Activity::class.java)
        startActivity(intent)
    }
}
