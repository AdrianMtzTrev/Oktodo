package com.example.oktodo.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user_profiles",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserProfileEntity(
    @PrimaryKey val id: String,
    val displayName: String,
    val username: String,
    val avatarEmoji: String,
    val createdAt: Long = System.currentTimeMillis()
)
