package com.example.plantasya_mobileapp

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantasya_mobileapp.database.LibraryPlant
import com.example.plantasya_mobileapp.database.LibraryViewModel
import java.util.Locale

sealed class LibraryItem {
    data class Header(val letter: String) : LibraryItem()
    data class PlantItem(val plant: LibraryPlant) : LibraryItem()
}

class LibraryFrag : Fragment() {

    private lateinit var rvPlants: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var adapter: LibraryAdapter
    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        rvPlants = view.findViewById(R.id.rvPlants)
        etSearch = view.findViewById(R.id.etSearch)
        
        setupRecyclerView()
        setupSearch()
        observeViewModel()
        
        return view
    }

    private fun setupRecyclerView() {
        adapter = LibraryAdapter(mutableListOf()) { plant ->
            openPlantDetails(plant)
        }
        rvPlants.layoutManager = LinearLayoutManager(context)
        rvPlants.adapter = adapter
    }

    private fun openPlantDetails(plant: LibraryPlant) {
        val fragment = PlantDetailsFragment.newInstance(plant.idLib)
        (activity as? MainActivity)?.replaceFragment(fragment, -1)
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setSearchQuery(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun observeViewModel() {
        viewModel.plants.observe(viewLifecycleOwner) { plants ->
            val libraryItems = mutableListOf<LibraryItem>()
            var currentLetter = ""

            for (plant in plants) {
                val name = plant.plantName ?: "Unknown"
                val firstLetter = name.firstOrNull()?.uppercaseChar()?.toString() ?: "#"
                if (firstLetter != currentLetter) {
                    currentLetter = firstLetter
                    libraryItems.add(LibraryItem.Header(currentLetter))
                }
                libraryItems.add(LibraryItem.PlantItem(plant))
            }

            adapter.updateItems(libraryItems)
        }
    }

    class LibraryAdapter(
        private var items: List<LibraryItem>,
        private val onPlantClick: (LibraryPlant) -> Unit
    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        companion object {
            private const val TYPE_HEADER = 0
            private const val TYPE_PLANT = 1
        }

        fun updateItems(newItems: List<LibraryItem>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun getItemViewType(position: Int): Int {
            return when (items[position]) {
                is LibraryItem.Header -> TYPE_HEADER
                is LibraryItem.PlantItem -> TYPE_PLANT
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return if (viewType == TYPE_HEADER) {
                HeaderViewHolder(inflater.inflate(R.layout.item_plant_header, parent, false))
            } else {
                PlantViewHolder(inflater.inflate(R.layout.item_plant, parent, false), onPlantClick)
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when (val item = items[position]) {
                is LibraryItem.Header -> (holder as HeaderViewHolder).bind(item.letter)
                is LibraryItem.PlantItem -> (holder as PlantViewHolder).bind(item.plant)
            }
        }

        override fun getItemCount(): Int = items.size

        class HeaderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val tvHeader: TextView = view.findViewById(R.id.tvHeader)
            fun bind(letter: String) {
                tvHeader.text = letter
            }
        }

        class PlantViewHolder(
            view: View,
            private val onPlantClick: (LibraryPlant) -> Unit
        ) : RecyclerView.ViewHolder(view) {
            private val ivPlant: ImageView = view.findViewById(R.id.ivPlant)
            private val tvPlantName: TextView = view.findViewById(R.id.tvPlantName)
            private val tvScientificName: TextView = view.findViewById(R.id.tvScientificName)

            fun bind(plant: LibraryPlant) {
                tvPlantName.text = plant.plantName
                tvScientificName.text = plant.scientificName
                
                // Load cover photo from database
                plant.coverPhoto?.let { bytes ->
                    val bitmap = BitmapConverter.byteArrayToBitmap(bytes)
                    ivPlant.setImageBitmap(bitmap)
                    ivPlant.visibility = View.VISIBLE
                } ?: run {
                    ivPlant.visibility = View.GONE
                }
                
                itemView.setOnClickListener {
                    onPlantClick(plant)
                }
            }
        }
    }
}
