package com.example.plantasya_mobileapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "user_id")
    val user_id: Int = 0,
    val username: String,
    val password: String,
    val role: String,
    @ColumnInfo(name = "display_name")
    val displayName: String? = null,
    @ColumnInfo(name = "profile_pic")
    val profilePic: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (user_id != other.user_id) return false
        if (username != other.username) return false
        if (password != other.password) return false
        if (role != other.role) return false
        if (displayName != other.displayName) return false
        if (profilePic != null) {
            if (other.profilePic == null) return false
            if (!profilePic.contentEquals(other.profilePic)) return false
        } else if (other.profilePic != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = user_id
        result = 31 * result + username.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + role.hashCode()
        result = 31 * result + (displayName?.hashCode() ?: 0)
        result = 31 * result + (profilePic?.contentHashCode() ?: 0)
        return result
    }
}
