package com.example.oktodo.ui.model

import androidx.compose.ui.graphics.Color
import java.time.LocalDate
import java.util.UUID

data class Task(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val time: String,
    val priority: String,
    val color: Color,
    val isCompleted: Boolean = false,
    val pointsReward: Int = 10,
    val category: String = "",
    val recurrenceType: String = "none",
    val recurrenceInterval: Int = 1,
    val date: LocalDate = LocalDate.now()
)