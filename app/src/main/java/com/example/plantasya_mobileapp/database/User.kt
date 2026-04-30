package com.example.plantasya_mobileapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    val user_id: Int = 0,
    val username: String,
    val password: String,
    val role: String
)
