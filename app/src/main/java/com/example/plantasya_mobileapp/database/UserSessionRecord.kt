package com.example.plantasya_mobileapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_sessions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class UserSessionRecord(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "session_id")
    val session_id: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "login_time")
    val loginTime: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "logout_time")
    var logoutTime: Long? = null
)
