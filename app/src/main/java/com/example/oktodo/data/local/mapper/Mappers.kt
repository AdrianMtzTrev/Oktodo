package com.example.oktodo.data.local.mapper

import androidx.compose.ui.graphics.Color
import com.example.oktodo.data.local.entity.*
import com.example.oktodo.ui.model.*
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeParseException

// ── Task ──────────────────────────────────────────────
fun TaskEntity.toDomain() = Task(
    id = id,
    title = title,
    time = time,
    priority = priority,
    color = Color(colorArgb.toULong()),
    isCompleted = isCompleted,
    pointsReward = pointsReward,
    category = category,
    recurrenceType = recurrenceType,
    recurrenceInterval = recurrenceInterval,
    date = try { LocalDate.parse(dateString) } catch (_: DateTimeParseException) { LocalDate.now() }
)

fun Task.toEntity() = TaskEntity(
    id = id,
    title = title,
    time = time,
    priority = priority,
    colorArgb = color.value.toLong(),
    isCompleted = isCompleted,
    pointsReward = pointsReward,
    category = category,
    recurrenceType = recurrenceType,
    recurrenceInterval = recurrenceInterval,
    dateString = date.toString()
)

// ── CalendarEvent ─────────────────────────────────────
fun CalendarEventEntity.toDomain() = CalendarEvent(
    id = id,
    title = title,
    date = try { LocalDate.parse(dateString) } catch (_: DateTimeParseException) { LocalDate.MIN },
    time = timeString?.let { try { LocalTime.parse(it) } catch (_: DateTimeParseException) { null } },
    location = location,
    color = Color(colorArgb.toULong()),
    description = description
)

fun CalendarEvent.toEntity() = CalendarEventEntity(
    id = id,
    title = title,
    dateString = date.toString(),
    timeString = time?.toString(),
    location = location,
    colorArgb = color.value.toLong(),
    description = description
)

// ── Friend ────────────────────────────────────────────
fun FriendEntity.toDomain() = Friend(
    id = id,
    name = name,
    points = points,
    events = events,
    avatar = avatar,
    avatarColor = Color(avatarColorArgb.toULong())
)

fun Friend.toEntity() = FriendEntity(
    id = id,
    name = name,
    points = points,
    events = events,
    avatar = avatar,
    avatarColorArgb = avatarColor.value.toLong()
)

// ── Group ─────────────────────────────────────────────
fun GroupEntity.toDomain() = Group(
    id = id,
    name = name,
    icon = icon,
    members = membersJoined.split(",").filter { it.isNotBlank() },
    memberIds = memberIdsJoined.split(",").filter { it.isNotBlank() },
    eventCount = eventCount,
    creatorId = creatorId
)

fun Group.toEntity() = GroupEntity(
    id = id,
    name = name,
    icon = icon,
    membersJoined = members.joinToString(","),
    memberIdsJoined = memberIds.joinToString(","),
    eventCount = eventCount,
    creatorId = creatorId
)

// ── Task → CalendarEvent (para mostrar tasks en el calendario) ──
fun Task.toCalendarEvent() = CalendarEvent(
    id = "task_$id",
    title = "✅ $title",
    date = date,
    time = try { LocalTime.parse(time) } catch (_: DateTimeParseException) { null },
    location = null,
    color = color,
    description = null
)

// ── SharedEvent ───────────────────────────────────────
fun SharedEvent.toCalendarEvent() = CalendarEvent(
    id = "shared_$id",
    title = "👥 $title",
    date = try { LocalDate.parse(date) } catch (_: DateTimeParseException) { LocalDate.MIN },
    time = try { LocalTime.parse(time) } catch (_: DateTimeParseException) { null },
    location = location.ifBlank { null },
    color = Color(0xFF7C3AED),
    description = "Creado por: $creator"
)

fun SharedEventEntity.toDomain() = SharedEvent(
    id = id,
    title = title,
    creator = creator,
    creatorId = creatorId,
    groupId = groupId,
    date = date,
    time = time,
    location = location,
    participants = participantsJoined.split(",").filter { it.isNotBlank() },
    editors = canEditJoined.split(",").filter { it.isNotBlank() }
)

fun SharedEvent.toEntity() = SharedEventEntity(
    id = id,
    title = title,
    creator = creator,
    creatorId = creatorId,
    groupId = groupId,
    date = date,
    time = time,
    location = location,
    participantsJoined = participants.joinToString(","),
    canEditJoined = editors.joinToString(",")
)

// ── Notification ──────────────────────────────────────
fun NotificationEntity.toDomain() = Notification(
    id = id,
    userId = userId,
    title = title,
    message = message,
    icon = icon,
    isRead = isRead,
    createdAt = createdAt
)

fun Notification.toEntity() = NotificationEntity(
    id = id,
    userId = userId,
    title = title,
    message = message,
    icon = icon,
    isRead = isRead,
    createdAt = createdAt
)

// ── UserProfile ───────────────────────────────────────
fun UserProfileEntity.toDomain() = UserProfile(
    id = id,
    displayName = displayName,
    username = username,
    avatarEmoji = avatarEmoji,
    passwordHash = passwordHash,
    createdAt = createdAt
)
fun UserProfile.toEntity() = UserProfileEntity(
    id = id,
    displayName = displayName,
    username = username,
    avatarEmoji = avatarEmoji,
    passwordHash = passwordHash,
    createdAt = createdAt
)

// ── FriendRequest ─────────────────────────────────────
fun FriendRequestEntity.toDomain() = FriendRequest(
    id = id,
    fromUserId = fromUserId,
    toUserId = toUserId,
    fromDisplayName = fromDisplayName,
    fromAvatarEmoji = fromAvatarEmoji,
    toDisplayName = toDisplayName,
    toAvatarEmoji = toAvatarEmoji,
    status = status,
    createdAt = createdAt
)
fun FriendRequest.toEntity() = FriendRequestEntity(
    id = id,
    fromUserId = fromUserId,
    toUserId = toUserId,
    fromDisplayName = fromDisplayName,
    fromAvatarEmoji = fromAvatarEmoji,
    toDisplayName = toDisplayName,
    toAvatarEmoji = toAvatarEmoji,
    status = status,
    createdAt = createdAt
)

// ── GroupInvitation ───────────────────────────────────
fun GroupInvitationEntity.toDomain() = GroupInvitation(
    id = id,
    fromUserId = fromUserId,
    fromDisplayName = fromDisplayName,
    toUserId = toUserId,
    toDisplayName = toDisplayName,
    groupId = groupId,
    groupName = groupName,
    groupIcon = groupIcon,
    status = status,
    createdAt = createdAt
)
fun GroupInvitation.toEntity() = GroupInvitationEntity(
    id = id,
    fromUserId = fromUserId,
    fromDisplayName = fromDisplayName,
    toUserId = toUserId,
    toDisplayName = toDisplayName,
    groupId = groupId,
    groupName = groupName,
    groupIcon = groupIcon,
    status = status,
    createdAt = createdAt
)

// ── ShopItem ──────────────────────────────────────────
fun ShopItemEntity.toDomain() = ShopItem(
    id = id,
    userId = userId,
    title = title,
    emoji = emoji,
    price = price,
    category = category,
    isPurchased = isPurchased,
    isEquipped = isEquipped
)

fun ShopItem.toEntity() = ShopItemEntity(
    id = id,
    userId = userId,
    title = title,
    emoji = emoji,
    price = price,
    category = category,
    isPurchased = isPurchased,
    isEquipped = isEquipped
)
