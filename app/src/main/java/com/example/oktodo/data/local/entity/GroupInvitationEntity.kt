package com.example.oktodo.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "group_invitations",
    indices = [Index(value = ["toUserId", "groupId"], unique = true)]
)
data class GroupInvitationEntity(
    @PrimaryKey val id: String,
    val fromUserId: String,
    val fromDisplayName: String,
    val toUserId: String,
    val toDisplayName: String,
    val groupId: String,
    val groupName: String,
    val groupIcon: String,
    val status: String,
    val createdAt: Long = System.currentTimeMillis()
)
