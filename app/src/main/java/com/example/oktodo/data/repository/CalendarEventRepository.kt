package com.example.oktodo.data.repository

import com.example.oktodo.data.local.dao.CalendarEventDao
import com.example.oktodo.data.local.mapper.toDomain
import com.example.oktodo.data.local.mapper.toEntity
import com.example.oktodo.ui.model.CalendarEvent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CalendarEventRepository @Inject constructor(private val dao: CalendarEventDao) {
    val events: Flow<List<CalendarEvent>> = dao.getAllEvents().map { list -> list.map { it.toDomain() } }

    suspend fun add(event: CalendarEvent) = dao.insert(event.toEntity())
    suspend fun update(event: CalendarEvent) = dao.update(event.toEntity())
    suspend fun deleteById(id: String) = dao.deleteById(id)
}
