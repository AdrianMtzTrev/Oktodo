@file:Suppress("SpellCheckingInspection")

package com.example.oktodo.ui.model

data class ProfileUiState(
    val displayName: String = "Usuario OKTodo",
    val username: String = "usuario_oktodo",
    val avatarEmoji: String = "🐙",
    val points: Int = 0,
    val completedTasks: Int = 0,
    val streakDays: Int = 5,
    val weeklyGoal: Int = 10,
    val weeklyCompleted: Int = 0,
    val achievements: List<Achievement> = listOf(
        Achievement("🏆", "Primera tarea"),
        Achievement("🔥", "5 días racha"),
        Achievement("⭐", "100 puntos")
    )
)

data class Achievement(
    val icon: String,
    val title: String
)

data class ShopItem(
    val id: String,
    val title: String,
    val emoji: String,
    val price: Int,
    val category: String,
    val isPurchased: Boolean = false
)
