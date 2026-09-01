package com.example.plantasya_mobileapp.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks_tbl")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks_tbl")
    suspend fun getAllTasksOnce(): List<Task>

    @Query("SELECT * FROM tasks_tbl WHERE plant_id = :plantId")
    fun getTasksByPlantId(plantId: Int): Flow<List<Task>>

    @Query("SELECT * FROM tasks_tbl WHERE plant_id = :plantId")
    suspend fun getTasksByPlantIdOnce(plantId: Int): List<Task>

    @Query("SELECT * FROM tasks_tbl WHERE id_task = :taskId")
    suspend fun getTaskById(taskId: Int): Task?

    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
    @Query("""
        SELECT tasks_tbl.*, owned_plants.plant_name 
        FROM tasks_tbl 
        JOIN owned_plants ON tasks_tbl.plant_id = owned_plants.id_owned
    """)
    suspend fun getTasksWithPlantName(): List<TaskWithPlantName>
}

data class TaskWithPlantName(
    @Embedded val task: Task,
    @ColumnInfo(name = "plant_name") val plantName: String?
)
