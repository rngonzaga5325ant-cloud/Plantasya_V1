package com.example.plantasya_mobileapp

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantasya_mobileapp.database.AppDatabase
import com.example.plantasya_mobileapp.database.History
import com.example.plantasya_mobileapp.database.OwnedPlant
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryFrag : Fragment() {

    private lateinit var rvHistory: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var sessionManager: SessionManager
    private lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        
        sessionManager = SessionManager(requireContext())
        database = AppDatabase.getDatabase(requireContext())

        rvHistory = view.findViewById(R.id.rvHistory)
        rvHistory.layoutManager = LinearLayoutManager(requireContext())
        
        adapter = HistoryAdapter(emptyList()) { history ->
            handleOwnedToggle(history)
        }
        rvHistory.adapter = adapter

        val userId = sessionManager.getUserId()

        lifecycleScope.launch {
            database.historyDao().getAllHistory(userId).collectLatest { historyList ->
                adapter.updateData(historyList)
            }
        }

        return view
    }

    private fun handleOwnedToggle(history: History) {
        if (history.isOwned) {
            showRemovePlantDialog(history)
        } else {
            addPlantToOwned(history)
        }
    }

    private fun showRemovePlantDialog(history: History) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_remove_plant, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.CustomDialog)
            .setView(dialogView)
            .create()

        val plantName = history.plantName ?: "this plant"
        dialogView.findViewById<TextView>(R.id.tvRemoveMessage).text = 
            "Are you sure you want to remove $plantName from your dashboard? All related tasks will also be deleted."

        dialogView.findViewById<Button>(R.id.btnCancelRemove).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.btnConfirmRemove).setOnClickListener {
            dialog.dismiss()
            removePlantFromOwned(history)
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }

    private fun addPlantToOwned(history: History) {
        lifecycleScope.launch {
            val userId = sessionManager.getUserId()
            
            // Add to Owned Plants Table
            val ownedPlant = OwnedPlant(
                userId = userId,
                libraryId = history.idPlant, // Might be null if it was a scan not from library
                plantName = history.plantName,
                plantPic = history.plantPic,
                owned = true
            )
            val newId = database.ownedPlantDao().insert(ownedPlant)
            
            // Sync tasks
            TaskSyncManager.syncTasksForPlant(requireContext(), newId.toInt(), history.plantName ?: "")
            
            // Update History record
            database.historyDao().update(history.copy(isOwned = true))
            
            Toast.makeText(context, "${history.plantName} added to your dashboard", Toast.LENGTH_SHORT).show()
        }
    }

    private fun removePlantFromOwned(history: History) {
        lifecycleScope.launch {
            val userId = sessionManager.getUserId()
            val plantName = history.plantName ?: ""
            
            // Remove from Owned Plants
            // Note: If multiple history items have the same name, this might remove "one" or "all" 
            // depending on the DAO. Our deleteByNameAndUser removes all with that name for that user.
            database.ownedPlantDao().deleteByNameAndUser(plantName, userId)
            
            // Update History record
            database.historyDao().update(history.copy(isOwned = false))
            
            Toast.makeText(context, "$plantName removed from your dashboard", Toast.LENGTH_SHORT).show()
        }
    }
}
