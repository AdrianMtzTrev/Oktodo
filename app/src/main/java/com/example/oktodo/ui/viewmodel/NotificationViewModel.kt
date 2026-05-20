package com.example.oktodo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oktodo.data.local.UserPreferencesDataStore
import com.example.oktodo.data.repository.NotificationRepository
import com.example.oktodo.ui.model.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository,
    private val dataStore: UserPreferencesDataStore
) : ViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    val notifications = dataStore.userId
        .flatMapLatest { userId ->
            if (userId.isNullOrBlank()) flowOf(emptyList())
            else repository.getNotificationsForUser(userId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    @OptIn(ExperimentalCoroutinesApi::class)
    val unreadCount = dataStore.userId
        .flatMapLatest { userId ->
            if (userId.isNullOrBlank()) flowOf(0)
            else repository.getUnreadCount(userId)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun markAsRead(id: String) {
        viewModelScope.launch { repository.markAsRead(id) }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val userId = dataStore.getUserId()
            if (userId != null) repository.markAllAsRead(userId)
        }
    }

    fun delete(notification: Notification) {
        viewModelScope.launch { repository.delete(notification) }
    }

    fun clearRead() {
        viewModelScope.launch {
            val userId = dataStore.getUserId()
            if (userId != null) repository.clearRead(userId)
        }
    }
}
