package com.example.oktodo.di

import android.content.Context
import androidx.room.Room
import com.example.oktodo.data.local.OktodoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): OktodoDatabase =
        Room.databaseBuilder(context, OktodoDatabase::class.java, "oktodo.db").build()

    @Provides fun provideTaskDao(db: OktodoDatabase) = db.taskDao()
    @Provides fun provideCalendarEventDao(db: OktodoDatabase) = db.calendarEventDao()
    @Provides fun provideFriendDao(db: OktodoDatabase) = db.friendDao()
    @Provides fun provideGroupDao(db: OktodoDatabase) = db.groupDao()
    @Provides fun provideSharedEventDao(db: OktodoDatabase) = db.sharedEventDao()
    @Provides fun provideShopItemDao(db: OktodoDatabase) = db.shopItemDao()
}
