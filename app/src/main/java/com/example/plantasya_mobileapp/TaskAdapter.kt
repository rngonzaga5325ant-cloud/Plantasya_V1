package com.example.plantasya_mobileapp

import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantasya_mobileapp.database.Task
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TaskAdapter(
    private var tasks: List<Task>,
    private val onTaskChecked: (Task, Boolean) -> Unit,
    private val onTaskScheduled: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val timeFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())
    private var highlightedTaskId: Int? = null

    companion object {
        private const val OVERDUE_THRESHOLD_MILLIS = 24 * 60 * 60 * 1000L // 24 hours
    }

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val container: View = view.findViewById(R.id.layoutTaskContainer)
        val checkBox: CheckBox = view.findViewById(R.id.cbTask)
        val description: TextView = view.findViewById(R.id.tvTaskDescription)
        val tvTime: TextView = view.findViewById(R.id.tvTaskTime)
        val tvOverdueBadge: TextView = view.findViewById(R.id.tvOverdueBadge)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.description.text = "${task.taskName} (${task.taskFrequency ?: "Care Task"})"
        
        val currentTime = System.currentTimeMillis()
        val nextTime = task.nextReminderTime ?: Long.MAX_VALUE
        
        // Disable checkbox if task is completed and schedule time has NOT arrived yet
        val isScheduledForFuture = task.taskDone && currentTime < nextTime
        holder.checkBox.isEnabled = !isScheduledForFuture

        // Overdue triggers ONLY after 24 hours of missed schedule time
        val isOverdue = !task.taskDone && task.nextReminderTime != null && currentTime >= (task.nextReminderTime + OVERDUE_THRESHOLD_MILLIS)

        if (isOverdue) {
            holder.tvOverdueBadge.visibility = View.VISIBLE
            holder.tvTime.setTextColor(Color.parseColor("#D32F2F"))
            task.nextReminderTime?.let {
                holder.tvTime.text = "Overdue since: ${timeFormat.format(Date(it))}"
            }
        } else {
            holder.tvOverdueBadge.visibility = View.GONE
            holder.tvTime.setTextColor(Color.parseColor("#666666"))
            task.nextReminderTime?.let {
                holder.tvTime.text = "Next: ${timeFormat.format(Date(it))}"
                holder.tvTime.visibility = View.VISIBLE
            } ?: run {
                holder.tvTime.visibility = View.GONE
            }
        }

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = task.taskDone

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onTaskChecked(task, isChecked)
        }

        holder.itemView.setOnClickListener {
            onTaskScheduled(task)
        }

        // Highlight effect if directed from notification
        if (highlightedTaskId == task.idTask) {
            holder.container.setBackgroundColor(Color.parseColor("#FFCDD2")) // Light red highlight
            Handler(Looper.getMainLooper()).postDelayed({
                holder.container.setBackgroundResource(R.drawable.bg_rounded)
            }, 3000)
        } else {
            holder.container.setBackgroundResource(R.drawable.bg_rounded)
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    fun highlightTask(taskId: Int) {
        highlightedTaskId = taskId
        val position = tasks.indexOfFirst { it.idTask == taskId }
        if (position != -1) {
            notifyItemChanged(position)
        }
    }
}
