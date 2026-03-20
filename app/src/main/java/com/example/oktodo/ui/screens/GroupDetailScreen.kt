package com.example.oktodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.oktodo.ui.components.SharedEventCard
import com.example.oktodo.ui.viewmodel.FriendsViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.*
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun GroupDetailScreen(
    navController: NavController,
    groupId: String,
    viewModel: FriendsViewModel
) {
    val group = viewModel.getGroupById(groupId) ?: return

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(12) }
    val endMonth = remember { currentMonth.plusMonths(12) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }

    val firstDayOfWeek = remember { DayOfWeek.SUNDAY }

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    val visibleMonth = calendarState.firstVisibleMonth.yearMonth
    val dayEvents = remember(selectedDate) {
        viewModel.getEventsForGroupOnDate(groupId, selectedDate)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F5FF))
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver")
            }

            Text(text = group.icon)
            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = group.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${group.members.size} miembros",
                    color = Color(0xFF6B7280)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Miembros",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            group.members.forEach { member ->
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (member == "Tú") Color(0xFF7C3AED) else Color(0xFFF3F4F6)
                ) {
                    Text(
                        text = member,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        color = if (member == "Tú") Color.White else Color(0xFF374151)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Calendario",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${visibleMonth.month.getDisplayName(TextStyle.FULL, Locale("es"))} ${visibleMonth.year}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                DaysOfWeekTitle(firstDayOfWeek = firstDayOfWeek)

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalCalendar(
                    state = calendarState,
                    dayContent = { day ->
                        val isSelected = day.date == selectedDate
                        val isCurrentMonth = day.position == DayPosition.MonthDate
                        val hasEvents = viewModel.getEventsForGroupOnDate(groupId, day.date).isNotEmpty()

                        Box(
                            modifier = Modifier
                                .aspectRatio(1f)
                                .padding(4.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isSelected) Color(0xFF7C3AED) else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = 8.dp)
                            ) {
                                TextButton(
                                    onClick = {
                                        if (isCurrentMonth) {
                                            selectedDate = day.date
                                        }
                                    },
                                    contentPadding = PaddingValues(0.dp)
                                ) {
                                    Text(
                                        text = day.date.dayOfMonth.toString(),
                                        color = when {
                                            !isCurrentMonth -> Color.LightGray
                                            isSelected -> Color.White
                                            else -> Color(0xFF111827)
                                        }
                                    )
                                }

                                if (hasEvents && isCurrentMonth) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isSelected) Color.White else Color(0xFF7C3AED)
                                            )
                                    )
                                }
                            }
                        }
                    },
                    monthHeader = {}
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Eventos - ${viewModel.getFormattedDateLabel(selectedDate.toString())}",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (dayEvents.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(28.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No hay eventos este día",
                        color = Color(0xFF9CA3AF)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(dayEvents) { event ->
                    SharedEventCard(event = event)
                }
            }
        }
    }
}

@Composable
private fun DaysOfWeekTitle(firstDayOfWeek: DayOfWeek) {
    val daysOfWeek = daysOfWeek(firstDayOfWeek)

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        daysOfWeek.forEach { dayOfWeek ->
            Text(
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es")),
                modifier = Modifier.weight(1f),
                color = Color(0xFF6B7280)
            )
        }
    }
}