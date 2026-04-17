package com.example.oktodo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "calendar_events")
data class CalendarEventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val dateString: String,
    val timeString: String?,
    val location: String?,
    val colorArgb: Long,
    val description: String?
)
