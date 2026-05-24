package com.example.plantasya_mobileapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_tbl")
data class History(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "history_id")
    val historyId: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "plant_pic", typeAffinity = ColumnInfo.BLOB)
    val plantPic: ByteArray? = null,

    @ColumnInfo(name = "id_plant")
    val idPlant: Int? = null,

    @ColumnInfo(name = "plant_name")
    val plantName: String? = null,

    @ColumnInfo(name = "is_owned")
    val isOwned: Boolean = false,

    @ColumnInfo(name = "date_scanned")
    val dateScanned: Long = System.currentTimeMillis()
)
