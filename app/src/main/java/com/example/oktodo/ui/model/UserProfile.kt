package com.example.oktodo.ui.model

data class UserProfile(
    val id: String,
    val displayName: String,
    val username: String,
    val avatarEmoji: String,
    val passwordHash: String = "",
    val createdAt: Long = 0L
)
