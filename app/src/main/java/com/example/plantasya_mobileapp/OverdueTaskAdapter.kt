package com.example.plantasya_mobileapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantasya_mobileapp.database.TaskWithPlantName
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OverdueTaskAdapter(
    private var tasks: List<TaskWithPlantName>,
    private val onCompleteClicked: (TaskWithPlantName) -> Unit
) : RecyclerView.Adapter<OverdueTaskAdapter.ViewHolder>() {

    private val timeFormat = SimpleDateFormat("MMM dd, yyyy - hh:mm a", Locale.getDefault())

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPlantName: TextView = view.findViewById(R.id.tvOverduePlantName)
        val tvTaskName: TextView = view.findViewById(R.id.tvOverdueTaskName)
        val tvDueTime: TextView = view.findViewById(R.id.tvOverdueDueTime)
        val btnComplete: Button = view.findViewById(R.id.btnQuickComplete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_overdue_task, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = tasks[position]
        val task = item.task
        
        holder.tvPlantName.text = item.plantName ?: "Your Plant"
        holder.tvTaskName.text = "${task.taskName} (${task.taskFrequency ?: "Care Task"})"
        
        task.nextReminderTime?.let {
            holder.tvDueTime.text = "Due: ${timeFormat.format(Date(it))}"
        } ?: run {
            holder.tvDueTime.text = "Overdue"
        }

        holder.btnComplete.setOnClickListener {
            onCompleteClicked(item)
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<TaskWithPlantName>) {
        tasks = newTasks
        notifyDataSetChanged()
    }
}
