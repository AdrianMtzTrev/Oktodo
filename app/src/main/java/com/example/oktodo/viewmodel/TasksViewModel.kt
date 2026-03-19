package com.example.oktodo.ui.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.example.oktodo.ui.model.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TasksViewModel : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _showBottomSheet = MutableStateFlow(false)
    val showBottomSheet: StateFlow<Boolean> = _showBottomSheet.asStateFlow()

    private val _points = MutableStateFlow(0)
    val points: StateFlow<Int> = _points.asStateFlow()

    fun addTask(title: String, time: String) {
        if (title.isNotBlank() && time.isNotBlank()) {
            val newTask = Task(
                title = title,
                time = time,
                priority = "Media",
                color = getPriorityColor("Media"),
                isCompleted = false,
                pointsReward = 10
            )
            _tasks.value = _tasks.value + newTask
            hideBottomSheet()
        }
    }

    fun toggleTaskCompletion(task: Task) {
        val updatedTasks = _tasks.value.map { currentTask ->
            if (currentTask.id == task.id) {
                val newCompletedState = !currentTask.isCompleted

                if (newCompletedState && !currentTask.isCompleted) {
                    _points.value += currentTask.pointsReward
                } else if (!newCompletedState && currentTask.isCompleted) {
                    _points.value -= currentTask.pointsReward
                }

                currentTask.copy(isCompleted = newCompletedState)
            } else {
                currentTask
            }
        }

        _tasks.value = updatedTasks
    }

    fun deleteTask(task: Task) {
        val taskToDelete = _tasks.value.find { it.id == task.id }
        if (taskToDelete != null && taskToDelete.isCompleted) {
            _points.value -= taskToDelete.pointsReward
        }

        _tasks.value = _tasks.value.filter { it.id != task.id }
    }

    fun showBottomSheet() {
        _showBottomSheet.value = true
    }

    fun hideBottomSheet() {
        _showBottomSheet.value = false
    }

    private fun getPriorityColor(priority: String): Color {
        return when (priority) {
            "Alta" -> Color(0xFFFF6B6B)
            "Media" -> Color(0xFF7C3AED)
            else -> Color(0xFF45B7D1)
        }
    }
}