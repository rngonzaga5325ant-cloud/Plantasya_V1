package com.example.plantasya_mobileapp

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.plantasya_mobileapp.database.AppDatabase
import com.example.plantasya_mobileapp.database.OwnedPlant
import com.example.plantasya_mobileapp.database.Task
import com.example.plantasya_mobileapp.database.TaskWithPlantName
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.math.abs

class DashboardFrag : Fragment() {

    private lateinit var taskAdapter: TaskAdapter
    private val viewModel: DashboardViewModel by activityViewModels()
    private lateinit var viewPager: ViewPager2
    private lateinit var rvTasks: RecyclerView
    private lateinit var lblOwnPlant: TextView
    private lateinit var layoutEmptyState: View
    private lateinit var cardTasks: View
    private lateinit var btnAddPlant: View
    private var currentOwnedPlants: List<OwnedPlant> = emptyList()
    private lateinit var sessionManager: SessionManager
    private var hasShownCompletedDialogThisSession = false

    companion object {
        private const val OVERDUE_THRESHOLD_MILLIS = 24 * 60 * 60 * 1000L // 24 hours
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            openCamera()
        } else {
            Toast.makeText(requireContext(), "Camera permission is required to scan plants", Toast.LENGTH_SHORT).show()
        }
    }

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
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState)
        cardTasks = view.findViewById(R.id.cardTasks)
        btnAddPlant = view.findViewById(R.id.btnAddPlant)

        btnAddPlant.setOnClickListener {
            showAddPlantOptions()
        }

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
        taskAdapter = TaskAdapter(emptyList(), { task, isChecked ->
            updateTaskInDatabase(task, isChecked)
        }, { task ->
            showDateTimePicker(task)
        })
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

        // Check for directives or pending dialogs
        val showOverdueDialog = arguments?.getBoolean("SHOW_OVERDUE_DIALOG", false) ?: false
        checkAndShowTaskDialogs(forceShowOverdue = showOverdueDialog)

        return view
    }

    private fun updateTaskInDatabase(task: Task, isChecked: Boolean) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val currentTime = System.currentTimeMillis()
            val plant = db.ownedPlantDao().getOwnedPlantById(task.plantId)
            val plantName = plant?.plantName ?: "Your Plant"

            if (isChecked) {
                val nextScheduledTime = TaskScheduler.calculateNextTime(task.taskFrequency)
                val updatedTask = task.copy(
                    taskDone = true,
                    taskDoneDate = currentTime,
                    nextReminderTime = nextScheduledTime
                )
                db.taskDao().update(updatedTask)
                
                // Re-schedule alarm for the next period
                TaskScheduler.scheduleTask(
                    requireContext(),
                    task.idTask,
                    task.taskName ?: "Care Task",
                    plantName,
                    task.taskFrequency
                )
                Toast.makeText(requireContext(), "Task completed! Checkbox locked until next schedule.", Toast.LENGTH_SHORT).show()

                // Check if all tasks are completed now
                val allTasks = db.taskDao().getTasksWithPlantName()
                if (allTasks.isNotEmpty() && allTasks.all { it.task.taskDone }) {
                    showWeeklyCompletedDialog()
                }
            } else {
                val updatedTask = task.copy(taskDone = false)
                db.taskDao().update(updatedTask)
            }
        }
    }

    private fun showDateTimePicker(task: Task) {
        val calendar = Calendar.getInstance()
        val now = System.currentTimeMillis()

        val datePickerDialog = DatePickerDialog(requireContext(), { _, year, month, day ->
            val selectedTime = Calendar.getInstance()
            selectedTime.set(year, month, day)

            val timePickerDialog = TimePickerDialog(requireContext(), { _, hour, minute ->
                selectedTime.set(Calendar.HOUR_OF_DAY, hour)
                selectedTime.set(Calendar.MINUTE, minute)
                selectedTime.set(Calendar.SECOND, 0)

                val chosenMillis = selectedTime.timeInMillis
                if (chosenMillis < System.currentTimeMillis()) {
                    Toast.makeText(requireContext(), "Selected time cannot be in the past. Scheduled for now.", Toast.LENGTH_SHORT).show()
                    updateTaskSchedule(task, System.currentTimeMillis() + 60000)
                } else {
                    updateTaskSchedule(task, chosenMillis)
                }
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false)

            timePickerDialog.show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        // Restrict calendar so user cannot pick a date earlier than current date
        datePickerDialog.datePicker.minDate = now - 1000
        datePickerDialog.show()
    }

    private fun updateTaskSchedule(task: Task, timeInMillis: Long) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val plant = db.ownedPlantDao().getOwnedPlantById(task.plantId)
            val plantName = plant?.plantName ?: "Your Plant"
            
            // Schedule exact alarm
            TaskScheduler.scheduleManualTask(requireContext(), task.idTask, task.taskName ?: "Task", plantName, task.taskFrequency, timeInMillis)
            
            // Update database
            db.taskDao().update(task.copy(nextReminderTime = timeInMillis, taskDone = false))
            Toast.makeText(requireContext(), "Reminder set for ${task.taskName}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkAndShowTaskDialogs(forceShowOverdue: Boolean = false) {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val tasksWithPlant = db.taskDao().getTasksWithPlantName()
            if (tasksWithPlant.isEmpty()) return@launch

            val currentTime = System.currentTimeMillis()

            // 1. Overdue tasks (24h+ past due)
            val overdueTasks = tasksWithPlant.filter { item ->
                !item.task.taskDone && item.task.nextReminderTime != null && currentTime >= (item.task.nextReminderTime + OVERDUE_THRESHOLD_MILLIS)
            }

            if (overdueTasks.isNotEmpty() && (forceShowOverdue || isAdded)) {
                showOverdueTasksDialog(overdueTasks)
                return@launch
            }

            // 2. Daily Missed tasks (due today, <= 24h past due)
            val missedTasksToday = tasksWithPlant.filter { item ->
                !item.task.taskDone && item.task.nextReminderTime != null && currentTime >= item.task.nextReminderTime
            }

            if (missedTasksToday.isNotEmpty() && isAdded) {
                showMissedTasksTodayDialog(missedTasksToday)
                return@launch
            }

            // 3. Weekly tasks all completed
            val allTasksCompleted = tasksWithPlant.all { it.task.taskDone }
            if (allTasksCompleted && !hasShownCompletedDialogThisSession && isAdded) {
                hasShownCompletedDialogThisSession = true
                showWeeklyCompletedDialog()
            }
        }
    }

    private fun showOverdueTasksDialog(overdueTasks: List<TaskWithPlantName>) {
        if (!isAdded) return
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_overdue_tasks, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
            .setView(dialogView)
            .create()

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvOverdueTitle)
        val rvOverdue = dialogView.findViewById<RecyclerView>(R.id.rvOverdueTasks)
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnCloseDialog)
        val btnDismiss = dialogView.findViewById<Button>(R.id.btnDismissDialog)

        tvTitle.text = "🚨 Overdue Plant Tasks"
        tvTitle.setTextColor(Color.parseColor("#D32F2F"))
        btnDismiss.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#D32F2F"))
        btnDismiss.text = "I'll Take Care of It"

        rvOverdue.layoutManager = LinearLayoutManager(requireContext())
        lateinit var overdueAdapter: OverdueTaskAdapter

        overdueAdapter = OverdueTaskAdapter(overdueTasks) { item ->
            updateTaskInDatabase(item.task, true)
            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(requireContext())
                val updatedList = db.taskDao().getTasksWithPlantName().filter {
                    !it.task.taskDone && it.task.nextReminderTime != null && System.currentTimeMillis() >= (it.task.nextReminderTime + OVERDUE_THRESHOLD_MILLIS)
                }
                if (updatedList.isEmpty()) {
                    dialog.dismiss()
                    checkAndShowTaskDialogs()
                } else {
                    overdueAdapter.updateTasks(updatedList)
                }
            }
        }
        rvOverdue.adapter = overdueAdapter

        btnClose.setOnClickListener { dialog.dismiss() }
        btnDismiss.setOnClickListener { dialog.dismiss() }

        dialog.show()
        dialog.window?.let { window ->
            window.setBackgroundDrawableResource(android.R.color.transparent)
            val layoutParams = window.attributes
            layoutParams.width = (resources.displayMetrics.widthPixels * 0.92).toInt()
            window.attributes = layoutParams
        }
    }

    private fun showMissedTasksTodayDialog(missedTasks: List<TaskWithPlantName>) {
        if (!isAdded) return
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_overdue_tasks, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
            .setView(dialogView)
            .create()

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvOverdueTitle)
        val rvMissed = dialogView.findViewById<RecyclerView>(R.id.rvOverdueTasks)
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnCloseDialog)
        val btnDismiss = dialogView.findViewById<Button>(R.id.btnDismissDialog)

        tvTitle.text = "📋 Missed Tasks Today"
        tvTitle.setTextColor(Color.parseColor("#E65100"))

        val orangeColorState = ColorStateList.valueOf(Color.parseColor("#E65100"))
        btnDismiss.backgroundTintList = orangeColorState
        btnDismiss.text = "Got It"

        rvMissed.layoutManager = LinearLayoutManager(requireContext())
        lateinit var missedAdapter: OverdueTaskAdapter

        missedAdapter = OverdueTaskAdapter(missedTasks) { item ->
            updateTaskInDatabase(item.task, true)
            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(requireContext())
                val currentTime = System.currentTimeMillis()
                val updatedList = db.taskDao().getTasksWithPlantName().filter {
                    !it.task.taskDone && it.task.nextReminderTime != null && currentTime >= it.task.nextReminderTime && currentTime < (it.task.nextReminderTime + OVERDUE_THRESHOLD_MILLIS)
                }
                if (updatedList.isEmpty()) {
                    dialog.dismiss()
                    checkAndShowTaskDialogs()
                } else {
                    missedAdapter.updateTasks(updatedList)
                }
            }
        }
        rvMissed.adapter = missedAdapter

        btnClose.setOnClickListener { dialog.dismiss() }
        btnDismiss.setOnClickListener { dialog.dismiss() }

        dialog.show()
        dialog.window?.let { window ->
            window.setBackgroundDrawableResource(android.R.color.transparent)
            val layoutParams = window.attributes
            layoutParams.width = (resources.displayMetrics.widthPixels * 0.92).toInt()
            window.attributes = layoutParams
        }
    }

    private fun showWeeklyCompletedDialog() {
        if (!isAdded) return
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_overdue_tasks, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
            .setView(dialogView)
            .create()

        val tvTitle = dialogView.findViewById<TextView>(R.id.tvOverdueTitle)
        val rvTasks = dialogView.findViewById<RecyclerView>(R.id.rvOverdueTasks)
        val btnClose = dialogView.findViewById<ImageButton>(R.id.btnCloseDialog)
        val btnDismiss = dialogView.findViewById<Button>(R.id.btnDismissDialog)

        tvTitle.text = "😊 Tasks Completed!"
        tvTitle.setTextColor(Color.parseColor("#398945"))

        rvTasks.visibility = View.GONE

        val greenColorState = ColorStateList.valueOf(Color.parseColor("#398945"))
        btnDismiss.backgroundTintList = greenColorState
        btnDismiss.text = "Awesome!"

        btnClose.setOnClickListener { dialog.dismiss() }
        btnDismiss.setOnClickListener { dialog.dismiss() }

        dialog.show()
        dialog.window?.let { window ->
            window.setBackgroundDrawableResource(android.R.color.transparent)
            val layoutParams = window.attributes
            layoutParams.width = (resources.displayMetrics.widthPixels * 0.92).toInt()
            window.attributes = layoutParams
        }
    }

    private fun showAddPlantOptions() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_add_plant, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
            .setView(dialogView)
            .create()

        dialogView.findViewById<View>(R.id.btnScanOption).setOnClickListener {
            dialog.dismiss()
            checkCameraPermission()
        }

        dialogView.findViewById<View>(R.id.btnLibraryOption).setOnClickListener {
            dialog.dismiss()
            (activity as? MainActivity)?.replaceFragment(LibraryFrag(), R.id.btn_lib)
        }

        dialog.show()
        dialog.window?.let { window ->
            window.setBackgroundDrawableResource(android.R.color.transparent)
            val layoutParams = window.attributes
            layoutParams.width = (resources.displayMetrics.widthPixels * 0.95).toInt()
            window.attributes = layoutParams
        }
    }

    private fun checkCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }
            else -> {
                requestCameraPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val intent = Intent(requireContext(), ScanActivity::class.java)
        startActivity(intent)
    }

    private fun observeViewModel() {
        // Observe Owned Plants for Carousel
        viewModel.ownedPlants.observe(viewLifecycleOwner) { plants ->
            currentOwnedPlants = plants
            if (plants.isEmpty()) {
                lblOwnPlant.visibility = View.GONE
                viewPager.visibility = View.GONE
                cardTasks.visibility = View.GONE
                layoutEmptyState.visibility = View.VISIBLE
                taskAdapter.updateTasks(emptyList())
            } else {
                lblOwnPlant.visibility = View.VISIBLE
                lblOwnPlant.text = "OWNED PLANTS:"
                viewPager.visibility = View.VISIBLE
                cardTasks.visibility = View.VISIBLE
                layoutEmptyState.visibility = View.GONE
                
                val images = plants.map { it.plantPic }
                val names = plants.map { it.plantName ?: "Unknown" }
                
                val carouselAdapter = CarouselAdapter(images, names)
                viewPager.adapter = carouselAdapter
                
                val targetPlantId = arguments?.getInt("PLANT_ID", -1) ?: -1
                if (targetPlantId != -1) {
                    val targetIndex = plants.indexOfFirst { it.idOwned == targetPlantId }
                    if (targetIndex != -1) {
                        viewPager.currentItem = targetIndex
                        viewModel.selectPlant(targetPlantId)
                    } else {
                        viewModel.selectPlant(plants[0].idOwned)
                    }
                } else {
                    viewModel.selectPlant(plants[0].idOwned)
                }
            }
        }

        // Observe Tasks for selected plant
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            val currentTime = System.currentTimeMillis()

            // Reset completion state if the scheduled time has arrived so checkbox unlocks
            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(requireContext())
                tasks.forEach { task ->
                    if (task.taskDone && task.nextReminderTime != null && currentTime >= task.nextReminderTime) {
                        db.taskDao().update(task.copy(taskDone = false))
                    }
                }
            }

            // Sort tasks: Active/Unchecked tasks first (soonest due first), then completed tasks below
            val sortedTasks = tasks.sortedWith(
                compareBy<Task> { it.taskDone }
                    .thenBy { it.nextReminderTime ?: Long.MAX_VALUE }
            )
            taskAdapter.updateTasks(sortedTasks)

            // Highlight target task if passed from notification/intent
            val highlightTaskId = arguments?.getInt("HIGHLIGHT_TASK_ID", -1) ?: -1
            if (highlightTaskId != -1) {
                taskAdapter.highlightTask(highlightTaskId)
            }
        }
    }
}
