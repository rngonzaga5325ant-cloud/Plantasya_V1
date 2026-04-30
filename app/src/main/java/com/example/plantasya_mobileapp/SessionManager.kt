package com.example.plantasya_mobileapp

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_session", Context.MODE_PRIVATE)

    companion object {
        const val KEY_USERNAME = "username"
        const val KEY_USER_ID = "user_id"
        const val KEY_SESSION_ID = "session_id"
        const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    fun startSession(userId: Int, username: String, sessionId: Int) {
        prefs.edit().apply {
            putInt(KEY_USER_ID, userId)
            putString(KEY_USERNAME, username)
            putInt(KEY_SESSION_ID, sessionId)
            putBoolean(KEY_IS_LOGGED_IN, true)
            apply()
        }
    }

    fun getUsername(): String? = prefs.getString(KEY_USERNAME, null)
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)
    fun getSessionId(): Int = prefs.getInt(KEY_SESSION_ID, -1)

    fun isSessionValid(): Boolean {
        // Session is valid if the user is explicitly logged in. 
        // Duration check removed as requested.
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
