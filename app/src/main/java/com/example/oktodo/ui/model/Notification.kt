package com.example.oktodo.ui.model

import java.util.UUID

data class Notification(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val message: String,
    val icon: String,
    val isRead: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
