package com.example.oktodo.ui.model

data class Group(
    val id: String,
    val name: String,
    val icon: String,
    val members: List<String>,
    val memberIds: List<String> = emptyList(),
    val eventCount: Int,
    val creatorId: String = ""
)