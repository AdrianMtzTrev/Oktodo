package com.example.oktodo.ui.model

data class GroupInvitation(
    val id: String,
    val fromUserId: String,
    val fromDisplayName: String,
    val toUserId: String,
    val toDisplayName: String,
    val groupId: String,
    val groupName: String,
    val groupIcon: String,
    val status: String,
    val createdAt: Long = 0L
)
