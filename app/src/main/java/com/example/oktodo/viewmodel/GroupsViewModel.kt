package com.example.oktodo.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oktodo.ui.model.Group
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GroupsViewModel : ViewModel() {

    private val _groups = MutableStateFlow<List<Group>>(emptyList())
    val groups: StateFlow<List<Group>> = _groups.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun createGroup(name: String, members: List<String>) {
        viewModelScope.launch {
            _isLoading.value = true

            val newGroup = Group(
                id = "g${_groups.value.size + 1}",
                name = name,
                icon = "👥",
                members = members,
                eventCount = 0
            )

            _groups.value = _groups.value + newGroup
            _isLoading.value = false
        }
    }

    fun getGroupsForUser(userId: String = "current_user"): List<Group> {
        return _groups.value.filter { group ->
            group.members.contains(userId)
        }
    }
}