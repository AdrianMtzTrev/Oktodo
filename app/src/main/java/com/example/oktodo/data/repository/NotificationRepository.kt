package com.example.oktodo.data.repository

import com.example.oktodo.data.local.dao.NotificationDao
import com.example.oktodo.data.local.mapper.toDomain
import com.example.oktodo.data.local.mapper.toEntity
import com.example.oktodo.ui.model.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val dao: NotificationDao
) {
    fun getNotificationsForUser(userId: String): Flow<List<Notification>> =
        dao.getNotificationsForUser(userId).map { list -> list.map { it.toDomain() } }

    fun getUnreadCount(userId: String): Flow<Int> = dao.getUnreadCount(userId)

    suspend fun add(notification: Notification) {
        dao.insert(notification.toEntity())
    }

    suspend fun markAsRead(id: String) = dao.markAsRead(id)

    suspend fun markAllAsRead(userId: String) = dao.markAllAsRead(userId)

    suspend fun delete(notification: Notification) {
        dao.delete(notification.toEntity())
    }

    suspend fun clearRead(userId: String) = dao.clearRead(userId)
}
