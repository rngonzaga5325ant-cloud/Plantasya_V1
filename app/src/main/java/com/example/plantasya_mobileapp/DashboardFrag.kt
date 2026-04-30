package com.example.plantasya_mobileapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.plantasya_mobileapp.database.AppDatabase
import com.example.plantasya_mobileapp.database.OwnedPlant
import kotlinx.coroutines.launch
import kotlin.math.abs

class DashboardFrag : Fragment() {

    private lateinit var taskAdapter: TaskAdapter
    private val viewModel: DashboardViewModel by activityViewModels()
    private lateinit var viewPager: ViewPager2
    private lateinit var rvTasks: RecyclerView
    private lateinit var lblOwnPlant: TextView
    private var currentOwnedPlants: List<OwnedPlant> = emptyList()
    private lateinit var sessionManager: SessionManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        sessionManager = SessionManager(requireContext())
        val userId = sessionManager.getUserId()
        viewModel.setUserId(userId)

        viewPager = view.findViewById(R.id.viewPagerCarousel)
        rvTasks = view.findViewById(R.id.rvTasks)
        lblOwnPlant = view.findViewById(R.id.lblOwnPlant)

        // Setup Carousel Visuals
        viewPager.offscreenPageLimit = 3
        viewPager.clipToPadding = false
        viewPager.clipChildren = false
        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(40))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
            page.alpha = 0.6f + r * 0.4f
        }
        viewPager.setPageTransformer(transformer)

        // Setup Task RecyclerView
        rvTasks.layoutManager = LinearLayoutManager(requireContext())
        taskAdapter = TaskAdapter(emptyList(), mutableListOf()) { position, isChecked ->
            updateTaskInDatabase(position, isChecked)
        }
        rvTasks.adapter = taskAdapter

        observeViewModel()

        // Listen for carousel changes to fetch specific tasks
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (currentOwnedPlants.isNotEmpty()) {
                    viewModel.selectPlant(currentOwnedPlants[position].idOwned)
                }
            }
        })

        return view
    }

    private fun updateTaskInDatabase(position: Int, isChecked: Boolean) {
        val currentTasks = viewModel.tasks.value
        if (currentTasks != null && position < currentTasks.size) {
            val task = currentTasks[position]
            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(requireContext())
                db.taskDao().update(task.copy(taskDone = isChecked))
            }
        }
    }

    private fun observeViewModel() {
        // Observe Owned Plants for Carousel
        viewModel.ownedPlants.observe(viewLifecycleOwner) { plants ->
            currentOwnedPlants = plants
            if (plants.isEmpty()) {
                lblOwnPlant.text = "NO OWNED PLANTS YET"
                viewPager.visibility = View.GONE
                taskAdapter.updateTasks(emptyList(), mutableListOf())
            } else {
                lblOwnPlant.text = "OWNED PLANTS:"
                viewPager.visibility = View.VISIBLE
                
                val images = plants.map { it.plantPic }
                val names = plants.map { it.plantName ?: "Unknown" }
                
                val carouselAdapter = CarouselAdapter(images, names)
                viewPager.adapter = carouselAdapter
                
                // Select the first plant's tasks by default
                viewModel.selectPlant(plants[0].idOwned)
            }
        }

        // Observe Tasks for selected plant
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            val taskNames = tasks.map { "${it.taskName} (${it.taskFrequency})" }
            val taskStates = tasks.map { it.taskDone }.toMutableList()
            taskAdapter.updateTasks(taskNames, taskStates)
        }
    }
}
