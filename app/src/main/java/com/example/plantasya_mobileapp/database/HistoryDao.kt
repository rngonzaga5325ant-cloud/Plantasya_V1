package com.example.plantasya_mobileapp.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history_tbl WHERE user_id = :userId ORDER BY date_scanned DESC")
    fun getAllHistory(userId: Int): Flow<List<History>>

    @Insert
    suspend fun insert(history: History)

    @Update
    suspend fun update(history: History)

    @Delete
    suspend fun delete(history: History)
}
