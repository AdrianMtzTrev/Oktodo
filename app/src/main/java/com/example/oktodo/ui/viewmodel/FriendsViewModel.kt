package com.example.oktodo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oktodo.data.repository.FriendsRepository
import com.example.oktodo.ui.model.Group
import com.example.oktodo.ui.model.SharedEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val repository: FriendsRepository
) : ViewModel() {

    val friends = repository.friends
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val groups = repository.groups
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sharedEvents = repository.sharedEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch { repository.seedIfEmpty() }
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
