package com.example.oktodo.ui.model

import java.time.LocalDate
import java.time.LocalTime

data class CalendarEvent(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val date: LocalDate,
    val time: LocalTime? = null,
    val location: String? = null,
    val color: androidx.compose.ui.graphics.Color = androidx.compose.ui.graphics.Color(0xFF9F7AEA),
    val description: String? = null
)