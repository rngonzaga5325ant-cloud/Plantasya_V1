package com.example.plantasya_mobileapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView

class ProfileFrag : Fragment() {

    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        
        sessionManager = SessionManager(requireContext())

        // Update Account Name
        val accountName = view.findViewById<TextView>(R.id.accountName)
        accountName.text = sessionManager.getUsername() ?: "Guest"

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
        return view
    }

    private fun openTutorial() {
        val intent = Intent(requireContext(), tutorial_Activity::class.java)
        startActivity(intent)
    }
}
