@file:Suppress("SpellCheckingInspection")

package com.example.oktodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.oktodo.ui.components.AddEventBottomSheet
import com.example.oktodo.ui.components.DayCalendar
import com.example.oktodo.ui.components.MonthlyCalendar
import com.example.oktodo.ui.components.WeekCalendar
import com.example.oktodo.ui.components.YearCalendarView
import com.example.oktodo.ui.model.CalendarEvent
import com.example.oktodo.ui.viewmodel.CalendarViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

enum class CalendarView {
    YEAR,
    MONTH,
    WEEK,
    DAY
}

@Composable
fun CalendarScreen(
    calendarViewModel: CalendarViewModel = viewModel()
) {
    var showBottomSheet by remember { mutableStateOf(false) }
    var selectedView by remember { mutableStateOf(CalendarView.MONTH) }
    var selectedEvent by remember { mutableStateOf<CalendarEvent?>(null) }

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val events by calendarViewModel.events.collectAsState()
    val selectedDate by calendarViewModel.selectedDate.collectAsState()
    val selectedMonth by calendarViewModel.selectedMonth.collectAsState()

    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary.copy(alpha = 0.14f),
            MaterialTheme.colorScheme.background
        )
    )

    val headerBrush = Brush.horizontalGradient(
        colors = listOf(
            MaterialTheme.colorScheme.primary,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
        )
    )

    val titleText = when (selectedView) {
        CalendarView.YEAR -> "Año ${selectedDate.year}"
        CalendarView.MONTH -> selectedMonth
            .format(DateTimeFormatter.ofPattern("MMMM yyyy"))
            .replaceFirstChar { it.uppercase() }
        CalendarView.WEEK -> "Semana"
        CalendarView.DAY -> selectedDate
            .format(DateTimeFormatter.ofPattern("EEEE, d MMMM"))
            .replaceFirstChar { it.uppercase() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(280.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(headerBrush)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Vista del calendario",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Divider()

                NavigationDrawerItem(
                    label = { Text("Año") },
                    selected = selectedView == CalendarView.YEAR,
                    onClick = {
                        selectedView = CalendarView.YEAR
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.ViewModule, contentDescription = null) }
                )

                NavigationDrawerItem(
                    label = { Text("Mes") },
                    selected = selectedView == CalendarView.MONTH,
                    onClick = {
                        selectedView = CalendarView.MONTH
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) }
                )

                NavigationDrawerItem(
                    label = { Text("Semana") },
                    selected = selectedView == CalendarView.WEEK,
                    onClick = {
                        selectedView = CalendarView.WEEK
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.ViewWeek, contentDescription = null) }
                )

                NavigationDrawerItem(
                    label = { Text("Día") },
                    selected = selectedView == CalendarView.DAY,
                    onClick = {
                        selectedView = CalendarView.DAY
                        scope.launch { drawerState.close() }
                    },
                    icon = { Icon(Icons.Default.ViewDay, contentDescription = null) }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CalendarHeader(
                    title = titleText,
                    brush = headerBrush,
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    },
                    onTodayClick = {
                        calendarViewModel.selectDate(LocalDate.now())
                        calendarViewModel.selectMonth(YearMonth.now())
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showBottomSheet = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Agregar evento",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundBrush)
                    .padding(padding)
            ) {
                when (selectedView) {
                    CalendarView.YEAR -> {
                        YearCalendarView(
                            year = selectedDate.year,
                            events = events,
                            onMonthSelected = { yearMonth ->
                                calendarViewModel.selectMonth(yearMonth)
                                calendarViewModel.selectDate(yearMonth.atDay(1))
                                selectedView = CalendarView.MONTH
                            }
                        )
                    }

                    CalendarView.MONTH -> {
                        MonthlyCalendar(
                            selectedDate = selectedDate,
                            events = events,
                            onDateSelected = { date ->
                                calendarViewModel.selectDate(date)
                                calendarViewModel.selectMonth(YearMonth.from(date))
                            },
                            onEventClick = { event ->
                                selectedEvent = event
                            }
                        )
                    }

                    CalendarView.WEEK -> {
                        WeekCalendar(
                            selectedDate = selectedDate,
                            events = events,
                            onDateSelected = { date ->
                                calendarViewModel.selectDate(date)
                            },
                            onEventClick = { event ->
                                selectedEvent = event
                            }
                        )
                    }

                    CalendarView.DAY -> {
                        DayCalendar(
                            date = selectedDate,
                            events = events.filter { it.date == selectedDate },
                            onEventClick = { event ->
                                selectedEvent = event
                            },
                            onAddEvent = {
                                showBottomSheet = true
                            }
                        )
                    }
                }
            }
        }
    }

    if (showBottomSheet) {
        AddEventBottomSheet(
            onDismiss = { showBottomSheet = false },
            onSave = { title, date, time, location, description, color ->
                val newEvent = CalendarEvent(
                    title = title,
                    date = date,
                    time = time,
                    location = location,
                    description = description,
                    color = color
                )
                calendarViewModel.addEvent(newEvent)
                showBottomSheet = false
            },
            initialDate = selectedDate
        )
    }

    selectedEvent?.let { event ->
        AlertDialog(
            onDismissRequest = { selectedEvent = null },
            title = { Text(event.title) },
            text = {
                Column {
                    Text("Fecha: ${event.date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}")
                    event.time?.let {
                        Text("Hora: ${it.format(DateTimeFormatter.ofPattern("HH:mm"))}")
                    }
                    event.location?.let {
                        Text("Ubicación: $it")
                    }
                    event.description?.let {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(it)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { selectedEvent = null }) {
                    Text("Cerrar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        calendarViewModel.deleteEvent(event.id)
                        selectedEvent = null
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            }
        )
    }
}

@Composable
fun CalendarHeader(
    title: String,
    brush: Brush,
    onMenuClick: () -> Unit,
    onTodayClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(brush)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onMenuClick) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }

                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )

                IconButton(onClick = onTodayClick) {
                    Icon(
                        imageVector = Icons.Default.Today,
                        contentDescription = "Hoy",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}