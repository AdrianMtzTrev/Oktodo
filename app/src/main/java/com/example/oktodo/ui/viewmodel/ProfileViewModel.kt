package com.example.oktodo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oktodo.data.local.UserPreferencesDataStore
import com.example.oktodo.data.repository.NotificationRepository
import com.example.oktodo.data.repository.ShopItemRepository
import com.example.oktodo.ui.model.Achievement
import com.example.oktodo.ui.model.Notification
import com.example.oktodo.ui.model.ProfileUiState
import com.example.oktodo.ui.model.ShopItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PurchaseEvent {
    data class Success(val itemName: String) : PurchaseEvent()
    data object InsufficientPoints : PurchaseEvent()
}

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefs: UserPreferencesDataStore,
    private val shopItemRepository: ShopItemRepository,
    private val notificationRepository: NotificationRepository
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
                    ),
                    isSocialRegistered = p.isSocialRegistered
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProfileUiState())

    val shopItems = shopItemRepository.items
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val equippedItem = shopItemRepository.equippedItem
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _purchaseEvent = Channel<PurchaseEvent>(Channel.BUFFERED)
    val purchaseEvent = _purchaseEvent.receiveAsFlow()

    init {
        viewModelScope.launch { shopItemRepository.seedIfEmpty() }
        viewModelScope.launch { prefs.checkWeeklyReset() }
        observeNewAchievements()
    }

    private fun observeNewAchievements() {
        viewModelScope.launch {
            combine(
                uiState.map { it.achievements.filter { a -> a.isUnlocked }.map { a -> a.title } },
                prefs.notifiedAchievements
            ) { unlockedTitles, notified ->
                unlockedTitles - notified
            }.collect { newTitles ->
                val userId = prefs.getUserId()
                if (userId == null) return@collect
                for (title in newTitles) {
                    val achievement = uiState.value.achievements.find { it.title == title } ?: continue
                    notificationRepository.add(
                        Notification(
                            userId = userId,
                            title = "Logro desbloqueado",
                            message = "Has desbloqueado el logro \"${achievement.title}\"",
                            icon = achievement.icon
                        )
                    )
                    prefs.markAchievementNotified(title)
                }
            }
        }
    }

    fun updateProfile(displayName: String, username: String, avatarEmoji: String) {
        viewModelScope.launch { prefs.updateProfile(displayName, username, avatarEmoji) }
    }

    fun setWeeklyGoal(goal: Int) {
        viewModelScope.launch { prefs.setWeeklyGoal(goal) }
    }

    fun addPoints(points: Int) {
        viewModelScope.launch { prefs.addPoints(points) }
    }

    fun purchaseItem(itemId: String) {
        viewModelScope.launch {
            val item = shopItems.value.find { it.id == itemId } ?: return@launch
            val success = shopItemRepository.purchase(item, uiState.value.points)
            if (success) {
                _purchaseEvent.send(PurchaseEvent.Success(item.title))
            } else {
                _purchaseEvent.send(PurchaseEvent.InsufficientPoints)
            }
        }
    }

    fun equipItem(itemId: String) {
        viewModelScope.launch { shopItemRepository.equip(itemId) }
    }

    fun unequipItem(itemId: String) {
        viewModelScope.launch { shopItemRepository.unequip(itemId) }
    }

    fun logoutSocial() {
        viewModelScope.launch { prefs.logoutSocial() }
    }
}
