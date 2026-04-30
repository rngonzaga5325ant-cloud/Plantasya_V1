package com.example.plantasya_mobileapp.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class LibraryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LibraryPlantRepository
    private val searchQuery = MutableStateFlow("")

    init {
        val database = AppDatabase.getDatabase(application)
        repository = LibraryPlantRepository(database.libraryPlantDao())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val plants = searchQuery.flatMapLatest { query ->
        if (query.isEmpty()) {
            repository.getAllPlants()
        } else {
            repository.searchPlants(query)
        }
    }.asLiveData()

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }
}
