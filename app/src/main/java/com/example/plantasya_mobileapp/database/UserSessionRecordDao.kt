package com.example.plantasya_mobileapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserSessionRecordDao {
    @Insert
    suspend fun insertSession(session: UserSessionRecord): Long

    @Update
    suspend fun updateSession(session: UserSessionRecord)

    @Query("SELECT * FROM user_sessions WHERE session_id = :sessionId")
    suspend fun getSessionById(sessionId: Int): UserSessionRecord?

    @Query("SELECT * FROM user_sessions WHERE user_id = :userId ORDER BY login_time DESC")
    suspend fun getSessionsByUserId(userId: Int): List<UserSessionRecord>
}
