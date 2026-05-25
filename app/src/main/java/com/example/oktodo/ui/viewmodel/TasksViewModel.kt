package com.example.oktodo.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oktodo.data.local.UserPreferencesDataStore
import com.example.oktodo.data.repository.TaskRepository
import com.example.oktodo.ui.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val prefs: UserPreferencesDataStore
) : ViewModel() {

    val tasks: StateFlow<List<Task>> = repository.tasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val taskMutex = Mutex()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

    val points: StateFlow<Int> = prefs.preferences
        .map { it.points }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val displayName: StateFlow<String> = prefs.preferences
        .map { it.displayName }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Usuario")

    init {
        viewModelScope.launch { prefs.checkWeeklyReset() }
    }

    fun addTask(title: String, time: String, priority: String = "Media") {
        if (title.isBlank() || time.isBlank()) return
        val color = when (priority) {
            "Alta" -> Color(0xFFEF4444)
            "Baja" -> Color(0xFF3B82F6)
            else  -> Color(0xFF7C3AED)
        }
        viewModelScope.launch {
            repository.add(
                Task(
                    title = title,
                    time = time,
                    priority = priority,
                    color = color,
                    isCompleted = false,
                    pointsReward = 10
                )
            )
            hideBottomSheet()
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            taskMutex.withLock {
                val current = repository.getTaskById(task.id)
                if (current == null) return@withLock
                val newState = !current.isCompleted
                repository.update(current.copy(isCompleted = newState))
                if (newState) {
                    prefs.completeTask(current.pointsReward)
                    prefs.updateStreak()
                } else {
                    prefs.uncompleteTask(current.pointsReward)
                }
            }
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskMutex.withLock {
                val current = repository.getTaskById(task.id) ?: return@withLock
                if (current.isCompleted) prefs.uncompleteTask(current.pointsReward)
                repository.delete(current)
            }
        }
    }

    fun showBottomSheet() { _showBottomSheet.value = true }
    fun hideBottomSheet() { _showBottomSheet.value = false }
}
