package com.example.plantasya_mobileapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "library_plant_tbl")
data class LibraryPlant(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id_lib")
    val idLib: Int = 0,

    @ColumnInfo(name = "plant_pic", typeAffinity = ColumnInfo.BLOB)
    val plantPic: ByteArray? = null,

    @ColumnInfo(name = "cover_photo", typeAffinity = ColumnInfo.BLOB)
    val coverPhoto: ByteArray? = null,

    @ColumnInfo(name = "plant_name")
    val plantName: String? = null,

    @ColumnInfo(name = "scientific_name")
    val scientificName: String? = null,

    @ColumnInfo(name = "owned")
    val owned: Boolean = false,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "light_pt")
    val lightPt: String? = null,

    @ColumnInfo(name = "water_pt")
    val waterPt: String? = null,

    @ColumnInfo(name = "humidity_plt")
    val humidityPlt: String? = null,

    @ColumnInfo(name = "temp_plt")
    val tempPlt: String? = null,

    @ColumnInfo(name = "soil_plt")
    val soilPlt: String? = null,

    @ColumnInfo(name = "fertilizer_plt")
    val fertilizerPlt: String? = null,

    @ColumnInfo(name = "toxicity_plt")
    val toxicityPlt: String? = null,

    @ColumnInfo(name = "plant_variation")
    val plantVariation: String? = null,

    @ColumnInfo(name = "height_range")
    val heightRange: String? = null,

    @ColumnInfo(name = "space_occupancy")
    val spaceOccupancy: String? = null,

    @ColumnInfo(name = "plant_use")
    val plantUse: String? = null
)
