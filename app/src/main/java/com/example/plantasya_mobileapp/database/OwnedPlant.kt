package com.example.plantasya_mobileapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "owned_plants",
    foreignKeys = [
        ForeignKey(
            entity = LibraryPlant::class,
            parentColumns = ["id_lib"],
            childColumns = ["library_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["user_id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["library_id"]),
        Index(value = ["user_id"])
    ]
)
data class OwnedPlant(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_owned")
    val idOwned: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int, // Link to the user who owns the plant

    @ColumnInfo(name = "library_id")
    val libraryId: Int? = null, // Link to LibraryPlant catalog entry

    @ColumnInfo(name = "plant_pic", typeAffinity = ColumnInfo.BLOB)
    val plantPic: ByteArray? = null,

    @ColumnInfo(name = "owned")
    val owned: Boolean = false,

    @ColumnInfo(name = "plant_name")
    val plantName: String? = null,

    @ColumnInfo(name = "scientific_name")
    val scientificName: String? = null
)
