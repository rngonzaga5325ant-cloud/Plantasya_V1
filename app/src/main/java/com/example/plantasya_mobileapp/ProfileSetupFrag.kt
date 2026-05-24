package com.example.plantasya_mobileapp

import android.app.Activity
import android.app.AlertDialog
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.plantasya_mobileapp.database.User
import com.example.plantasya_mobileapp.database.UserViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.InputStream

class ProfileSetupFrag : Fragment() {

    private lateinit var btnProfilePic: ImageButton
    private lateinit var etDisplayName: EditText
    private lateinit var btnSaveProfile: Button
    
    private var selectedImageBytes: ByteArray? = null
    private val userViewModel: UserViewModel by viewModels()

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { uri ->
                handleImageSelection(uri)
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openGallery()
        } else {
            Toast.makeText(requireContext(), "Permission denied to access gallery", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile_setup, container, false)

        btnProfilePic = view.findViewById(R.id.btnProfilePic)
        etDisplayName = view.findViewById(R.id.etDisplayName)
        btnSaveProfile = view.findViewById(R.id.btnSaveProfile)

        btnProfilePic.setOnClickListener {
            checkPermissionAndOpenGallery()
        }

        btnSaveProfile.setOnClickListener {
            saveProfile()
        }

        return view
    }

    private fun checkPermissionAndOpenGallery() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) == PackageManager.PERMISSION_GRANTED -> {
                openGallery()
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "image/*"
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/jpeg", "image/png", "image/jpg"))
        }
        imagePickerLauncher.launch(intent)
    }

    private fun handleImageSelection(uri: Uri) {
        val contentResolver = requireContext().contentResolver
        
        // Check size and type
        val mimeType = contentResolver.getType(uri)
        if (mimeType !in listOf("image/jpeg", "image/png", "image/jpg")) {
            Toast.makeText(requireContext(), "Only PNG, JPEG, and JPG are allowed", Toast.LENGTH_SHORT).show()
            return
        }

        var fileSize: Long = 0
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
            if (cursor.moveToFirst()) {
                fileSize = cursor.getLong(sizeIndex)
            }
        }

        if (fileSize > 10 * 1024 * 1024) { // 10MB
            Toast.makeText(requireContext(), "Image size must be less than 10MB", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            btnProfilePic.setImageBitmap(bitmap)
            
            selectedImageBytes = BitmapConverter.bitmapToByteArray(bitmap)
            
        } catch (e: Exception) {
            Toast.makeText(requireContext(), "Failed to load image", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveProfile() {
        val displayName = etDisplayName.text.toString().trim()
        
        if (displayName.isEmpty()) {
            Toast.makeText(requireContext(), "Please enter a display name", Toast.LENGTH_SHORT).show()
            return
        }

        if (selectedImageBytes == null) {
            Toast.makeText(requireContext(), "Please select a profile picture", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch sign-up credentials from arguments
        val username = arguments?.getString("USERNAME")
        val password = arguments?.getString("PASSWORD")
        val role = arguments?.getString("ROLE") ?: "user"

        if (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            Toast.makeText(requireContext(), "Error: Missing sign-up credentials", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val newUser = User(
                username = username,
                password = password,
                role = role,
                displayName = displayName,
                profilePic = selectedImageBytes
            )

            userViewModel.insertUser(newUser)
            Toast.makeText(requireContext(), "Profile saved successfully!", Toast.LENGTH_SHORT).show()
            
            // Navigate back to login or wherever appropriate
            activity?.let {
                val intent = Intent(it, Login_Activity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                it.finish()
            }
        }
    }

    private fun showExitDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_exit_setup, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnCancelExit).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnConfirmExit).setOnClickListener {
            dialog.dismiss()
            activity?.let {
                val intent = Intent(it, LandPage_Activity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                it.finish()
            }
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    companion object {
        fun newInstance(username: String, password: String, role: String = "user"): ProfileSetupFrag {
            val fragment = ProfileSetupFrag()
            val args = Bundle()
            args.putString("USERNAME", username)
            args.putString("PASSWORD", password)
            args.putString("ROLE", role)
            fragment.arguments = args
            return fragment
        }
    }
}
