package com.example.oktodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.oktodo.ui.components.InviteToGroupBottomSheet
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    navController: NavController,
    groupId: String,
    viewModel: FriendsViewModel
) {
    val groups by viewModel.groups.collectAsState()
    val group = groups.find { it.id == groupId } ?: return
    val friends by viewModel.friends.collectAsState()
    val socialState by viewModel.socialState.collectAsState()

    var showInviteSheet by remember { mutableStateOf(false) }
    var showSettings by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var groupNameInput by remember(group.id) { mutableStateOf(group.name) }

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
    val dayEvents = remember(selectedDate, viewModel.sharedEvents) {
        viewModel.getEventsForGroupOnDate(groupId, selectedDate)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(0.dp),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver")
                }

                Text(text = group.icon)
                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = group.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${group.members.size} miembros",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = { groupNameInput = group.name; showSettings = true }) {
                    Icon(Icons.Outlined.Settings, contentDescription = "Configurar grupo")
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Miembros",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { showInviteSheet = true }) {
                    Text("+ Invitar amigo")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                group.members.forEach { member ->
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (member == socialState.displayName || member == "Tú") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (member == socialState.displayName || member == "Tú") "Tú" else member,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            color = if (member == socialState.displayName || member == "Tú") MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
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
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "${visibleMonth.month.getDisplayName(TextStyle.FULL, Locale("es"))} ${visibleMonth.year}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
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
                                        if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
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
                                                !isCurrentMonth -> MaterialTheme.colorScheme.onSurfaceVariant
                                                isSelected -> MaterialTheme.colorScheme.onPrimary
                                                else -> MaterialTheme.colorScheme.onSurface
                                            }
                                        )
                                    }

                                    if (hasEvents && isCurrentMonth) {
                                        Box(
                                            modifier = Modifier
                                                .size(6.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.primary
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
        }

        if (dayEvents.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(28.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay eventos este día",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(dayEvents) { event ->
                SharedEventCard(
                    event = event,
                    onClick = { navController.navigate("shared_event_detail/$groupId/${event.id}") }
                )
            }
        }
    }

    if (showInviteSheet) {
        InviteToGroupBottomSheet(
            friends = friends,
            groupMembers = group.members,
            onInvite = { friend ->
                viewModel.sendGroupInvitation(friend, group)
                showInviteSheet = false
            },
            onDismiss = { showInviteSheet = false }
        )
    }

    if (showSettings) {
        ModalBottomSheet(
            onDismissRequest = { showSettings = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Configurar grupo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(20.dp))

                OutlinedTextField(
                    value = groupNameInput,
                    onValueChange = { groupNameInput = it },
                    label = { Text("Nombre del grupo") },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        viewModel.renameGroup(group.id, groupNameInput)
                        showSettings = false
                    },
                    enabled = groupNameInput.isNotBlank() && groupNameInput.trim() != group.name,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Text("Guardar cambios")
                }

                Spacer(modifier = Modifier.height(24.dp))

                HorizontalDivider()

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(
                    onClick = { showDeleteConfirm = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Eliminar grupo",
                        color = MaterialTheme.colorScheme.error,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("¿Eliminar grupo?") },
            text = { Text("Se eliminará \"${group.name}\" permanentemente. Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteGroup(group.id)
                        showDeleteConfirm = false
                        showSettings = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            }
        )
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
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}