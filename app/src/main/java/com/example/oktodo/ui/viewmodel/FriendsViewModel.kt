package com.example.oktodo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oktodo.data.local.UserPreferencesDataStore
import com.example.oktodo.data.repository.FriendsRepository
import com.example.oktodo.data.repository.NotificationRepository
import com.example.oktodo.ui.model.Friend
import com.example.oktodo.ui.model.FriendRequest
import com.example.oktodo.ui.model.Group
import com.example.oktodo.ui.model.GroupInvitation
import com.example.oktodo.ui.model.Notification
import com.example.oktodo.ui.model.SharedEvent
import com.example.oktodo.ui.model.UserProfile
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    val userId: String = "",
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
    private val prefs: UserPreferencesDataStore,
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    val friends = repository.friends
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groups: StateFlow<List<Group>> = combine(
        repository.groups,
        prefs.preferences
    ) { allGroups, p ->
        allGroups.filter { group ->
            group.creatorId == p.userId || group.members.contains(p.displayName)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    val sharedEvents = repository.sharedEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfiles = repository.userProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _socialState = MutableStateFlow(SocialState())
    val socialState: StateFlow<SocialState> = _socialState

    val pendingIncoming: StateFlow<List<FriendRequest>> = _socialState
        .flatMapLatest { state ->
            if (state.isRegistered && state.userId.isNotBlank())
                repository.getPendingIncoming(state.userId)
            else
                flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingOutgoing: StateFlow<List<FriendRequest>> = _socialState
        .flatMapLatest { state ->
            if (state.isRegistered && state.userId.isNotBlank())
                repository.getOutgoing(state.userId)
            else
                flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pendingGroupInvitations: StateFlow<List<GroupInvitation>> = _socialState
        .flatMapLatest { state ->
            if (state.isRegistered && state.userId.isNotBlank())
                repository.getPendingGroupInvitations(state.userId)
            else
                flowOf(emptyList())
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

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
                    userId = prefs.userId,
                    displayName = prefs.displayName,
                    avatarEmoji = prefs.avatarEmoji
                )
            }
        }
    }

    fun sendFriendRequest(target: UserProfile) {
        viewModelScope.launch {
            val state = _socialState.value
            if (!state.isRegistered || state.userId.isBlank()) return@launch
            try {
                repository.sendFriendRequest(
                    fromUserId = state.userId,
                    toUserId = target.id,
                    fromDisplayName = state.displayName,
                    fromAvatarEmoji = state.avatarEmoji,
                    toDisplayName = target.displayName,
                    toAvatarEmoji = target.avatarEmoji
                )
                notificationRepository.add(
                    Notification(
                        userId = state.userId,
                        title = "Solicitud de amistad",
                        message = "Solicitud enviada a @${target.username}",
                        icon = "👤"
                    )
                )
                notificationRepository.add(
                    Notification(
                        userId = target.id,
                        title = "Solicitud de amistad",
                        message = "${state.displayName} quiere ser tu amigo",
                        icon = "👤"
                    )
                )
            } catch (_: Exception) { }
        }
    }

    fun acceptFriendRequest(request: FriendRequest) {
        viewModelScope.launch {
            repository.acceptFriendRequest(request)
        }
    }

    fun declineFriendRequest(request: FriendRequest) {
        viewModelScope.launch {
            repository.declineFriendRequest(request.id)
        }
    }

    fun sendGroupInvitation(friend: Friend, group: Group) {
        viewModelScope.launch {
            val state = _socialState.value
            val fromId = state.userId.ifBlank { "local" }
            if (group.members.contains(friend.name)) return@launch
            try {
                repository.sendGroupInvitation(
                    fromUserId = fromId,
                    fromDisplayName = state.displayName,
                    toUserId = friend.id,
                    toDisplayName = friend.name,
                    group = group
                )
                if (state.userId.isNotBlank()) {
                    notificationRepository.add(
                        Notification(
                            userId = state.userId,
                            title = "Invitación enviada",
                            message = "Invitaste a ${friend.name} al grupo ${group.name}",
                            icon = group.icon
                        )
                    )
                }
                notificationRepository.add(
                    Notification(
                        userId = friend.id,
                        title = "Invitación a grupo",
                        message = "${state.displayName} te invitó al grupo ${group.name}",
                        icon = group.icon
                    )
                )
            } catch (_: Exception) { }
        }
    }

    fun acceptGroupInvitation(invitation: GroupInvitation) {
        viewModelScope.launch {
            repository.acceptGroupInvitation(invitation)
        }
    }

    fun declineGroupInvitation(invitation: GroupInvitation) {
        viewModelScope.launch {
            repository.declineGroupInvitation(invitation.id)
        }
    }

    fun renameGroup(groupId: String, newName: String) {
        if (newName.isBlank()) return
        viewModelScope.launch { repository.renameGroup(groupId, newName.trim()) }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch { repository.deleteGroup(groupId) }
    }

    fun toggleMode() {
        _socialState.value = _socialState.value.copy(
            isSignupMode = !_socialState.value.isSignupMode,
            authError = null
        )
    }

    fun signup(
        displayName: String,
        avatarEmoji: String,
        username: String,
        password: String,
        confirmPassword: String
    ) {
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
            val cleanName = displayName.ifBlank { cleanUsername }
            val profile = UserProfile(
                id = userId,
                displayName = cleanName,
                username = cleanUsername,
                avatarEmoji = avatarEmoji
            )
            repository.registerUser(profile, password)
            repository.seedDemoFriendRequests(userId)
            notificationRepository.add(
                Notification(
                    userId = userId,
                    title = "Solicitud de amistad",
                    message = "María García quiere ser tu amigo",
                    icon = "👩"
                )
            )
            notificationRepository.add(
                Notification(
                    userId = userId,
                    title = "Solicitud de amistad",
                    message = "Sofía Torres quiere ser tu amigo",
                    icon = "👧"
                )
            )
            prefs.registerSocial(userId, cleanName, profile.username, profile.avatarEmoji)
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
                _socialState.value = _socialState.value.copy(isAuthLoading = false)
            } else {
                _socialState.value = _socialState.value.copy(
                    authError = "Usuario o contraseña incorrectos",
                    isAuthLoading = false
                )
            }
        }
    }

    fun createGroup(name: String, selectedFriendNames: List<String>) {
        viewModelScope.launch {
            val state = _socialState.value
            val fromId = state.userId.ifBlank { "local" }
            val displayName = state.displayName.ifBlank { "Tú" }
            val group = Group(
                id = "g${System.currentTimeMillis()}",
                name = name,
                icon = "👥",
                members = listOf(displayName),
                eventCount = 0,
                creatorId = state.userId
            )
            repository.addGroup(group)
            val friendsToInvite = friends.value.filter { it.name in selectedFriendNames }
            for (friend in friendsToInvite) {
                try {
                    repository.sendGroupInvitation(
                        fromUserId = fromId,
                        fromDisplayName = state.displayName,
                        toUserId = friend.id,
                        toDisplayName = friend.name,
                        group = group
                    )
                    notificationRepository.add(
                        Notification(
                            userId = friend.id,
                            title = "Invitación a grupo",
                            message = "${state.displayName} te invitó al grupo ${group.name}",
                            icon = group.icon
                        )
                    )
                } catch (_: Exception) { }
            }
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
