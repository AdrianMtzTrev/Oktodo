package com.example.oktodo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.oktodo.ui.model.Friend

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupBottomSheet(
    friends: List<Friend>,
    onDismiss: () -> Unit,
    onCreateGroup: (String, List<String>) -> Unit
) {
    var groupName by remember { mutableStateOf("") }
    val selected = remember { mutableStateListOf<String>() }

    val surface = MaterialTheme.colorScheme.surface
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val primary = MaterialTheme.colorScheme.primary
    val selectedContainer = MaterialTheme.colorScheme.primaryContainer
    val unselectedContainer = MaterialTheme.colorScheme.surfaceVariant

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = surface
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
                Text(
                    text = "Nuevo Grupo",
                    style = MaterialTheme.typography.titleLarge,
                    color = onSurface
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Cerrar",
                        tint = onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = groupName,
                onValueChange = { groupName = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Nombre del grupo", color = onSurfaceVariant)
                },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Selecciona miembros",
                fontWeight = FontWeight.SemiBold,
                color = onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.heightIn(max = 260.dp)
            ) {
                items(friends) { friend ->
                    val isSelected = selected.contains(friend.name)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .clickable {
                                if (isSelected) {
                                    selected.remove(friend.name)
                                } else {
                                    selected.add(friend.name)
                                }
                            }
                            .background(
                                if (isSelected) selectedContainer else unselectedContainer
                            )
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(friend.avatarColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(friend.avatar)
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Text(
                            text = friend.name,
                            modifier = Modifier.weight(1f),
                            color = onSurface
                        )

                        Checkbox(
                            checked = isSelected,
                            onCheckedChange = {
                                if (it) {
                                    if (!selected.contains(friend.name)) selected.add(friend.name)
                                } else {
                                    selected.remove(friend.name)
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    if (groupName.isNotBlank() && selected.isNotEmpty()) {
                        onCreateGroup(groupName, selected.toList())
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = primary
                )
            ) {
                Text("Crear grupo")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}