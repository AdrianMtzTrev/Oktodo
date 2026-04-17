package com.example.oktodo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class UserPreferences(
    val username: String = "Usuario OKTodo",
    val avatarEmoji: String = "🐙",
    val points: Int = 0,
    val completedTasks: Int = 0,
    val streakDays: Int = 5,
    val weeklyGoal: Int = 10,
    val weeklyCompleted: Int = 0,
    val isDarkMode: Boolean = false
)

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val USERNAME = stringPreferencesKey("username")
        val AVATAR = stringPreferencesKey("avatar_emoji")
        val POINTS = intPreferencesKey("points")
        val COMPLETED_TASKS = intPreferencesKey("completed_tasks")
        val STREAK = intPreferencesKey("streak_days")
        val WEEKLY_GOAL = intPreferencesKey("weekly_goal")
        val WEEKLY_COMPLETED = intPreferencesKey("weekly_completed")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            username = prefs[Keys.USERNAME] ?: "Usuario OKTodo",
            avatarEmoji = prefs[Keys.AVATAR] ?: "🐙",
            points = prefs[Keys.POINTS] ?: 0,
            completedTasks = prefs[Keys.COMPLETED_TASKS] ?: 0,
            streakDays = prefs[Keys.STREAK] ?: 5,
            weeklyGoal = prefs[Keys.WEEKLY_GOAL] ?: 10,
            weeklyCompleted = prefs[Keys.WEEKLY_COMPLETED] ?: 0,
            isDarkMode = prefs[Keys.DARK_MODE] ?: false
        )
    }

    suspend fun updateProfile(username: String, avatarEmoji: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USERNAME] = username
            prefs[Keys.AVATAR] = avatarEmoji
        }
    }

    suspend fun addPoints(delta: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.POINTS] ?: 0
            prefs[Keys.POINTS] = maxOf(0, current + delta)
            if (delta > 0) {
                prefs[Keys.COMPLETED_TASKS] = (prefs[Keys.COMPLETED_TASKS] ?: 0) + 1
                prefs[Keys.WEEKLY_COMPLETED] = (prefs[Keys.WEEKLY_COMPLETED] ?: 0) + 1
            } else {
                prefs[Keys.COMPLETED_TASKS] = maxOf(0, (prefs[Keys.COMPLETED_TASKS] ?: 0) - 1)
                prefs[Keys.WEEKLY_COMPLETED] = maxOf(0, (prefs[Keys.WEEKLY_COMPLETED] ?: 0) - 1)
            }
        }
    }

    suspend fun toggleDarkMode() {
        context.dataStore.edit { prefs ->
            prefs[Keys.DARK_MODE] = !(prefs[Keys.DARK_MODE] ?: false)
        }
    }

    suspend fun purchaseItem(cost: Int) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.POINTS] ?: 0
            if (current >= cost) prefs[Keys.POINTS] = current - cost
        }
    }
}
