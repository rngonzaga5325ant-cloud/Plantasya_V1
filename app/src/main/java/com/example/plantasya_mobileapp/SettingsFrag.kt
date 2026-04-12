package com.example.plantasya_mobileapp

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

class SettingsFrag : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_settings, container, false)

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
