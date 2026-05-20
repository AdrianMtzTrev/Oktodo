package com.example.oktodo.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.temporal.WeekFields
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

data class UserPreferences(
    val displayName: String = "Usuario OKTodo",
    val username: String = "",
    val avatarEmoji: String = "🐙",
    val points: Int = 0,
    val completedTasks: Int = 0,
    val streakDays: Int = 0,
    val weeklyGoal: Int = 10,
    val weeklyCompleted: Int = 0,
    val isDarkMode: Boolean = false,
    val lastActiveDate: String = "",
    val isSocialRegistered: Boolean = false,
    val userId: String = "",
    val lastWeekReset: String = ""
)

@Singleton
class UserPreferencesDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object Keys {
        val DISPLAY_NAME = stringPreferencesKey("display_name")
        val USERNAME = stringPreferencesKey("username")
        val AVATAR = stringPreferencesKey("avatar_emoji")
        val POINTS = intPreferencesKey("points")
        val COMPLETED_TASKS = intPreferencesKey("completed_tasks")
        val STREAK = intPreferencesKey("streak_days")
        val WEEKLY_GOAL = intPreferencesKey("weekly_goal")
        val WEEKLY_COMPLETED = intPreferencesKey("weekly_completed")
        val DARK_MODE = booleanPreferencesKey("dark_mode")
        val LAST_ACTIVE_DATE = stringPreferencesKey("last_active_date")
        val NOTIFIED_ACHIEVEMENTS = stringPreferencesKey("notified_achievements")
        val IS_SOCIAL_REGISTERED = booleanPreferencesKey("is_social_registered")
        val USER_ID = stringPreferencesKey("user_id")
        val LAST_WEEK_RESET = stringPreferencesKey("last_week_reset")
    }

    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        UserPreferences(
            displayName = prefs[Keys.DISPLAY_NAME] ?: "Usuario OKTodo",
            username = prefs[Keys.USERNAME] ?: "",
            avatarEmoji = prefs[Keys.AVATAR] ?: "🐙",
            points = prefs[Keys.POINTS] ?: 0,
            completedTasks = prefs[Keys.COMPLETED_TASKS] ?: 0,
            streakDays = prefs[Keys.STREAK] ?: 0,
            weeklyGoal = prefs[Keys.WEEKLY_GOAL] ?: 10,
            weeklyCompleted = prefs[Keys.WEEKLY_COMPLETED] ?: 0,
            isDarkMode = prefs[Keys.DARK_MODE] ?: false,
            lastActiveDate = prefs[Keys.LAST_ACTIVE_DATE] ?: "",
            isSocialRegistered = prefs[Keys.IS_SOCIAL_REGISTERED] ?: false,
            userId = prefs[Keys.USER_ID] ?: "",
            lastWeekReset = prefs[Keys.LAST_WEEK_RESET] ?: ""
        )
    }

    suspend fun updateProfile(displayName: String, username: String, avatarEmoji: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DISPLAY_NAME] = displayName
            prefs[Keys.USERNAME] = username
            prefs[Keys.AVATAR] = avatarEmoji
        }
    }

    suspend fun registerSocial(
        userId: String,
        displayName: String,
        username: String,
        avatarEmoji: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.USER_ID] = userId
            prefs[Keys.DISPLAY_NAME] = displayName
            prefs[Keys.USERNAME] = username
            prefs[Keys.AVATAR] = avatarEmoji
            prefs[Keys.IS_SOCIAL_REGISTERED] = true
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

    suspend fun updateStreak() {
        val today = LocalDate.now().toString()
        val yesterday = LocalDate.now().minusDays(1).toString()
        context.dataStore.edit { prefs ->
            val lastDate = prefs[Keys.LAST_ACTIVE_DATE] ?: ""
            if (lastDate == today) return@edit
            prefs[Keys.LAST_ACTIVE_DATE] = today
            prefs[Keys.STREAK] = when (lastDate) {
                yesterday -> (prefs[Keys.STREAK] ?: 0) + 1
                else      -> 1
            }
        }
    }

    val notifiedAchievements: Flow<Set<String>> = context.dataStore.data.map { prefs ->
        val raw = prefs[Keys.NOTIFIED_ACHIEVEMENTS] ?: ""
        if (raw.isBlank()) emptySet() else raw.split(",").toSet()
    }

    val userId: Flow<String> = preferences.map { it.userId }

    suspend fun getUserId(): String? {
        return context.dataStore.data.first()[Keys.USER_ID]
    }

    suspend fun markAchievementNotified(title: String) {
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.NOTIFIED_ACHIEVEMENTS] ?: ""
            val titles = if (current.isBlank()) emptySet() else current.split(",").toSet()
            prefs[Keys.NOTIFIED_ACHIEVEMENTS] = (titles + title).joinToString(",")
        }
    }

    suspend fun purchaseItem(cost: Int): Boolean {
        var success = false
        context.dataStore.edit { prefs ->
            val current = prefs[Keys.POINTS] ?: 0
            if (current >= cost) {
                prefs[Keys.POINTS] = current - cost
                success = true
            }
        }
        return success
    }

    suspend fun setWeeklyGoal(goal: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.WEEKLY_GOAL] = goal.coerceIn(1, 30)
        }
    }

    suspend fun checkWeeklyReset() {
        val currentWeek = LocalDate.now().let {
            "${it.year}-W${it.get(WeekFields.ISO.weekOfWeekBasedYear())}"
        }
        context.dataStore.edit { prefs ->
            val lastReset = prefs[Keys.LAST_WEEK_RESET] ?: ""
            if (lastReset != currentWeek) {
                prefs[Keys.WEEKLY_COMPLETED] = 0
                prefs[Keys.LAST_WEEK_RESET] = currentWeek
            }
        }
    }

    suspend fun logoutSocial() {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_SOCIAL_REGISTERED] = false
        }
    }
}
