package com.example.plantasya_mobileapp.database

class UserRepository(
    private val userDao: UserDao,
    private val sessionDao: UserSessionRecordDao
) {

    suspend fun insertUser(user: User) {
        userDao.insertUser(user)
    }

    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }

    suspend fun getUserByUsername(username: String): User? {
        return userDao.getUserByUsername(username)
    }

    suspend fun getUserById(userId: Int): User? {
        return userDao.getUserById(userId)
    }

    suspend fun getUsersByRole(role: String): List<User> {
        return userDao.getUsersByRole(role)
    }

    suspend fun getAllUsers(): List<User> {
        return userDao.getAllUsers()
    }

    suspend fun deleteUser(user: User) {
        userDao.deleteUser(user)
    }

    // Session methods
    suspend fun insertSession(session: UserSessionRecord): Long {
        return sessionDao.insertSession(session)
    }

    suspend fun updateSession(session: UserSessionRecord) {
        sessionDao.updateSession(session)
    }

    suspend fun getSessionById(sessionId: Int): UserSessionRecord? {
        return sessionDao.getSessionById(sessionId)
    }
}
