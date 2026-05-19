package com.example.oktodo.data.repository

import com.example.oktodo.data.local.dao.NotificationDao
import com.example.oktodo.data.local.entity.NotificationEntity
import com.example.oktodo.data.local.mapper.toDomain
import com.example.oktodo.ui.model.Notification
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepository @Inject constructor(
    private val dao: NotificationDao
) {
    val notifications: Flow<List<Notification>> =
        dao.getAllNotifications().map { list -> list.map { it.toDomain() } }

    val unreadCount: Flow<Int> = dao.getUnreadCount()

    suspend fun add(notification: Notification) {
        dao.insert(
            NotificationEntity(
                id = notification.id,
                title = notification.title,
                message = notification.message,
                icon = notification.icon,
                isRead = notification.isRead,
                createdAt = notification.createdAt
            )
        )
    }

    suspend fun markAsRead(id: String) = dao.markAsRead(id)

    suspend fun markAllAsRead() = dao.markAllAsRead()

    suspend fun delete(notification: Notification) {
        dao.delete(
            NotificationEntity(
                id = notification.id,
                title = notification.title,
                message = notification.message,
                icon = notification.icon,
                isRead = notification.isRead,
                createdAt = notification.createdAt
            )
        )
    }

    suspend fun clearRead() = dao.clearRead()
}
