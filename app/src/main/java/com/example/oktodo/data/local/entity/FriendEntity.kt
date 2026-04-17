package com.example.oktodo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friends")
data class FriendEntity(
    @PrimaryKey val id: String,
    val name: String,
    val points: Int,
    val events: Int,
    val avatar: String,
    val avatarColorArgb: Long
)
