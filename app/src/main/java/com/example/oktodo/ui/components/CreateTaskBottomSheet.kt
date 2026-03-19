@file:Suppress("SpellCheckingInspection")

package com.example.oktodo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.AccessTime
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTaskBottomSheet(
    onDismiss: () -> Unit,
    onTaskCreated: (String, String) -> Unit
) {
    var taskTitle by remember { mutableStateOf("") }
    var taskTime by remember { mutableStateOf("") }
    var showTimePicker by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    // Auto-focus en el campo de título cuando se abre
    LaunchedEffect(Unit) {
        delay(100)
        focusRequester.requestFocus()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 8.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .background(
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        RoundedCornerShape(2.dp)
                    )
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Crear nueva tarea",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Campo de título
            OutlinedTextField(
                value = taskTitle,
                onValueChange = { taskTitle = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                label = { Text("Nombre de la tarea") },
                placeholder = { Text("Ej: Revisar diseño de UI") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de hora
            OutlinedTextField(
                value = taskTime,
                onValueChange = { taskTime = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Hora límite") },
                placeholder = { Text("Ej: 10:00 AM") },
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    cursorColor = MaterialTheme.colorScheme.primary
                ),
                trailingIcon = {
                    IconButton(onClick = { showTimePicker = true }) {
                        // En el código:
                        Icon(
                            Icons.Outlined.AccessTime,  // Cambiado de AccessTime a Icons.Outlined.AccessTime
                            contentDescription = "Seleccionar hora",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botón de crear
            Button(
                onClick = {
                    keyboardController?.hide()
                    if (taskTitle.isNotBlank() && taskTime.isNotBlank()) {
                        onTaskCreated(taskTitle, taskTime)
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                ),
                enabled = taskTitle.isNotBlank() && taskTime.isNotBlank()
            ) {
                Text(
                    text = "Crear tarea",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Placeholder para el selector de hora
    if (showTimePicker) {
        // Aquí puedes implementar un TimePicker real después
        LaunchedEffect(showTimePicker) {
            showTimePicker = false
        }
    }
}
