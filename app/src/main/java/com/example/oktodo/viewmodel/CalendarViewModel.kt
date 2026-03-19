package com.example.oktodo.ui.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.example.oktodo.ui.model.CalendarEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

class CalendarViewModel : ViewModel() {
    private val _events = MutableStateFlow<List<CalendarEvent>>(emptyList())
    val events: StateFlow<List<CalendarEvent>> = _events.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    fun addEvent(event: CalendarEvent) {
        _events.value = _events.value + event
    }

    fun deleteEvent(eventId: String) {
        _events.value = _events.value.filter { it.id != eventId }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
    }

    fun selectMonth(month: YearMonth) {
        _selectedMonth.value = month
    }

    fun getEventsForDate(date: LocalDate): List<CalendarEvent> {
        return _events.value.filter { it.date == date }
    }
}