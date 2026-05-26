package com.example.oktodo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.oktodo.data.local.dao.*
import com.example.oktodo.data.local.entity.*

@Database(
    entities = [
        TaskEntity::class,
        CalendarEventEntity::class,
        FriendEntity::class,
        GroupEntity::class,
        SharedEventEntity::class,
        ShopItemEntity::class,
        NotificationEntity::class,
        UserProfileEntity::class,
        FriendRequestEntity::class,
        GroupInvitationEntity::class
    ],
    version = 16,
    exportSchema = false
)
abstract class OktodoDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun calendarEventDao(): CalendarEventDao
    abstract fun friendDao(): FriendDao
    abstract fun groupDao(): GroupDao
    abstract fun sharedEventDao(): SharedEventDao
    abstract fun shopItemDao(): ShopItemDao
    abstract fun notificationDao(): NotificationDao
    abstract fun userProfileDao(): UserProfileDao
    abstract fun friendRequestDao(): FriendRequestDao
    abstract fun groupInvitationDao(): GroupInvitationDao
}
