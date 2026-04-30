package com.example.plantasya_mobileapp.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryPlantDao {
    @Query("SELECT * FROM library_plant_tbl ORDER BY plant_name ASC")
    fun getAll(): Flow<List<LibraryPlant>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(plant: LibraryPlant)

    @Query("SELECT COUNT(*) FROM library_plant_tbl")
    suspend fun getCount(): Int

    @Query("SELECT * FROM library_plant_tbl WHERE id_lib = :plantId LIMIT 1")
    suspend fun getPlantById(plantId: Int): LibraryPlant?

    @Query("SELECT * FROM library_plant_tbl WHERE owned = 1 ORDER BY plant_name ASC")
    fun getOwnedLibraryPlants(): Flow<List<LibraryPlant>>

    @Query("SELECT * FROM library_plant_tbl WHERE plant_name LIKE '%' || :searchQuery || '%' ORDER BY plant_name ASC")
    fun searchPlants(searchQuery: String): Flow<List<LibraryPlant>>

    @Query("UPDATE library_plant_tbl SET owned = :isOwned WHERE id_lib = :plantId")
    suspend fun updateOwnedStatus(plantId: Int, isOwned: Boolean)
}
