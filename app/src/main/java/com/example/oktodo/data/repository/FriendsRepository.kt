package com.example.oktodo.data.repository

import androidx.compose.ui.graphics.Color
import com.example.oktodo.data.local.dao.FriendDao
import com.example.oktodo.data.local.dao.GroupDao
import com.example.oktodo.data.local.dao.SharedEventDao
import com.example.oktodo.data.local.dao.UserProfileDao
import com.example.oktodo.data.local.entity.FriendEntity
import com.example.oktodo.data.local.entity.GroupEntity
import com.example.oktodo.data.local.entity.SharedEventEntity
import com.example.oktodo.data.local.entity.UserProfileEntity
import com.example.oktodo.data.local.mapper.toDomain
import com.example.oktodo.data.local.mapper.toEntity
import com.example.oktodo.ui.model.Friend
import com.example.oktodo.ui.model.Group
import com.example.oktodo.ui.model.SharedEvent
import com.example.oktodo.ui.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendsRepository @Inject constructor(
    private val friendDao: FriendDao,
    private val groupDao: GroupDao,
    private val sharedEventDao: SharedEventDao,
    private val userProfileDao: UserProfileDao
) {
    val friends: Flow<List<Friend>> = friendDao.getAllFriends().map { list -> list.map { it.toDomain() } }
    val groups: Flow<List<Group>> = groupDao.getAllGroups().map { list -> list.map { it.toDomain() } }
    val sharedEvents: Flow<List<SharedEvent>> = sharedEventDao.getAllEvents().map { list -> list.map { it.toDomain() } }
    val userProfiles: Flow<List<UserProfile>> = userProfileDao.getAllProfiles().map { list -> list.map { it.toDomain() } }

    suspend fun addGroup(group: Group) = groupDao.insert(group.toEntity())
    suspend fun updateGroup(group: Group) = groupDao.update(group.toEntity())
    suspend fun addSharedEvent(event: SharedEvent) = sharedEventDao.insert(event.toEntity())
    suspend fun deleteSharedEvent(id: String) = sharedEventDao.deleteById(id)

    fun searchUsers(query: String): Flow<List<UserProfile>> =
        userProfileDao.search(query).map { list -> list.map { it.toDomain() } }

    suspend fun isUsernameTaken(username: String): Boolean =
        userProfileDao.isUsernameTaken(username)

    suspend fun registerUser(profile: UserProfile) =
        userProfileDao.insert(profile.toEntity())

    suspend fun seedIfEmpty() {
        if (userProfileDao.count() > 0) return

        listOf(
            UserProfileEntity("u1", "María García", "mariagarcia", "👩"),
            UserProfileEntity("u2", "Carlos López", "carloslopez", "👱"),
            UserProfileEntity("u3", "Ana Martínez", "anamartinez", "👩‍🦰"),
            UserProfileEntity("u4", "Luis Rodríguez", "luisrodriguez", "👦"),
            UserProfileEntity("u5", "Sofía Torres", "sofiatorres", "👧"),
            UserProfileEntity("u6", "Diego Ramírez", "diegoramirez", "🧑"),
            UserProfileEntity("u7", "Valentina Cruz", "valentinacruz", "👩‍🦱"),
            UserProfileEntity("u8", "Mateo Hernández", "mateohernandez", "👨")
        ).forEach { userProfileDao.insert(it) }

        if (friendDao.count() > 0) return

        listOf(
            FriendEntity("1", "María García", 280, 3, "👩", Color(0xFFC4B5FD).value.toLong()),
            FriendEntity("2", "Carlos López", 195, 2, "👱", Color(0xFFDDD6FE).value.toLong()),
            FriendEntity("3", "Ana Martínez", 340, 5, "👩‍🦰", Color(0xFFE9D5FF).value.toLong()),
            FriendEntity("4", "Luis Rodríguez", 150, 1, "👦", Color(0xFFD8B4FE).value.toLong())
        ).forEach { friendDao.insert(it) }

        listOf(
            GroupEntity("g1", "Equipo Estudio", "📚", "María García,Ana Martínez,Tú", 2),
            GroupEntity("g2", "Deportistas", "⚽", "Carlos López,Luis Rodríguez,Tú", 1)
        ).forEach { groupDao.insert(it) }

        listOf(
            SharedEventEntity("e1", "Cine con amigos", "María García", "g1", "2026-03-16", "19:00", "Cinépolis Centro", "María García,Carlos López,Tú"),
            SharedEventEntity("e2", "Estudio grupal", "Ana Martínez", "g1", "2026-03-18", "15:00", "Biblioteca Universidad", "Ana Martínez,Luis Rodríguez,Tú"),
            SharedEventEntity("e3", "Entrenamiento", "Carlos López", "g2", "2026-03-20", "07:00", "Unidad Deportiva", "Carlos López,Luis Rodríguez,Tú")
        ).forEach { sharedEventDao.insert(it) }
    }
}
