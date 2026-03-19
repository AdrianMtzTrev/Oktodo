package com.example.oktodo.ui.model

import androidx.compose.ui.graphics.Color

data class Friend(
    val id: String,
    val name: String,
    val points: Int,
    val events: Int,
    val avatar: String,
    val avatarColor: Color
)