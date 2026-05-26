package com.example.oktodo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oktodo.data.local.UserPreferencesDataStore
import com.example.oktodo.data.repository.FriendsRepository
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
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
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
    private val friendsRepository: FriendsRepository,
    private val shopItemRepository: ShopItemRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    companion object {
        private const val LOCAL_USER_ID = "offline"
    }

    private fun effectiveUserId(raw: String): String =
        if (raw.isNotBlank()) raw else LOCAL_USER_ID

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

    private val currentUserId = prefs.preferences
        .map { effectiveUserId(it.userId) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), LOCAL_USER_ID)

    val shopItems = currentUserId.flatMapLatest { userId ->
        shopItemRepository.getItemsForUser(userId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val equippedItem = currentUserId.flatMapLatest { userId ->
        shopItemRepository.getEquippedItemForUser(userId)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _purchaseEvent = Channel<PurchaseEvent>(Channel.BUFFERED)
    val purchaseEvent = _purchaseEvent.receiveAsFlow()

    init {
        observeNewAchievements()
        viewModelScope.launch {
            prefs.preferences.map { effectiveUserId(it.userId) }.distinctUntilChanged().collect { userId ->
                shopItemRepository.seedIfEmpty(userId)
            }
        }
    }

    private fun observeNewAchievements() {
        viewModelScope.launch {
            combine(
                uiState.map { s -> s.achievements },
                prefs.notifiedAchievements
            ) { allAchievements, notified ->
                val newTitles = allAchievements.filter { it.isUnlocked }.map { it.title } - notified
                allAchievements to newTitles
            }.collect { (allAchievements, newTitles) ->
                val userId = prefs.getUserId()
                if (userId == null) return@collect
                for (title in newTitles) {
                    val achievement = allAchievements.find { it.title == title } ?: continue
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
        viewModelScope.launch {
            prefs.updateProfile(displayName, username, avatarEmoji)
            val userId = prefs.getUserId()
            if (userId != null) {
                friendsRepository.updateUserProfile(userId, displayName, username, avatarEmoji)
            }
        }
    }

    fun setWeeklyGoal(goal: Int) {
        viewModelScope.launch { prefs.setWeeklyGoal(goal) }
    }

    fun purchaseItem(itemId: String) {
        viewModelScope.launch {
            val userId = effectiveUserId(prefs.getUserId() ?: "")
            shopItemRepository.seedIfEmpty(userId)
            val item = shopItems.value.find { it.id == itemId } ?: return@launch
            val success = shopItemRepository.purchase(item, userId, uiState.value.points)
            if (success) {
                _purchaseEvent.send(PurchaseEvent.Success(item.title))
            } else {
                _purchaseEvent.send(PurchaseEvent.InsufficientPoints)
            }
        }
    }

    fun equipItem(itemId: String) {
        viewModelScope.launch {
            val userId = effectiveUserId(prefs.getUserId() ?: "")
            shopItemRepository.equip(itemId, userId)
        }
    }

    fun unequipItem(itemId: String) {
        viewModelScope.launch {
            val userId = effectiveUserId(prefs.getUserId() ?: "")
            shopItemRepository.unequip(itemId, userId)
        }
    }

    fun logoutSocial() {
        viewModelScope.launch { prefs.logoutSocial() }
    }
}
