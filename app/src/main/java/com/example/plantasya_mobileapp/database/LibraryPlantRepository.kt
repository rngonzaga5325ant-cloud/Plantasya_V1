package com.example.plantasya_mobileapp.database

import kotlinx.coroutines.flow.Flow

class LibraryPlantRepository(private val libraryPlantDao: LibraryPlantDao) {

    fun getAllPlants(): Flow<List<LibraryPlant>> = libraryPlantDao.getAll()

    fun searchPlants(query: String): Flow<List<LibraryPlant>> = libraryPlantDao.searchPlants(query)

    suspend fun insert(plant: LibraryPlant) {
        libraryPlantDao.insert(plant)
    }
}
