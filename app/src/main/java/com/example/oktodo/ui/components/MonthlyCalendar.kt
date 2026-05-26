package com.example.oktodo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.oktodo.ui.model.CalendarEvent
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.foundation.shape.RoundedCornerShape

@Composable
fun MonthlyCalendar(
    selectedDate: LocalDate,
    events: List<CalendarEvent>,
    onDateSelected: (LocalDate) -> Unit,
    onEventClick: (CalendarEvent) -> Unit
) {
    val initialMonth = remember(selectedDate) { YearMonth.from(selectedDate) }
    val startMonth = remember(initialMonth) { initialMonth.minusMonths(12) }
    val endMonth = remember(initialMonth) { initialMonth.plusMonths(12) }
    val scope = rememberCoroutineScope()

    val daysOfWeek = remember {
        listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY,
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY
        )
    }

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = initialMonth,
        firstDayOfWeek = daysOfWeek.first()
    )

    val currentVisibleMonth by remember {
        derivedStateOf { calendarState.firstVisibleMonth.yearMonth }
    }

    val selectedDayEvents = remember(events, selectedDate) {
        events.filter { it.date == selectedDate }
    }

    val eventsByDate = remember(events) {
        events.groupBy { it.date }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            scope.launch {
                                calendarState.animateScrollToMonth(currentVisibleMonth.minusMonths(1))
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Mes anterior")
                    }

                    Text(
                        text = currentVisibleMonth
                            .format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale("es")))
                            .replaceFirstChar {
                                if (it.isLowerCase()) it.titlecase(Locale("es")) else it.toString()
                            },
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = {
                            scope.launch {
                                calendarState.animateScrollToMonth(currentVisibleMonth.plusMonths(1))
                            }
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Mes siguiente")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                DaysOfWeekTitle(daysOfWeek)

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalCalendar(
                    modifier = Modifier.fillMaxWidth(),
                    state = calendarState,
                    dayContent = { day ->
                        MonthDay(
                            day = day,
                            isSelected = day.date == selectedDate,
                            events = eventsByDate[day.date].orEmpty(),
                            onClick = { onDateSelected(day.date) },
                            currentMonth = currentVisibleMonth
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Eventos del ${selectedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (selectedDayEvents.isEmpty()) {
                    Text(
                        text = "No hay eventos en este día",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    selectedDayEvents.forEach { event ->
                        EventPreviewRow(
                            event = event,
                            onClick = { onEventClick(event) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp)
    ) {
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                modifier = Modifier.weight(1f),
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es")).replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("es")) else it.toString() },
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MonthDay(
    day: CalendarDay,
    isSelected: Boolean,
    events: List<CalendarEvent>,
    onClick: () -> Unit,
    currentMonth: YearMonth
) {
    val isFromCurrentMonth = remember(day, currentMonth) {
        YearMonth.from(day.date) == currentMonth
    }

    val isToday = day.date == LocalDate.now()

    val backgroundColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isToday -> MaterialTheme.colorScheme.primaryContainer
        !isFromCurrentMonth -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.18f)
        else -> Color.Transparent
    }

    val contentColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> MaterialTheme.colorScheme.onPrimaryContainer
        !isFromCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.35f)
        else -> MaterialTheme.colorScheme.onSurface
    }

    Column(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(3.dp)
            .clip(MaterialTheme.shapes.medium)
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
            color = contentColor
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (events.isNotEmpty()) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                events.take(3).forEach { event ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 1.dp)
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.onPrimary else event.color
                            )
                    )
                }

                if (events.size > 3) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Text(
                        text = "+${events.size - 3}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimary
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(6.dp))
        }
    }
}

@Composable
private fun EventPreviewRow(
    event: CalendarEvent,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 6.dp, height = 36.dp)
                .clip(RoundedCornerShape(50))
                .background(event.color)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = event.title,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            val secondary = buildString {
                event.time?.let { append(it.format(DateTimeFormatter.ofPattern("HH:mm"))) }
                if (!event.location.isNullOrBlank()) {
                    if (isNotEmpty()) append(" • ")
                    append(event.location)
                }
            }

            if (secondary.isNotBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = secondary,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}