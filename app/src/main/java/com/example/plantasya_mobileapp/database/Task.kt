package com.example.plantasya_mobileapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tasks_tbl",
    foreignKeys = [
        ForeignKey(
            entity = OwnedPlant::class,
            parentColumns = ["id_owned"],
            childColumns = ["plant_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["plant_id"])]
)
data class Task(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_task")
    val idTask: Int = 0,

    @ColumnInfo(name = "plant_id")
    val plantId: Int, // Foreign key referencing OwnedPlant.idOwned

    @ColumnInfo(name = "task_name")
    val taskName: String? = null,

    @ColumnInfo(name = "task_frequency")
    val taskFrequency: String? = null,

    @ColumnInfo(name = "task_done")
    val taskDone: Boolean = false,

    @ColumnInfo(name = "task_done_date")
    val taskDoneDate: Long? = null,

    @ColumnInfo(name = "next_reminder_time")
    val nextReminderTime: Long? = null
)
