package com.example.oktodo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onSave: (String, LocalDate, LocalTime?, String?, String?, Color) -> Unit,  // Añadido Color
    initialDate: LocalDate = LocalDate.now()
) {
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(initialDate) }
    var time by remember { mutableStateOf<LocalTime?>(null) }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedColor by remember { mutableStateOf(EventColors[0]) }  // Color por defecto
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // Header
                Text(
                    text = "Nuevo Evento",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Título
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Título del evento") },
                    placeholder = { Text("Ej: Reunión de diseño") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Title,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Fecha
                OutlinedTextField(
                    value = date.format(dateFormatter),
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Fecha") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Cambiar fecha",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    enabled = false,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Hora
                OutlinedTextField(
                    value = time?.format(timeFormatter) ?: "",
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Hora (opcional)") },
                    placeholder = { Text("Seleccionar hora") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { showTimePicker = true }) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Cambiar hora",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    enabled = false,
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Ubicación
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Ubicación (opcional)") },
                    placeholder = { Text("Ej: Sala 301, Online, etc.") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Descripción
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Descripción (opcional)") },
                    placeholder = { Text("Añade más detalles...") },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        focusedLabelColor = MaterialTheme.colorScheme.primary
                    ),
                    leadingIcon = {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    minLines = 2,
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de color
                ColorPicker(
                    selectedColor = selectedColor,
                    onColorSelected = { selectedColor = it },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botones
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            onSave(title, date, time, location.ifBlank { null }, description.ifBlank { null }, selectedColor)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        enabled = title.isNotBlank()
                    ) {
                        Text("Guardar")
                    }
                }
            }
        }
    }

    // Placeholders para pickers
    if (showDatePicker) {
        LaunchedEffect(showDatePicker) {
            showDatePicker = false
        }
    }

    if (showTimePicker) {
        LaunchedEffect(showTimePicker) {
            showTimePicker = false
        }
    }
}