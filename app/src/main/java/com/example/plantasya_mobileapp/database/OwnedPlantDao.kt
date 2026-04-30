package com.example.plantasya_mobileapp.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OwnedPlantDao {
    @Query("SELECT * FROM owned_plants")
    fun getAll(): Flow<List<OwnedPlant>>

    @Query("SELECT * FROM owned_plants WHERE user_id = :userId")
    fun getOwnedPlantsByUserId(userId: Int): Flow<List<OwnedPlant>>

    @Query("SELECT * FROM owned_plants WHERE library_id = :libraryId AND user_id = :userId LIMIT 1")
    suspend fun getOwnedPlantByLibraryIdAndUser(libraryId: Int, userId: Int): OwnedPlant?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(plant: OwnedPlant)

    @Update
    suspend fun update(plant: OwnedPlant)

    @Delete
    suspend fun delete(plant: OwnedPlant)

    @Query("DELETE FROM owned_plants WHERE library_id = :libraryId AND user_id = :userId")
    suspend fun deleteByLibraryIdAndUser(libraryId: Int, userId: Int)
}
