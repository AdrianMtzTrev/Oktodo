package com.example.oktodo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oktodo.data.repository.NotificationRepository
import com.example.oktodo.ui.model.Notification
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val repository: NotificationRepository
) : ViewModel() {

    val notifications = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val unreadCount = repository.unreadCount
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun markAsRead(id: String) {
        viewModelScope.launch { repository.markAsRead(id) }
    }

    fun markAllAsRead() {
        viewModelScope.launch { repository.markAllAsRead() }
    }

    fun delete(notification: Notification) {
        viewModelScope.launch { repository.delete(notification) }
    }

    fun clearRead() {
        viewModelScope.launch { repository.clearRead() }
    }
}
