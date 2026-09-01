package com.example.plantasya_mobileapp

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

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.cbTask)
        val description: TextView = view.findViewById(R.id.tvTaskDescription)
        val tvTime: TextView = view.findViewById(R.id.tvTaskTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.description.text = "${task.taskName} (${task.taskFrequency})"
        
        task.nextReminderTime?.let {
            holder.tvTime.text = "Next: ${timeFormat.format(Date(it))}"
            holder.tvTime.visibility = View.VISIBLE
        } ?: run {
            holder.tvTime.visibility = View.GONE
        }

        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = task.taskDone

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onTaskChecked(task, isChecked)
        }

        holder.itemView.setOnClickListener {
            onTaskScheduled(task)
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}
