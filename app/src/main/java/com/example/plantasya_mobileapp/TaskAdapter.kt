package com.example.plantasya_mobileapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private var tasks: List<String>,
    private var taskStates: MutableList<Boolean>,
    private val onTaskChecked: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.cbTask)
        val description: TextView = view.findViewById(R.id.tvTaskDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.description.text = tasks[position]
        
        // Remove listener before setting checked state to avoid triggering it
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = taskStates[position]

        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            taskStates[position] = isChecked
            onTaskChecked(position, isChecked)
        }
    }

    override fun getItemCount(): Int = tasks.size

    fun updateTasks(newTasks: List<String>, newStates: List<Boolean>) {
        tasks = newTasks
        taskStates = newStates.toMutableList()
        notifyDataSetChanged()
    }
}
