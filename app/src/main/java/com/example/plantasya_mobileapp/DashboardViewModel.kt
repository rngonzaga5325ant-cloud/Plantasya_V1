package com.example.plantasya_mobileapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import com.example.plantasya_mobileapp.database.AppDatabase
import com.example.plantasya_mobileapp.database.OwnedPlant
import com.example.plantasya_mobileapp.database.Task
import com.example.plantasya_mobileapp.database.TaskDao

class DashboardViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val ownedPlantDao = database.ownedPlantDao()
    private val taskDao = database.taskDao()

    private val _userId = MutableLiveData<Int>()

    val ownedPlants: LiveData<List<OwnedPlant>> = _userId.switchMap { userId ->
        ownedPlantDao.getOwnedPlantsByUserId(userId).asLiveData()
    }

    private val selectedPlantId = MutableLiveData<Int>()

    val tasks: LiveData<List<Task>> = selectedPlantId.switchMap { plantId ->
        taskDao.getTasksByPlantId(plantId).asLiveData()
    }

    fun setUserId(userId: Int) {
        _userId.value = userId
    }

    fun selectPlant(plantId: Int) {
        selectedPlantId.value = plantId
    }
    
    fun updateTaskStatus(task: Task, isChecked: Boolean) {
        // Implementation for updating task status can be added here if needed
    }
}
