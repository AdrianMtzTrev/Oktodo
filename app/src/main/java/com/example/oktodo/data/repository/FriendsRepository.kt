package com.example.oktodo.data.repository

import androidx.compose.ui.graphics.Color
import androidx.room.Transaction
import com.example.oktodo.data.local.dao.FriendDao
import com.example.oktodo.data.local.dao.FriendRequestDao
import com.example.oktodo.data.local.dao.GroupDao
import com.example.oktodo.data.local.dao.GroupInvitationDao
import com.example.oktodo.data.local.dao.SharedEventDao
import com.example.oktodo.data.local.dao.UserProfileDao
import com.example.oktodo.data.local.entity.FriendEntity
import com.example.oktodo.data.local.entity.FriendRequestEntity
import com.example.oktodo.data.local.entity.GroupEntity
import com.example.oktodo.data.local.entity.GroupInvitationEntity
import com.example.oktodo.data.local.entity.SharedEventEntity
import com.example.oktodo.data.local.entity.UserProfileEntity
import com.example.oktodo.data.local.mapper.toDomain
import com.example.oktodo.data.local.mapper.toEntity
import com.example.oktodo.ui.model.Friend
import com.example.oktodo.ui.model.FriendRequest
import com.example.oktodo.ui.model.Group
import com.example.oktodo.ui.model.GroupInvitation
import com.example.oktodo.ui.model.SharedEvent
import com.example.oktodo.ui.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.security.SecureRandom
import java.util.UUID
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FriendsRepository @Inject constructor(
    private val friendDao: FriendDao,
    private val groupDao: GroupDao,
    private val sharedEventDao: SharedEventDao,
    private val userProfileDao: UserProfileDao,
    private val friendRequestDao: FriendRequestDao,
    private val groupInvitationDao: GroupInvitationDao
) {
    val friends: Flow<List<Friend>> = friendDao.getAllFriends().map { list -> list.map { it.toDomain() } }
    val groups: Flow<List<Group>> = groupDao.getAllGroups().map { list -> list.map { it.toDomain() } }
    val sharedEvents: Flow<List<SharedEvent>> = sharedEventDao.getAllEvents().map { list -> list.map { it.toDomain() } }
    val userProfiles: Flow<List<UserProfile>> = userProfileDao.getAllProfiles().map { list -> list.map { it.toDomain() } }

    suspend fun addGroup(group: Group) = groupDao.insert(group.toEntity())
    suspend fun updateGroup(group: Group) = groupDao.update(group.toEntity())
    suspend fun incrementGroupEventCount(groupId: String) = groupDao.incrementEventCount(groupId)
    suspend fun addSharedEvent(event: SharedEvent) = sharedEventDao.insert(event.toEntity())
    suspend fun updateSharedEvent(id: String, title: String, date: String, time: String, location: String) =
        sharedEventDao.update(id, title, date, time, location)
    suspend fun setEventEditors(eventId: String, editors: List<String>) =
        sharedEventDao.setEditors(eventId, editors.joinToString(","))
    suspend fun deleteSharedEvent(id: String) = sharedEventDao.deleteById(id)

    fun searchUsers(query: String): Flow<List<UserProfile>> =
        userProfileDao.search(query).map { list -> list.map { it.toDomain() } }

    suspend fun isUsernameTaken(username: String): Boolean =
        userProfileDao.isUsernameTaken(username)

    private fun hashPassword(password: String, salt: String): String {
        val spec = PBEKeySpec(password.toCharArray(), salt.toByteArray(), 600_000, 256)
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val hash = factory.generateSecret(spec).encoded
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun generateSalt(): String {
        val salt = ByteArray(32)
        SecureRandom().nextBytes(salt)
        return salt.joinToString("") { "%02x".format(it) }
    }

    suspend fun registerUser(profile: UserProfile, password: String) {
        val salt = generateSalt()
        val hash = hashPassword(password, salt)
        val entity = profile.toEntity().copy(passwordHash = "$salt:$hash")
        userProfileDao.insert(entity)
    }

    suspend fun loginUser(username: String, password: String): UserProfile? {
        val entity = userProfileDao.findByUsername(username.lowercase().replace(" ", "_")) ?: return null
        val parts = entity.passwordHash.split(":")
        if (parts.size != 2) return null
        val (salt, storedHash) = parts
        return if (hashPassword(password, salt) == storedHash) entity.toDomain() else null
    }

    // ── Friend requests ─────────────────────────────────

    fun getPendingIncoming(userId: String): Flow<List<FriendRequest>> =
        friendRequestDao.getPendingIncoming(userId).map { list -> list.map { it.toDomain() } }

    fun getOutgoing(userId: String): Flow<List<FriendRequest>> =
        friendRequestDao.getOutgoing(userId).map { list -> list.map { it.toDomain() } }

    suspend fun sendFriendRequest(
        fromUserId: String, toUserId: String,
        fromDisplayName: String, fromAvatarEmoji: String,
        toDisplayName: String, toAvatarEmoji: String
    ) {
        friendRequestDao.insert(
            FriendRequestEntity(
                id = UUID.randomUUID().toString(),
                fromUserId = fromUserId,
                toUserId = toUserId,
                fromDisplayName = fromDisplayName,
                fromAvatarEmoji = fromAvatarEmoji,
                toDisplayName = toDisplayName,
                toAvatarEmoji = toAvatarEmoji,
                status = "pending"
            )
        )
    }

    suspend fun deleteSharedEventsByGroupId(groupId: String) = sharedEventDao.deleteByGroupId(groupId)

    @Transaction
    suspend fun acceptFriendRequest(request: FriendRequest) {
        friendRequestDao.updateStatus(request.id, "accepted")
        friendDao.insert(
            FriendEntity(
                id = request.fromUserId,
                name = request.fromDisplayName,
                points = 0,
                events = 0,
                avatar = request.fromAvatarEmoji,
                avatarColorArgb = Color(0xFFC4B5FD).value.toLong()
            )
        )
    }

    suspend fun declineFriendRequest(requestId: String) {
        friendRequestDao.updateStatus(requestId, "declined")
    }

    suspend fun seedDemoFriendRequests(currentUserId: String) {
        listOf(
            FriendRequestEntity(
                id = UUID.randomUUID().toString(),
                fromUserId = "u1",
                toUserId = currentUserId,
                fromDisplayName = "María García",
                fromAvatarEmoji = "👩",
                toDisplayName = "",
                toAvatarEmoji = "",
                status = "pending"
            ),
            FriendRequestEntity(
                id = UUID.randomUUID().toString(),
                fromUserId = "u5",
                toUserId = currentUserId,
                fromDisplayName = "Sofía Torres",
                fromAvatarEmoji = "👧",
                toDisplayName = "",
                toAvatarEmoji = "",
                status = "pending"
            )
        ).forEach { friendRequestDao.insert(it) }
    }

    // ── Group invitations ────────────────────────────────

    fun getPendingGroupInvitations(userId: String): Flow<List<GroupInvitation>> =
        groupInvitationDao.getPendingForUser(userId).map { list -> list.map { it.toDomain() } }

    suspend fun sendGroupInvitation(
        fromUserId: String,
        fromDisplayName: String,
        toUserId: String,
        toDisplayName: String,
        group: Group
    ) {
        val existing = groupInvitationDao.findExisting(toUserId, group.id)
        if (existing != null) return
        groupInvitationDao.insert(
            GroupInvitationEntity(
                id = UUID.randomUUID().toString(),
                fromUserId = fromUserId,
                fromDisplayName = fromDisplayName,
                toUserId = toUserId,
                toDisplayName = toDisplayName,
                groupId = group.id,
                groupName = group.name,
                groupIcon = group.icon,
                status = "pending"
            )
        )
    }

    suspend fun acceptGroupInvitation(invitation: GroupInvitation) {
        groupInvitationDao.updateStatus(invitation.id, "accepted")
        val group = groupDao.getGroupById(invitation.groupId) ?: return
        val currentMembers = group.membersJoined.split(",").filter { it.isNotBlank() }.toMutableList()
        val currentIds = group.memberIdsJoined.split(",").filter { it.isNotBlank() }.toMutableList()
        if (invitation.toDisplayName !in currentMembers) currentMembers.add(invitation.toDisplayName)
        if (invitation.toUserId !in currentIds) currentIds.add(invitation.toUserId)
        groupDao.update(group.copy(
            membersJoined = currentMembers.joinToString(","),
            memberIdsJoined = currentIds.joinToString(",")
        ))
    }

    suspend fun declineGroupInvitation(invitationId: String) {
        groupInvitationDao.updateStatus(invitationId, "declined")
    }

    suspend fun renameGroup(groupId: String, newName: String) {
        val entity = groupDao.getGroupById(groupId) ?: return
        groupDao.update(entity.copy(name = newName))
    }

    suspend fun deleteGroup(groupId: String) {
        val entity = groupDao.getGroupById(groupId) ?: return
        sharedEventDao.deleteByGroupId(groupId)
        groupDao.delete(entity)
    }

    // ── Seed ────────────────────────────────────────────

    suspend fun seedIfEmpty() {
        if (userProfileDao.count() > 0) return

        val seedSalt = generateSalt()
        val seedHash = hashPassword("oktodo123", seedSalt)
        val stored = "$seedSalt:$seedHash"

        listOf(
            UserProfileEntity("u1", "María García", "mariagarcia", "👩", stored),
            UserProfileEntity("u2", "Carlos López", "carloslopez", "👱", stored),
            UserProfileEntity("u3", "Ana Martínez", "anamartinez", "👩‍🦰", stored),
            UserProfileEntity("u4", "Luis Rodríguez", "luisrodriguez", "👦", stored),
            UserProfileEntity("u5", "Sofía Torres", "sofiatorres", "👧", stored),
            UserProfileEntity("u6", "Diego Ramírez", "diegoramirez", "🧑", stored),
            UserProfileEntity("u7", "Valentina Cruz", "valentinacruz", "👩‍🦱", stored),
            UserProfileEntity("u8", "Mateo Hernández", "mateohernandez", "👨", stored)
        ).forEach { userProfileDao.insert(it) }
    }
}
