@file:Suppress("SpellCheckingInspection")

package com.example.oktodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat  // Versión AutoMirrored
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.oktodo.ui.components.DurationPickerDialog
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun FocusScreen() {
    var selectedDuration by remember { mutableStateOf(25) }
    var sessionDurationSeconds by remember { mutableStateOf(25 * 60) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var timeLeft by remember { mutableStateOf(25 * 60) }
    var progress by remember { mutableStateOf(0f) }
    var showDurationPicker by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val blockedApps = listOf(
        "Instagram" to Icons.Outlined.PhotoCamera,
        "TikTok" to Icons.Outlined.MusicNote,
        "Twitter" to Icons.Outlined.Tag,
        "YouTube" to Icons.Outlined.PlayArrow,
        "Facebook" to Icons.Outlined.Facebook,
        "WhatsApp" to Icons.AutoMirrored.Outlined.Chat  // ← Usando la versión AutoMirrored
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header con título
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
                    .padding(top = 24.dp, bottom = 16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Modo Enfoque",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Text(
                        text = "Mantente concentrado en tus objetivos",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Timer Circular
        item {
            Card(
                modifier = Modifier
                    .padding(24.dp)
                    .size(280.dp)
                    .shadow(20.dp, CircleShape),
                shape = CircleShape,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    // Círculo de progreso
                    CircularProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        color = MaterialTheme.colorScheme.primary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 8.dp
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            if (!isTimerRunning) showDurationPicker = true
                        }
                    ) {
                        val displayHours = timeLeft / 3600
                        val displayMinutes = (timeLeft % 3600) / 60
                        val displaySeconds = timeLeft % 60
                        Text(
                            text = if (displayHours > 0) {
                                "${displayHours}:${String.format("%02d", displayMinutes)}:${String.format("%02d", displaySeconds)}"
                            } else {
                                "${displayMinutes}:${String.format("%02d", displaySeconds)}"
                            },
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Controles del timer
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón de play/pausa
                FloatingActionButton(
                    onClick = {
                        if (isTimerRunning) {
                            isTimerRunning = false
                        } else {
                            isTimerRunning = true
                            coroutineScope.launch {
                                while (isTimerRunning && timeLeft > 0) {
                                    delay(1000)
                                    timeLeft--
                                    progress = 1f - (timeLeft.toFloat() / sessionDurationSeconds)
                                }
                                if (timeLeft == 0) {
                                    isTimerRunning = false
                                }
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    shape = CircleShape,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        imageVector = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isTimerRunning) "Pausar" else "Iniciar",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Botón de reinicio
                FloatingActionButton(
                    onClick = {
                        isTimerRunning = false
                        timeLeft = sessionDurationSeconds
                        progress = 0f
                    },
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    shape = CircleShape,
                    modifier = Modifier.size(72.dp)
                ) {
                    Icon(
                        Icons.Default.Refresh,
                        contentDescription = "Reiniciar",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }

        // Selector de duración
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Text(
                        text = "Duración de la sesión",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        val durations = listOf(15, 25, 45, 60)
                        durations.forEach { duration ->
                            DurationChip(
                                duration = duration,
                                isSelected = selectedDuration == duration,
                                onClick = {
                                    if (!isTimerRunning) {
                                        selectedDuration = duration
                                        sessionDurationSeconds = duration * 60
                                        timeLeft = duration * 60
                                        progress = 0f
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // Apps bloqueadas
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 12.dp)
                    ) {
                        Icon(
                            Icons.Outlined.Block,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = "Apps bloqueadas durante enfoque",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    blockedApps.take(4).forEach { (appName, icon) ->
                        BlockedAppItem(appName = appName, icon = icon)
                    }

                    // Botón para ver más apps
                    TextButton(
                        onClick = { /* Mostrar más apps bloqueadas */ },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Ver más apps bloqueadas",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Espacio adicional para el bottom bar
        item {
            Spacer(modifier = Modifier.height(80.dp))
        }
    }

    if (showDurationPicker) {
        DurationPickerDialog(
            initialSeconds = sessionDurationSeconds,
            onDismiss = { showDurationPicker = false },
            onConfirm = { totalSeconds ->
                sessionDurationSeconds = totalSeconds
                selectedDuration = totalSeconds / 60
                timeLeft = totalSeconds
                progress = 0f
                isTimerRunning = false
                showDurationPicker = false
            }
        )
    }
}

@Composable
fun DurationChip(
    duration: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(60.dp)
            .height(40.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primary
            else
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(),  // ← Asegura que el Box ocupe todo el espacio de la Card
            contentAlignment = Alignment.Center  // ← Centra el contenido
        ) {
            Text(
                text = "${duration}m",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected)
                    MaterialTheme.colorScheme.onPrimary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun BlockedAppItem(
    appName: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = appName,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        Icon(
            Icons.Default.Lock,
            contentDescription = "Bloqueada",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp)
        )
    }
}
