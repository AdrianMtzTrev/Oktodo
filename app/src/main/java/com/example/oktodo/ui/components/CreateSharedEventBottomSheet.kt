package com.example.oktodo.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.oktodo.ui.model.Group

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSharedEventBottomSheet(
    groups: List<Group>,
    onDismiss: () -> Unit,
    onCreateEvent: (title: String, groupId: String, date: String, time: String, location: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    var selectedGroupName by remember { mutableStateOf("") }
    var selectedGroupId by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.White
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Nuevo Evento Grupal", style = MaterialTheme.typography.titleLarge)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Outlined.Close, contentDescription = "Cerrar")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Título del evento") },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedGroupName,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                        .fillMaxWidth(),
                    placeholder = { Text("Selecciona un grupo") },
                    shape = RoundedCornerShape(14.dp),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    groups.forEach { group ->
                        DropdownMenuItem(
                            text = { Text(group.name) },
                            onClick = {
                                selectedGroupName = group.name
                                selectedGroupId = group.id
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("2026-03-16") },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )

                OutlinedTextField(
                    value = time,
                    onValueChange = { time = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("19:00") },
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Ubicación (opcional)") },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    if (
                        title.isNotBlank() &&
                        selectedGroupId.isNotBlank() &&
                        date.isNotBlank() &&
                        time.isNotBlank()
                    ) {
                        onCreateEvent(title, selectedGroupId, date, time, location)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF7C3AED)
                )
            ) {
                Text("Crear evento")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}