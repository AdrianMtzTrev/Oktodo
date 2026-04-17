package com.example.oktodo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "groups")
data class GroupEntity(
    @PrimaryKey val id: String,
    val name: String,
    val icon: String,
    val membersJoined: String,
    val eventCount: Int
)
