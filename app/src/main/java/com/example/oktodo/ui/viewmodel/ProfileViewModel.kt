package com.example.oktodo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oktodo.data.local.UserPreferencesDataStore
import com.example.oktodo.data.repository.ShopItemRepository
import com.example.oktodo.ui.model.Achievement
import com.example.oktodo.ui.model.ProfileUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefs: UserPreferencesDataStore,
    private val shopItemRepository: ShopItemRepository
) : ViewModel() {

    val uiState = prefs.preferences
        .map { p ->
            ProfileUiState(
                displayName = p.displayName,
                username = p.username,
                avatarEmoji = p.avatarEmoji,
                points = p.points,
                completedTasks = p.completedTasks,
                streakDays = p.streakDays,
                weeklyGoal = p.weeklyGoal,
                weeklyCompleted = p.weeklyCompleted,
                achievements = listOf(
                    Achievement("🏆", "Primera tarea",   p.completedTasks >= 1),
                    Achievement("✅", "Dedicado",         p.completedTasks >= 10),
                    Achievement("🚀", "Imparable",        p.completedTasks >= 50),
                    Achievement("🔥", "Racha de 5 días",  p.streakDays >= 5),
                    Achievement("🌟", "Racha de 14 días", p.streakDays >= 14),
                    Achievement("⭐", "100 puntos",       p.points >= 100),
                    Achievement("💎", "500 puntos",       p.points >= 500),
                    Achievement("🎯", "Meta semanal",     p.weeklyCompleted >= p.weeklyGoal)
                )
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState())

    val shopItems = shopItemRepository.items
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { shopItemRepository.seedIfEmpty() }
    }

    fun updateProfile(displayName: String, username: String, avatarEmoji: String) {
        viewModelScope.launch { prefs.updateProfile(displayName, username, avatarEmoji) }
    }

    fun addPoints(points: Int) {
        viewModelScope.launch { prefs.addPoints(points) }
    }

    fun purchaseItem(itemId: String) {
        viewModelScope.launch {
            val item = shopItems.value.find { it.id == itemId } ?: return@launch
            shopItemRepository.purchase(item, uiState.value.points)
        }
    }
}
