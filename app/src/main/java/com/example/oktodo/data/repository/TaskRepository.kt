package com.example.oktodo.data.repository

import com.example.oktodo.data.local.dao.TaskDao
import com.example.oktodo.data.local.mapper.toDomain
import com.example.oktodo.data.local.mapper.toEntity
import com.example.oktodo.ui.model.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskRepository @Inject constructor(private val dao: TaskDao) {
    val tasks: Flow<List<Task>> = dao.getAllTasks().map { list -> list.map { it.toDomain() } }

    suspend fun add(task: Task) = dao.insert(task.toEntity())
    suspend fun update(task: Task) = dao.update(task.toEntity())
    suspend fun delete(task: Task) = dao.delete(task.toEntity())
    suspend fun getTaskById(id: String): Task? = dao.getTaskById(id)?.toDomain()
}
