package com.example.oktodo.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.oktodo.data.repository.CalendarEventRepository
import com.example.oktodo.ui.model.CalendarEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val repository: CalendarEventRepository
) : ViewModel() {

    val events: StateFlow<List<CalendarEvent>> = repository.events
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _selectedMonth = MutableStateFlow(YearMonth.now())
    val selectedMonth: StateFlow<YearMonth> = _selectedMonth.asStateFlow()

    fun addEvent(event: CalendarEvent) {
        viewModelScope.launch { repository.add(event) }
    }

    fun deleteEvent(eventId: String) {
        viewModelScope.launch { repository.deleteById(eventId) }
    }

    fun selectDate(date: LocalDate) {
        _selectedDate.value = date
        _selectedMonth.value = YearMonth.from(date)
    }

    fun selectMonth(month: YearMonth) { _selectedMonth.value = month }

    fun goToToday() {
        val today = LocalDate.now()
        _selectedDate.value = today
        _selectedMonth.value = YearMonth.from(today)
    }

    fun getEventsForDate(date: LocalDate): List<CalendarEvent> =
        events.value.filter { it.date == date }
}
