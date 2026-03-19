package com.example.oktodo.ui.model

data class SharedEvent(
    val id: String,
    val title: String,
    val creator: String,
    val groupId: String,
    val date: String,      // formato: yyyy-MM-dd
    val time: String,
    val location: String,
    val participants: List<String>
)