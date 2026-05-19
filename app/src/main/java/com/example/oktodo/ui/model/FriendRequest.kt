package com.example.oktodo.ui.model

data class FriendRequest(
    val id: String,
    val fromUserId: String,
    val toUserId: String,
    val fromDisplayName: String,
    val fromAvatarEmoji: String,
    val toDisplayName: String,
    val toAvatarEmoji: String,
    val status: String,
    val createdAt: Long = 0L
)
