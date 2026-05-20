package com.example.oktodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.oktodo.ui.viewmodel.FriendsViewModel
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedEventDetailScreen(
    navController: NavController,
    groupId: String,
    eventId: String,
    viewModel: FriendsViewModel
) {
    val socialState by viewModel.socialState.collectAsState()
    val sharedEvents by viewModel.sharedEvents.collectAsState()
    val event = remember(sharedEvents, eventId) {
        sharedEvents.find { it.id == eventId }
    }

    if (event == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Evento no encontrado", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        return
    }

    val displayName = socialState.displayName
    val isCreator = event.creator == displayName || (event.creator == "Tú" && displayName == "Usuario OKTodo")
    val isEditor = isCreator || event.editors.contains(displayName) || event.editors.contains("Tú")

    var isEditing by remember { mutableStateOf(false) }
    var editTitle by remember(event.id) { mutableStateOf(event.title) }
    var editDate by remember(event.id) { mutableStateOf(event.date) }
    var editTime by remember(event.id) { mutableStateOf(event.time) }
    var editLocation by remember(event.id) { mutableStateOf(event.location) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showManageEditors by remember { mutableStateOf(false) }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = try {
                LocalDate.parse(editDate).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            } catch (_: Exception) { System.currentTimeMillis() }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val ld = Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()
                        editDate = ld.toString()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val initialHour = try { LocalTime.parse(editTime).hour } catch (_: Exception) { 12 }
        val initialMinute = try { LocalTime.parse(editTime).minute } catch (_: Exception) { 0 }
        val timePickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute,
            is24Hour = true
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            title = { Text("Seleccionar hora") },
            text = { TimePicker(state = timePickerState) },
            confirmButton = {
                TextButton(onClick = {
                    editTime = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            }
        )
    }

    if (showManageEditors) {
        ManageEditorsDialog(
            currentEditors = event.editors,
            participants = event.participants,
            onSave = { editors ->
                viewModel.setSharedEventEditors(eventId, editors)
                showManageEditors = false
            },
            onDismiss = { showManageEditors = false }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopAppBar(
            title = { Text(if (isEditing) "Editar evento" else "Detalle del evento") },
            navigationIcon = {
                IconButton(onClick = {
                    if (isEditing) {
                        isEditing = false
                        editTitle = event.title
                        editDate = event.date
                        editTime = event.time
                        editLocation = event.location
                    } else {
                        navController.popBackStack()
                    }
                }) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Volver")
                }
            },
            actions = {
                if (!isEditing && isEditor) {
                    TextButton(onClick = { isEditing = true }) { Text("Editar") }
                }
            }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    if (isEditing) {
                        OutlinedTextField(
                            value = editTitle,
                            onValueChange = { editTitle = it },
                            label = { Text("Título") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        Text(
                            text = event.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Person,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Creado por ${event.creator}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isEditing) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.DateRange,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = { showDatePicker = true }) {
                                Text(editDate)
                            }
                        }
                    } else {
                        DetailRow(icon = Icons.Outlined.DateRange, text = event.date)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isEditing) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Outlined.AccessTime,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            TextButton(onClick = { showTimePicker = true }) {
                                Text(editTime)
                            }
                        }
                    } else {
                        DetailRow(icon = Icons.Outlined.AccessTime, text = event.time)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    if (isEditing) {
                        OutlinedTextField(
                            value = editLocation,
                            onValueChange = { editLocation = it },
                            label = { Text("Ubicación") },
                            singleLine = true,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    } else {
                        DetailRow(icon = Icons.Outlined.LocationOn, text = event.location)
                    }

                    if (isEditing) {
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                viewModel.updateSharedEvent(eventId, editTitle, editDate, editTime, editLocation)
                                isEditing = false
                            },
                            enabled = editTitle.isNotBlank(),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Guardar cambios")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Participants ──
            DetailSectionHeader("Participantes (${event.participants.size})")
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                event.participants.forEach { name ->
                    val isCurrentUser = name == displayName || name == "Tú"
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isCurrentUser) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Text(
                            text = if (isCurrentUser) "Tú" else name,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Editors ──
            DetailSectionHeader("Editores (${event.editors.size})")
            if (isCreator && !isEditing) {
                TextButton(onClick = { showManageEditors = true }) {
                    Text("Gestionar editores", color = MaterialTheme.colorScheme.primary)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                event.editors.forEach { name ->
                    val isCurrentUser = name == displayName || name == "Tú"
                    Surface(
                        shape = RoundedCornerShape(50),
                        color = if (isCurrentUser) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.tertiary
                    ) {
                        Text(
                            text = if (isCurrentUser) "Tú" else name,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                            color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.onTertiary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun DetailSectionHeader(title: String) {
    Text(
        text = title,
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun ManageEditorsDialog(
    currentEditors: List<String>,
    participants: List<String>,
    onSave: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    var selected by remember { mutableStateOf(currentEditors.toSet()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Gestionar editores") },
        text = {
            Column {
                participants.forEach { name ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selected = if (name in selected) selected - name else selected + name
                            }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = name in selected,
                            onCheckedChange = {
                                selected = if (name in selected) selected - name else selected + name
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(name)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(selected.toList()) }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
