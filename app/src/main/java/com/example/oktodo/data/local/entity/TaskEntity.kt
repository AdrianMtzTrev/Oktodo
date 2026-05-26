package com.example.oktodo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val time: String,
    val priority: String,
    val colorArgb: Long,
    val isCompleted: Boolean,
    val pointsReward: Int,
    val category: String = "",
    val recurrenceType: String = "none",
    val recurrenceInterval: Int = 1,
    val dateString: String
)
