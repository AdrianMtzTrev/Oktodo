package com.example.oktodo.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.oktodo.ui.model.Friend
import com.example.oktodo.ui.model.Group
import com.example.oktodo.ui.model.SharedEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FriendsViewModel : ViewModel() {

    private val _friends = MutableStateFlow(
        listOf(
            Friend("1", "María García", 280, 3, "👩", Color(0xFFC4B5FD)),
            Friend("2", "Carlos López", 195, 2, "👱", Color(0xFFDDD6FE)),
            Friend("3", "Ana Martínez", 340, 5, "👩‍🦰", Color(0xFFE9D5FF)),
            Friend("4", "Luis Rodríguez", 150, 1, "👦", Color(0xFFD8B4FE))
        )
    )
    val friends: StateFlow<List<Friend>> = _friends.asStateFlow()

    private val _groups = MutableStateFlow(
        listOf(
            Group(
                id = "g1",
                name = "Equipo Estudio",
                icon = "📚",
                members = listOf("María García", "Ana Martínez", "Tú"),
                eventCount = 2
            ),
            Group(
                id = "g2",
                name = "Deportistas",
                icon = "⚽",
                members = listOf("Carlos López", "Luis Rodríguez", "Tú"),
                eventCount = 1
            )
        )
    )
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    private val _sharedEvents = MutableStateFlow(
        listOf(
            SharedEvent(
                id = "e1",
                title = "Cine con amigos",
                creator = "María García",
                groupId = "g1",
                date = "2026-03-16",
                time = "19:00",
                location = "Cinépolis Centro",
                participants = listOf("María García", "Carlos López", "Tú")
            ),
            SharedEvent(
                id = "e2",
                title = "Estudio grupal",
                creator = "Ana Martínez",
                groupId = "g1",
                date = "2026-03-18",
                time = "15:00",
                location = "Biblioteca Universidad",
                participants = listOf("Ana Martínez", "Luis Rodríguez", "Tú")
            ),
            SharedEvent(
                id = "e3",
                title = "Entrenamiento",
                creator = "Carlos López",
                groupId = "g2",
                date = "2026-03-20",
                time = "07:00",
                location = "Unidad Deportiva",
                participants = listOf("Carlos López", "Luis Rodríguez", "Tú")
            )
        )
    )
    val sharedEvents: StateFlow<List<SharedEvent>> = _sharedEvents.asStateFlow()

    fun createGroup(name: String, members: List<String>) {
        val newGroup = Group(
            id = "g${_groups.value.size + 1}",
            name = name,
            icon = "👥",
            members = members + "Tú",
            eventCount = 0
        )
        _groups.value = _groups.value + newGroup
    }

    fun createSharedEvent(
        title: String,
        groupId: String,
        date: String,
        time: String,
        location: String
    ) {
        val selectedGroup = _groups.value.find { it.id == groupId } ?: return

        val newEvent = SharedEvent(
            id = "e${_sharedEvents.value.size + 1}",
            title = title,
            creator = "Tú",
            groupId = groupId,
            date = date,
            time = time,
            location = location,
            participants = selectedGroup.members
        )

        _sharedEvents.value = _sharedEvents.value + newEvent

        _groups.value = _groups.value.map { group ->
            if (group.id == groupId) {
                group.copy(eventCount = group.eventCount + 1)
            } else {
                group
            }
        }
    }

    fun getGroupById(groupId: String): Group? {
        return _groups.value.find { it.id == groupId }
    }

    fun getEventsForGroup(groupId: String): List<SharedEvent> {
        return _sharedEvents.value.filter { it.groupId == groupId }
    }

    fun getEventsForGroupOnDate(groupId: String, date: LocalDate): List<SharedEvent> {
        return _sharedEvents.value.filter {
            it.groupId == groupId && it.date == date.toString()
        }
    }

    fun getFormattedDateLabel(rawDate: String): String {
        return try {
            val date = LocalDate.parse(rawDate)
            val formatter = DateTimeFormatter.ofPattern("EEE, d MMM")
            date.format(formatter)
        } catch (_: Exception) {
            rawDate
        }
    }
}