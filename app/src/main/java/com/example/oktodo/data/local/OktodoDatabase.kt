package com.example.oktodo.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
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
        NotificationEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class OktodoDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun calendarEventDao(): CalendarEventDao
    abstract fun friendDao(): FriendDao
    abstract fun groupDao(): GroupDao
    abstract fun sharedEventDao(): SharedEventDao
    abstract fun shopItemDao(): ShopItemDao
    abstract fun notificationDao(): NotificationDao
}
