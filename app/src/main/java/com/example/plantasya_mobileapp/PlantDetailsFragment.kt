package com.example.plantasya_mobileapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.plantasya_mobileapp.database.AppDatabase
import com.example.plantasya_mobileapp.database.OwnedPlant
import kotlinx.coroutines.launch

class PlantDetailsFragment : Fragment() {

    private var plantId: Int = -1
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            plantId = it.getInt(ARG_PLANT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plant_details, container, false)

        sessionManager = SessionManager(requireContext())

        val btnBack = view.findViewById<ImageButton>(R.id.btnBack)
        val btnOwned = view.findViewById<ImageButton>(R.id.btnOwned)
        val tvName = view.findViewById<TextView>(R.id.tvPlantNameDetail)
        val tvSciName = view.findViewById<TextView>(R.id.tvScientificNameDetail)
        val tvDescription = view.findViewById<TextView>(R.id.tvDescriptionDetail)
        val tvLight = view.findViewById<TextView>(R.id.tvLightDetail)
        val tvWater = view.findViewById<TextView>(R.id.tvWaterDetail)
        val tvHumidity = view.findViewById<TextView>(R.id.tvHumidityDetail)
        val tvTemp = view.findViewById<TextView>(R.id.tvTemperatureDetail)
        val tvFertilizer = view.findViewById<TextView>(R.id.tvFertilizerDetail)
        val tvToxicity = view.findViewById<TextView>(R.id.tvToxicityDetail)
        val tvVariations = view.findViewById<TextView>(R.id.tvVariationsDetail)
        val ivPlant = view.findViewById<ImageView>(R.id.ivPlantDetail)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnOwned.setOnClickListener {
            toggleOwnedStatus(btnOwned)
        }

        loadPlantDetails(tvName, tvSciName, tvDescription, tvLight, tvWater, tvHumidity, tvTemp, tvFertilizer, tvToxicity, tvVariations, ivPlant, btnOwned)

        return view
    }

    private fun showLeavingAppDialog(variationName: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_leaving_app, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnContinue).setOnClickListener {
            dialog.dismiss()
            val query = Uri.encode(variationName)
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/search?q=$query"))
            startActivity(intent)
        }

        dialog.show()
    }

    private fun loadPlantDetails(
        tvName: TextView, tvSciName: TextView, tvDesc: TextView,
        tvLight: TextView, tvWater: TextView, tvHum: TextView,
        tvTemp: TextView, tvFert: TextView, tvTox: TextView,
        tvVars: TextView, ivPlant: ImageView, btnOwned: ImageButton
    ) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val plant = db.libraryPlantDao().getPlantById(plantId)
            val userId = sessionManager.getUserId()
            
            // Check if this plant is owned by the current user
            val ownedPlant = db.ownedPlantDao().getOwnedPlantByLibraryIdAndUser(plantId, userId)
            val isOwned = ownedPlant != null
            
            plant?.let {
                tvName.text = it.plantName?.uppercase() ?: "UNKNOWN"
                tvSciName.text = it.scientificName ?: ""
                tvDesc.text = it.description ?: "No description available."
                tvLight.text = it.lightPt ?: "N/A"
                tvWater.text = it.waterPt ?: "N/A"
                tvHum.text = it.humidityPlt ?: "N/A"
                tvTemp.text = it.tempPlt ?: "N/A"
                tvFert.text = it.fertilizerPlt ?: "N/A"
                tvTox.text = it.toxicityPlt ?: "N/A"
                
                val variations = it.plantVariation ?: "N/A"
                tvVars.text = variations
                
                if (variations != "N/A") {
                    tvVars.setOnClickListener {
                        showLeavingAppDialog(variations)
                    }
                }
                
                // Update Owned Button Appearance based on per-user ownership
                if (isOwned) {
                    btnOwned.setImageResource(R.drawable.ic_owned_plant_filled)
                } else {
                    btnOwned.setImageResource(R.drawable.ic_owned_plant)
                }

                // Load cover photo from database
                it.coverPhoto?.let { bytes ->
                    val bitmap = BitmapConverter.byteArrayToBitmap(bytes)
                    ivPlant.setImageBitmap(bitmap)
                    ivPlant.scaleType = ImageView.ScaleType.CENTER_CROP
                } ?: run {
                    ivPlant.setImageResource(android.R.color.darker_gray)
                }
            }
        }
    }

    private fun toggleOwnedStatus(btnOwned: ImageButton) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val userId = sessionManager.getUserId()
            val plant = db.libraryPlantDao().getPlantById(plantId)
            
            plant?.let { libraryPlant ->
                val existingOwned = db.ownedPlantDao().getOwnedPlantByLibraryIdAndUser(plantId, userId)
                
                if (existingOwned != null) {
                    showRemovePlantDialog(btnOwned, libraryPlant.plantName ?: "this plant")
                } else {
                    addPlantToOwned(btnOwned, libraryPlant, userId)
                }
            }
        }
    }

    private fun showRemovePlantDialog(btnOwned: ImageButton, plantName: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_remove_plant, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.tvRemoveMessage).text = 
            "Are you sure you want to remove $plantName from your dashboard? All related tasks will also be deleted."

        dialogView.findViewById<Button>(R.id.btnCancelRemove).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnConfirmRemove).setOnClickListener {
            dialog.dismiss()
            removePlantFromOwned(btnOwned, plantName)
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun addPlantToOwned(btnOwned: ImageButton, libraryPlant: com.example.plantasya_mobileapp.database.LibraryPlant, userId: Int) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            btnOwned.setImageResource(R.drawable.ic_owned_plant_filled)
            
            val ownedPlant = OwnedPlant(
                userId = userId,
                libraryId = libraryPlant.idLib,
                plantName = libraryPlant.plantName,
                scientificName = libraryPlant.scientificName,
                plantPic = libraryPlant.coverPhoto,
                owned = true
            )
            val newId = db.ownedPlantDao().insert(ownedPlant)
            
            TaskSyncManager.syncTasksForPlant(requireContext(), newId.toInt(), libraryPlant.plantName ?: "")
            Toast.makeText(context, "${libraryPlant.plantName} added to your dashboard", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removePlantFromOwned(btnOwned: ImageButton, plantName: String) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val userId = sessionManager.getUserId()
            btnOwned.setImageResource(R.drawable.ic_owned_plant)
            db.ownedPlantDao().deleteByLibraryIdAndUser(plantId, userId)
            Toast.makeText(context, "$plantName removed from your dashboard", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    companion object {
        private const val ARG_PLANT_ID = "plant_id"

        fun newInstance(plantId: Int) = PlantDetailsFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_PLANT_ID, plantId)
            }
        }
    }
}
