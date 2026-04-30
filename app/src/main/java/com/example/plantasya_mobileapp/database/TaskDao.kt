package com.example.plantasya_mobileapp.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks_tbl")
    fun getAllTasks(): Flow<List<Task>>

    @Query("SELECT * FROM tasks_tbl WHERE plant_id = :plantId")
    fun getTasksByPlantId(plantId: Int): Flow<List<Task>>

    @Insert
    suspend fun insert(task: Task)

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)
}
