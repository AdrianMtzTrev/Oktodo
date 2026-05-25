package com.example.oktodo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shared_events")
data class SharedEventEntity(
    @PrimaryKey val id: String,
    val title: String,
    val creator: String,
    val groupId: String,
    val date: String,
    val time: String,
    val location: String,
    val participantsJoined: String,
    val canEditJoined: String = ""
)
