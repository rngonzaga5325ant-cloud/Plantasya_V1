package com.example.plantasya_mobileapp.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_tbl ORDER BY date_scanned DESC")
    fun getAllHistory(): Flow<List<History>>

    @Insert
    suspend fun insert(history: History)

    @Delete
    suspend fun delete(history: History)
}
