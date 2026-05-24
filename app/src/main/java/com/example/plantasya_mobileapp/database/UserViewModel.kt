package com.example.plantasya_mobileapp.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: UserRepository

    init {
        val database = AppDatabase.getDatabase(application)
        repository = UserRepository(database.userDao(), database.sessionDao())
    }

    fun insertUser(user: User) = viewModelScope.launch {
        repository.insertUser(user)
    }

    fun updateUser(user: User) = viewModelScope.launch {
        repository.updateUser(user)
    }

    suspend fun getUserByUsername(username: String): User? {
        return repository.getUserByUsername(username)
    }

    suspend fun getUserById(userId: Int): User? {
        return repository.getUserById(userId)
    }

    suspend fun getUsersByRole(role: String): List<User> {
        return repository.getUsersByRole(role)
    }

    suspend fun getAllUsers(): List<User> {
        return repository.getAllUsers()
    }

    fun deleteUser(user: User) = viewModelScope.launch {
        repository.deleteUser(user)
    }

    // Session methods
    suspend fun startSession(userId: Int): Long {
        return repository.insertSession(UserSessionRecord(userId = userId))
    }

    fun endSession(sessionId: Int) = viewModelScope.launch {
        val session = repository.getSessionById(sessionId)
        session?.let {
            it.logoutTime = System.currentTimeMillis()
            repository.updateSession(it)
        }
    }
}
