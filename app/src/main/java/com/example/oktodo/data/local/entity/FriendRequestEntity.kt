package com.example.oktodo.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "friend_requests",
    indices = [Index(value = ["fromUserId", "toUserId"], unique = true)]
)
data class FriendRequestEntity(
    @PrimaryKey val id: String,
    val fromUserId: String,
    val toUserId: String,
    val fromDisplayName: String,
    val fromAvatarEmoji: String,
    val toDisplayName: String,
    val toAvatarEmoji: String,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)
