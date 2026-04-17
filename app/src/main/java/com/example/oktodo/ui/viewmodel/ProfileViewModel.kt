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
                username = p.username,
                avatarEmoji = p.avatarEmoji,
                points = p.points,
                completedTasks = p.completedTasks,
                streakDays = p.streakDays,
                weeklyGoal = p.weeklyGoal,
                weeklyCompleted = p.weeklyCompleted,
                achievements = listOf(
                    Achievement("🏆", "Primera tarea"),
                    Achievement("🔥", "5 días racha"),
                    Achievement("⭐", "100 puntos")
                )
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState())

    val shopItems = shopItemRepository.items
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { shopItemRepository.seedIfEmpty() }
    }

    fun updateProfile(username: String, avatarEmoji: String) {
        viewModelScope.launch { prefs.updateProfile(username, avatarEmoji) }
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
