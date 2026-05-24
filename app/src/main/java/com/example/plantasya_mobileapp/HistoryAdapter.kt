package com.example.plantasya_mobileapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.plantasya_mobileapp.database.History
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private var historyList: List<History>,
    private val onOwnedClick: (History) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgPlant: ImageView = view.findViewById(R.id.imgPlant)
        val tvPlantName: TextView = view.findViewById(R.id.tvPlantName)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val btnOwned: ImageButton = view.findViewById(R.id.btnOwned)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val history = historyList[position]
        holder.tvPlantName.text = history.plantName
        
        val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        holder.tvDate.text = sdf.format(Date(history.dateScanned))

        history.plantPic?.let {
            holder.imgPlant.setImageBitmap(BitmapConverter.byteArrayToBitmap(it))
        }

        if (history.isOwned) {
            holder.btnOwned.setImageResource(R.drawable.ic_owned_plant_filled)
        } else {
            holder.btnOwned.setImageResource(R.drawable.ic_owned_plant)
        }

        holder.btnOwned.setOnClickListener {
            onOwnedClick(history)
        }
    }

    override fun getItemCount() = historyList.size

    fun updateData(newList: List<History>) {
        historyList = newList
        notifyDataSetChanged()
    }
}
