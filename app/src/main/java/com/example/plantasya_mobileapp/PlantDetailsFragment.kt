package com.example.plantasya_mobileapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.plantasya_mobileapp.database.AppDatabase
import com.example.plantasya_mobileapp.database.OwnedPlant
import com.example.plantasya_mobileapp.database.Task
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
        val ivPlant = view.findViewById<ImageView>(R.id.ivPlantDetail)

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnOwned.setOnClickListener {
            toggleOwnedStatus(btnOwned)
        }

        loadPlantDetails(tvName, tvSciName, tvDescription, tvLight, tvWater, tvHumidity, tvTemp, tvFertilizer, tvToxicity, ivPlant, btnOwned)

        // Hide Scan Button in MainActivity
        (activity as? MainActivity)?.setScanButtonVisibility(View.GONE)

        return view
    }

    private fun loadPlantDetails(
        tvName: TextView, tvSciName: TextView, tvDesc: TextView,
        tvLight: TextView, tvWater: TextView, tvHum: TextView,
        tvTemp: TextView, tvFert: TextView, tvTox: TextView,
        ivPlant: ImageView, btnOwned: ImageButton
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
            
            plant?.let {
                val existingOwned = db.ownedPlantDao().getOwnedPlantByLibraryIdAndUser(plantId, userId)
                val isCurrentlyOwned = existingOwned != null
                val newOwnedStatus = !isCurrentlyOwned
                
                if (newOwnedStatus) {
                    // Update Button UI
                    btnOwned.setImageResource(R.drawable.ic_owned_plant_filled)
                    
                    // Add to Owned Plants Table with current userId
                    val ownedPlant = OwnedPlant(
                        userId = userId,
                        libraryId = it.idLib,
                        plantName = it.plantName,
                        scientificName = it.scientificName,
                        plantPic = it.coverPhoto,
                        owned = true
                    )
                    db.ownedPlantDao().insert(ownedPlant)
                    
                    // Fetch the inserted owned plant to get its generated ID
                    val inserted = db.ownedPlantDao().getOwnedPlantByLibraryIdAndUser(it.idLib, userId)
                    inserted?.let { owned ->
                        // Generate Tasks
                        generateTasksForPlant(owned.idOwned, it.plantName ?: "")
                    }
                    
                    Toast.makeText(context, "${it.plantName} added to your dashboard", Toast.LENGTH_SHORT).show()
                } else {
                    // Update Button UI
                    btnOwned.setImageResource(R.drawable.ic_owned_plant)
                    
                    // Remove from Owned Plants (Tasks will cascade delete)
                    db.ownedPlantDao().deleteByLibraryIdAndUser(plantId, userId)
                    Toast.makeText(context, "${it.plantName} removed from your dashboard", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private suspend fun generateTasksForPlant(ownedId: Int, plantName: String) {
        val db = AppDatabase.getDatabase(requireContext())
        val tasks = when (plantName) {
            "Aglaonema" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Every 7-10 days"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Monthly (Spring/Summer)"),
                Task(plantId = ownedId, taskName = "Leaf Cleaning", taskFrequency = "Every 2 weeks")
            )
            "Snake Plant" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Every 21-30 days"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Monthly (Spring/Summer)"),
                Task(plantId = ownedId, taskName = "Check soil moisture", taskFrequency = "Every 2 weeks")
            )
            "Philodendron" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Every 7 days"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Monthly (Spring/Summer)"),
                Task(plantId = ownedId, taskName = "Mist leaves", taskFrequency = "Every 3 days")
            )
            "Calathea" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Every 5-7 days"),
                Task(plantId = ownedId, taskName = "Humidity check", taskFrequency = "Daily"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Monthly (Spring/Summer)")
            )
            "Bromeliad" -> listOf(
                Task(plantId = ownedId, taskName = "Water central cup", taskFrequency = "Weekly"),
                Task(plantId = ownedId, taskName = "Foliar Fertilizing", taskFrequency = "Monthly (Spring/Summer)"),
                Task(plantId = ownedId, taskName = "Flush soil", taskFrequency = "Monthly")
            )
            "Peace Lily" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Every 5-7 days"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "3 times per year"),
                Task(plantId = ownedId, taskName = "Wipe leaves", taskFrequency = "Every 2 weeks")
            )
            "Rubber Tree" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Every 7-14 days"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Every 2-4 weeks"),
                Task(plantId = ownedId, taskName = "Rotate plant", taskFrequency = "Monthly")
            )
            "Fiddle Leaf Fig" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Weekly"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Every 2-4 weeks"),
                Task(plantId = ownedId, taskName = "Clean leaves", taskFrequency = "Weekly")
            )
            "Orchid" -> listOf(
                Task(plantId = ownedId, taskName = "Watering (Soak roots)", taskFrequency = "Every 7 days"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Every 4-6 weeks"),
                Task(plantId = ownedId, taskName = "Check bark moisture", taskFrequency = "Twice weekly")
            )
            "Spider Plant" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Every 7 days"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Monthly (Spring/Summer)"),
                Task(plantId = ownedId, taskName = "Prune spiderettes", taskFrequency = "As needed")
            )
            else -> emptyList()
        }

        tasks.forEach { db.taskDao().insert(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Show Scan Button when leaving
        (activity as? MainActivity)?.setScanButtonVisibility(View.VISIBLE)
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
