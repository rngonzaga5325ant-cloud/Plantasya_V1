package com.example.plantasya_mobileapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.plantasya_mobileapp.database.AppDatabase
import com.example.plantasya_mobileapp.database.History
import com.example.plantasya_mobileapp.database.OwnedPlant
import kotlinx.coroutines.launch

class ScanPreviewFragment : Fragment() {

    private var imageBytes: ByteArray? = null
    private lateinit var sessionManager: SessionManager
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            imageBytes = it.getByteArray(ARG_IMAGE_BYTES)
        }
        sessionManager = SessionManager(requireContext())
        database = AppDatabase.getDatabase(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scan_preview, container, false)

        val imgPreview = view.findViewById<ImageView>(R.id.imgPreview)
        val etPlantName = view.findViewById<EditText>(R.id.etPlantName)
        val btnOwn = view.findViewById<Button>(R.id.btnOwn)
        val btnNotOwn = view.findViewById<Button>(R.id.btnNotOwn)
        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)

        imageBytes?.let {
            imgPreview.setImageBitmap(BitmapConverter.byteArrayToBitmap(it))
        }

        btnBack.setOnClickListener {
            (activity as? ScanActivity)?.showCamera()
            parentFragmentManager.beginTransaction().remove(this).commit()
        }

        btnOwn.setOnClickListener {
            savePlant(etPlantName.text.toString().trim(), true)
        }

        btnNotOwn.setOnClickListener {
            savePlant(etPlantName.text.toString().trim(), false)
        }

        return view
    }

    private fun savePlant(name: String, isOwned: Boolean) {
        val plantName = if (name.isEmpty()) "Unknown Plant" else name
        val userId = sessionManager.getUserId()

        if (userId == -1) {
            Toast.makeText(requireContext(), "User session error", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            // Save to History
            val history = History(
                userId = userId,
                plantPic = imageBytes,
                plantName = plantName,
                isOwned = isOwned,
                dateScanned = System.currentTimeMillis()
            )
            database.historyDao().insert(history)

            // Save to Owned if applicable
            if (isOwned) {
                val ownedPlant = OwnedPlant(
                    userId = userId,
                    plantPic = imageBytes,
                    plantName = plantName,
                    owned = true
                )
                val newPlantId = database.ownedPlantDao().insert(ownedPlant)
                
                // Sync tasks based on plant name/library
                TaskSyncManager.syncTasksForPlant(requireContext(), newPlantId.toInt(), plantName)

                Toast.makeText(requireContext(), "Plant added to your dashboard!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Scan saved to history", Toast.LENGTH_SHORT).show()
            }

            activity?.finish()
        }
    }

    companion object {
        private const val ARG_IMAGE_BYTES = "image_bytes"

        @JvmStatic
        fun newInstance(imageBytes: ByteArray) =
            ScanPreviewFragment().apply {
                arguments = Bundle().apply {
                    putByteArray(ARG_IMAGE_BYTES, imageBytes)
                }
            }
    }
}
