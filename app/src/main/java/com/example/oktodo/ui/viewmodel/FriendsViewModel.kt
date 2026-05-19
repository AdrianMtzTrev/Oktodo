package com.example.oktodo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oktodo.data.local.UserPreferencesDataStore
import com.example.oktodo.data.repository.FriendsRepository
import com.example.oktodo.ui.model.Group
import com.example.oktodo.ui.model.SharedEvent
import com.example.oktodo.ui.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID
import javax.inject.Inject

data class SocialState(
    val isRegistered: Boolean = false,
    val displayName: String = "Usuario OKTodo",
    val avatarEmoji: String = "🐙",
    val authError: String? = null,
    val isAuthLoading: Boolean = false,
    val isSignupMode: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: FriendsRepository,
    private val prefs: UserPreferencesDataStore
) : ViewModel() {

    val friends = repository.friends
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val groups = repository.groups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val sharedEvents = repository.sharedEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfiles = repository.userProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _socialState = MutableStateFlow(SocialState())
    val socialState: StateFlow<SocialState> = _socialState

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val searchResults: StateFlow<List<UserProfile>> = _searchQuery
        .flatMapLatest { query ->
            if (query.isBlank()) flowOf(emptyList())
            else repository.searchUsers(query)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setSearchQuery(query: String) { _searchQuery.value = query }

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
        viewModelScope.launch {
            prefs.preferences.collect { prefs ->
                _socialState.value = _socialState.value.copy(
                    isRegistered = prefs.isSocialRegistered,
                    displayName = prefs.displayName,
                    avatarEmoji = prefs.avatarEmoji
                )
            }
        }
    }

    fun toggleMode() {
        _socialState.value = _socialState.value.copy(
            isSignupMode = !_socialState.value.isSignupMode,
            authError = null
        )
    }

    fun signup(username: String, password: String, confirmPassword: String) {
        viewModelScope.launch {
            _socialState.value = _socialState.value.copy(isAuthLoading = true, authError = null)

            val cleanUsername = username.trim().lowercase().replace(" ", "_")

            if (cleanUsername.isBlank()) {
                _socialState.value = _socialState.value.copy(
                    authError = "El nombre de usuario no puede estar vacío",
                    isAuthLoading = false
                )
                return@launch
            }

            if (password.length < 4) {
                _socialState.value = _socialState.value.copy(
                    authError = "La contraseña debe tener al menos 4 caracteres",
                    isAuthLoading = false
                )
                return@launch
            }

            if (password != confirmPassword) {
                _socialState.value = _socialState.value.copy(
                    authError = "Las contraseñas no coinciden",
                    isAuthLoading = false
                )
                return@launch
            }

            if (repository.isUsernameTaken(cleanUsername)) {
                _socialState.value = _socialState.value.copy(
                    authError = "Ese nombre de usuario ya está en uso",
                    isAuthLoading = false
                )
                return@launch
            }

            val userId = UUID.randomUUID().toString()
            val profile = UserProfile(
                id = userId,
                displayName = _socialState.value.displayName,
                username = cleanUsername,
                avatarEmoji = _socialState.value.avatarEmoji
            )
            repository.registerUser(profile, password)
            prefs.registerSocial(userId, profile.displayName, profile.username, profile.avatarEmoji)
            _socialState.value = _socialState.value.copy(isAuthLoading = false)
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _socialState.value = _socialState.value.copy(isAuthLoading = true, authError = null)

            val cleanUsername = username.trim().lowercase().replace(" ", "_")

            if (cleanUsername.isBlank()) {
                _socialState.value = _socialState.value.copy(
                    authError = "Ingresa tu nombre de usuario",
                    isAuthLoading = false
                )
                return@launch
            }

            if (password.isBlank()) {
                _socialState.value = _socialState.value.copy(
                    authError = "Ingresa tu contraseña",
                    isAuthLoading = false
                )
                return@launch
            }

            val profile = repository.loginUser(cleanUsername, password)
            if (profile != null) {
                prefs.registerSocial(
                    profile.id, profile.displayName, profile.username, profile.avatarEmoji
                )
            } else {
                _socialState.value = _socialState.value.copy(
                    authError = "Usuario o contraseña incorrectos",
                    isAuthLoading = false
                )
            }
        }
    }

    fun createGroup(name: String, members: List<String>) {
        viewModelScope.launch {
            repository.addGroup(
                Group(
                    id = "g${System.currentTimeMillis()}",
                    name = name,
                    icon = "👥",
                    members = members + "Tú",
                    eventCount = 0
                )
            )
        }
    }

    fun createSharedEvent(title: String, groupId: String, date: String, time: String, location: String) {
        viewModelScope.launch {
            val group = groups.value.find { it.id == groupId } ?: return@launch
            val event = SharedEvent(
                id = "e${System.currentTimeMillis()}",
                title = title,
                creator = "Tú",
                groupId = groupId,
                date = date,
                time = time,
                location = location,
                participants = group.members
            )
            repository.addSharedEvent(event)
            repository.updateGroup(group.copy(eventCount = group.eventCount + 1))
        }
    }

    fun getGroupById(groupId: String) = groups.value.find { it.id == groupId }

    fun getEventsForGroup(groupId: String) = sharedEvents.value.filter { it.groupId == groupId }

    fun getEventsForGroupOnDate(groupId: String, date: LocalDate) =
        sharedEvents.value.filter { it.groupId == groupId && it.date == date.toString() }

    fun getFormattedDateLabel(rawDate: String): String = try {
        val date = LocalDate.parse(rawDate)
        date.format(DateTimeFormatter.ofPattern("EEE, d MMM"))
    } catch (_: Exception) { rawDate }
}
