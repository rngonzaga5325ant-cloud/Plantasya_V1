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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

data class Plant(val name: String, val scientificName: String, val imageResId: Int)

sealed class LibraryItem {
    data class Header(val letter: String) : LibraryItem()
    data class PlantItem(val plant: Plant) : LibraryItem()
}

class LibraryFrag : Fragment() {

    private lateinit var rvPlants: RecyclerView
    private lateinit var etSearch: EditText
    private lateinit var adapter: LibraryAdapter
    private val allPlants = listOf(
        Plant("Beliz Rubber", "(Ficus elastica 'Belize')", R.drawable.beliz_rubber),
        Plant("Cast Iron", "(Aspidistra elatior)", R.drawable.cast_iron),
        Plant("Chinese Evergreen", "(Aglaonema)", R.drawable.chinese_evergreen),
        Plant("Fiddleleaf Fig", "(Ficus lyrata)", R.drawable.fiddleleaf_fig),
        Plant("Living Lace", "(Asplenium scolopendrium)", R.drawable.living_lace),
        Plant("Parlor Palm", "(Chamaedorea elegans)", R.drawable.parlor_palm),
        Plant("Philodendron", "(Philodendron)", R.drawable.philodendron),
        Plant("Pothos", "(Epipremnum aureum)", R.drawable.pothos),
        Plant("Tradescantia", "(Tradescantia)", R.drawable.tradescantia),
        Plant("ZZ Plant", "(Zamioculcas zamiifolia)", R.drawable.zz)
    ).sortedBy { it.name }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_library, container, false)
        rvPlants = view.findViewById(R.id.rvPlants)
        etSearch = view.findViewById(R.id.etSearch)
        
        setupRecyclerView()
        setupSearch()
        
        return view
    }

    private fun setupRecyclerView() {
        adapter = LibraryAdapter(mutableListOf())
        rvPlants.layoutManager = LinearLayoutManager(context)
        rvPlants.adapter = adapter
        updateFilteredList("")
    }

    private fun setupSearch() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateFilteredList(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun updateFilteredList(query: String) {
        val filteredPlants = if (query.isEmpty()) {
            allPlants
        } else {
            allPlants.filter { it.name.lowercase(Locale.ROOT).startsWith(query.lowercase(Locale.ROOT)) }
        }

        val libraryItems = mutableListOf<LibraryItem>()
        var currentLetter = ""

        for (plant in filteredPlants) {
            val firstLetter = plant.name.first().uppercaseChar().toString()
            if (firstLetter != currentLetter) {
                currentLetter = firstLetter
                libraryItems.add(LibraryItem.Header(currentLetter))
            }
            libraryItems.add(LibraryItem.PlantItem(plant))
        }

        adapter.updateItems(libraryItems)
    }

    class LibraryAdapter(private var items: List<LibraryItem>) :
        RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
                PlantViewHolder(inflater.inflate(R.layout.item_plant, parent, false))
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

        class PlantViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            private val ivPlant: ImageView = view.findViewById(R.id.ivPlant)
            private val tvPlantName: TextView = view.findViewById(R.id.tvPlantName)
            private val tvScientificName: TextView = view.findViewById(R.id.tvScientificName)

            fun bind(plant: Plant) {
                ivPlant.setImageResource(plant.imageResId)
                tvPlantName.text = plant.name
                tvScientificName.text = plant.scientificName
            }
        }
    }
}