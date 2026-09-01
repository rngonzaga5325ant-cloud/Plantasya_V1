package com.example.plantasya_mobileapp

import android.content.Context
import com.example.plantasya_mobileapp.database.AppDatabase
import com.example.plantasya_mobileapp.database.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object TaskSyncManager {

    suspend fun syncTasksForPlant(context: Context, ownedPlantId: Int, plantName: String) {
        val database = AppDatabase.getDatabase(context)
        
        // Try to find a library plant with a similar name
        val libraryPlant = database.libraryPlantDao().getPlantByName("%$plantName%")

        val tasks = if (libraryPlant != null) {
            getTasksForLibraryPlant(ownedPlantId, libraryPlant.plantName ?: "")
        } else {
            // Check if the input plantName itself matches our hardcoded list
            getTasksForLibraryPlant(ownedPlantId, plantName)
        }

        val finalTasks = if (tasks.isEmpty()) {
            // Default tasks for unknown plants
            listOf(
                Task(plantId = ownedPlantId, taskName = "Watering", taskFrequency = "Weekly"),
                Task(plantId = ownedPlantId, taskName = "Check Soil", taskFrequency = "Every 3 days"),
                Task(plantId = ownedPlantId, taskName = "General Care", taskFrequency = "Monthly")
            )
        } else {
            tasks
        }

        withContext(Dispatchers.IO) {
            finalTasks.forEach { task ->
                val taskId = database.taskDao().insert(task)
                TaskScheduler.scheduleTask(
                    context, 
                    taskId.toInt(), 
                    task.taskName ?: "Care Task", 
                    plantName, 
                    task.taskFrequency
                )
            }
        }
    }

    private fun getTasksForLibraryPlant(ownedId: Int, plantName: String): List<Task> {
        return when (plantName) {
            "Aglaonema" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Every 7-10 days"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Monthly (Spring/Summer)"),
                Task(plantId = ownedId, taskName = "Leaf Cleaning", taskFrequency = "Every 2 weeks")
            )
            "Pothos" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Every 7 days"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Monthly"),
                Task(plantId = ownedId, taskName = "Pruning", taskFrequency = "Monthly")
            )
            "Calathea" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Every 5-7 days"),
                Task(plantId = ownedId, taskName = "Humidity check", taskFrequency = "Daily"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Monthly (Spring/Summer)")
            )
            "Money Tree" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Every 7-10 days"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Every 4-6 weeks"),
                Task(plantId = ownedId, taskName = "Rotate plant", taskFrequency = "Monthly")
            )
            "Orchid" -> listOf(
                Task(plantId = ownedId, taskName = "Watering (Soak roots)", taskFrequency = "Every 7 days"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Every 4-6 weeks"),
                Task(plantId = ownedId, taskName = "Check bark moisture", taskFrequency = "Twice weekly")
            )
            "Parlor Palm" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Every 7 days"),
                Task(plantId = ownedId, taskName = "Mist leaves", taskFrequency = "Every 3 days"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Monthly")
            )
            "ZZ Plant" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Monthly"),
                Task(plantId = ownedId, taskName = "Dusting leaves", taskFrequency = "Every 2 weeks"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Every 3 months")
            )
            "Peace Lily" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Every 5-7 days"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "3 times per year"),
                Task(plantId = ownedId, taskName = "Wipe leaves", taskFrequency = "Every 2 weeks")
            )
            "Snake Plant" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Every 21-30 days"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Monthly (Spring/Summer)"),
                Task(plantId = ownedId, taskName = "Check soil moisture", taskFrequency = "Every 2 weeks")
            )
            "Dracaena" -> listOf(
                Task(plantId = ownedId, taskName = "Watering", taskFrequency = "Every 10-14 days"),
                Task(plantId = ownedId, taskName = "Mist leaves", taskFrequency = "Every 3 days"),
                Task(plantId = ownedId, taskName = "Fertilizing", taskFrequency = "Monthly")
            )
            else -> emptyList()
        }
    }
}
