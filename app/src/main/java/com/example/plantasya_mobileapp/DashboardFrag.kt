package com.example.plantasya_mobileapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import kotlin.math.abs

class DashboardFrag : Fragment() {

    private lateinit var taskAdapter: TaskAdapter
    private val viewModel: DashboardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val viewPager = view.findViewById<ViewPager2>(R.id.viewPagerCarousel)
        val rvTasks = view.findViewById<RecyclerView>(R.id.rvTasks)

        val images = listOf(
            R.drawable.fiddleleaf_fig,
            R.drawable.parlor_palm,
            R.drawable.philodendron,
            R.drawable.pothos,
            R.drawable.zz
        )

        val names = listOf(
            "Fiddle-leaf Fig",
            "Parlor Palm",
            "Philodendron",
            "Pothos",
            "ZZ Plant"
        )

        val carouselAdapter = CarouselAdapter(images, names)
        viewPager.adapter = carouselAdapter

        // Setup Task RecyclerView
        rvTasks.layoutManager = LinearLayoutManager(context)
        taskAdapter = TaskAdapter(emptyList(), mutableListOf()) { position, isChecked ->
            val currentPlant = names[viewPager.currentItem]
            viewModel.updateTaskState(currentPlant, position, isChecked)
        }
        rvTasks.adapter = taskAdapter

        // Listen for carousel changes
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val selectedPlant = names[position]
                updateTasksForPlant(selectedPlant)
            }
        })

        // Carousel visual effects
        viewPager.offscreenPageLimit = 3
        viewPager.clipToPadding = false
        viewPager.clipChildren = false

        val transformer = CompositePageTransformer()
        transformer.addTransformer(MarginPageTransformer(5))
        transformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = 0.85f + r * 0.15f
            page.alpha = 0.6f + r * 0.4f
        }
        viewPager.setPageTransformer(transformer)

        return view
    }

    private fun updateTasksForPlant(plantName: String) {
        val tasks = viewModel.plantTasks[plantName] ?: emptyList()
        val states = viewModel.getTaskStatesForPlant(plantName)
        taskAdapter.updateTasks(tasks, states)
    }
}
