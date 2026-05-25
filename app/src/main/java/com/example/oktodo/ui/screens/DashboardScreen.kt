package com.example.oktodo.ui.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.oktodo.ui.components.CreateTaskBottomSheet
import com.example.oktodo.ui.model.Task
import com.example.oktodo.ui.viewmodel.NotificationViewModel
import com.example.oktodo.ui.viewmodel.TasksViewModel
import com.example.oktodo.ui.viewmodel.ThemeViewModel
import java.util.Calendar

@Composable
fun DashboardScreen(
    navController: NavController,
    themeViewModel: ThemeViewModel,
    tasksViewModel: TasksViewModel,
    notificationViewModel: NotificationViewModel
) {
    val tasks by tasksViewModel.tasks.collectAsState()
    val showBottomSheet by tasksViewModel.showBottomSheet.collectAsState()
    val editingTask by tasksViewModel.editingTask.collectAsState()
    val points by tasksViewModel.points.collectAsState()
    val userName by tasksViewModel.displayName.collectAsState()
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val unreadNotifications by notificationViewModel.unreadCount.collectAsState()
    val pendingCount = tasks.count { !it.isCompleted }

    Scaffold(
        topBar = {
            DashboardHeader(
                onThemeToggle = { themeViewModel.toggleTheme() },
                isDarkMode = isDarkMode,
                points = points,
                notificationCount = unreadNotifications,
                onNotificationsClick = { navController.navigate("notifications") }
            )
        },
        floatingActionButton = {
            DashboardFAB(
                onClick = { tasksViewModel.showBottomSheet() }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        DashboardContent(
            modifier = Modifier.padding(paddingValues),
            tasks = tasks,
            points = points,
            userName = userName,
            onTaskToggle = { tasksViewModel.toggleTaskCompletion(it) },
            onTaskDelete = { tasksViewModel.deleteTask(it) },
            onTaskEdit = { tasksViewModel.editTask(it) }
        )
    }

    if (showBottomSheet) {
        CreateTaskBottomSheet(
            existingTask = editingTask,
            onDismiss = { tasksViewModel.hideBottomSheet() },
            onTaskCreated = { title, time, priority ->
                tasksViewModel.addTask(title, time, priority)
            },
            onTaskUpdated = { title, time, priority ->
                if (editingTask != null) {
                    tasksViewModel.updateTask(editingTask!!, title, time, priority)
                }
            }
        )
    }
}

@Composable
fun DashboardHeader(
    onThemeToggle: () -> Unit,
    isDarkMode: Boolean,
    points: Int,
    notificationCount: Int = 0,
    onNotificationsClick: () -> Unit = {}
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            StreakBadge(streakPoints = points)

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onThemeToggle) {
                    Icon(
                        imageVector = if (isDarkMode) {
                            Icons.Outlined.LightMode
                        } else {
                            Icons.Outlined.DarkMode
                        },
                        contentDescription = if (isDarkMode) {
                            "Cambiar a modo claro"
                        } else {
                            "Cambiar a modo oscuro"
                        },
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = onNotificationsClick) {
                    BadgedBox(
                        badge = {
                            if (notificationCount > 0) {
                                Badge { Text(notificationCount.toString()) }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            contentDescription = "Notificaciones",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StreakBadge(streakPoints: Int) {
    Box(
        modifier = Modifier
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary,
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.75f)
                    )
                ),
                shape = RoundedCornerShape(50)
            )
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🔥",
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "$streakPoints",
                color = MaterialTheme.colorScheme.onPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun DashboardFAB(
    onClick: () -> Unit
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Agregar tarea",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}

@Composable
fun DashboardContent(
    modifier: Modifier = Modifier,
    tasks: List<Task>,
    points: Int,
    userName: String,
    onTaskToggle: (Task) -> Unit,
    onTaskDelete: (Task) -> Unit,
    onTaskEdit: (Task) -> Unit
) {
    val greeting = remember { getGreeting() }

    val pendingTasks = tasks.filter { !it.isCompleted }
    val completedTasks = tasks.filter { it.isCompleted }
    var showCompleted by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))
            WelcomeHeader(
                greeting = greeting,
                userName = userName,
                pendingCount = pendingTasks.size
            )
        }

        item {
            OctoStatusCard(
                pendingCount = pendingTasks.size,
                completedCount = completedTasks.size,
                points = points
            )
        }

        if (pendingTasks.isNotEmpty()) {
            item {
                SectionHeader(title = "Pendientes (${pendingTasks.size})")
            }

            items(pendingTasks, key = { it.id }) { task ->
                TaskItem(
                    task = task,
                    onToggle = { onTaskToggle(task) },
                    onEdit = { onTaskEdit(task) }
                )
            }
        }

        if (completedTasks.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showCompleted = !showCompleted },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SectionHeader(title = "Completadas (${completedTasks.size})")
                    Icon(
                        imageVector = if (showCompleted) Icons.Filled.KeyboardArrowUp
                                      else Icons.Filled.KeyboardArrowDown,
                        contentDescription = if (showCompleted) "Ocultar completadas"
                                             else "Mostrar completadas",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            if (showCompleted) {
                items(completedTasks, key = { it.id }) { task ->
                    TaskItem(
                        task = task,
                        onToggle = { onTaskToggle(task) },
                        onEdit = { onTaskEdit(task) }
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeHeader(
    greeting: String,
    userName: String,
    pendingCount: Int
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = greeting,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = if (pendingCount == 0) {
                "¿Qué haremos hoy, $userName?"
            } else {
                "Tienes $pendingCount tareas por avanzar hoy"
            },
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun OctoStatusCard(
    pendingCount: Int,
    completedCount: Int,
    points: Int
) {
    val title = when {
        pendingCount == 0 && completedCount == 0 -> "¡Todo listo! ✨"
        pendingCount == 0 -> "¡Gran trabajo hoy! 🎉"
        else -> "Vamos paso a paso 🐙"
    }

    val subtitle = when {
        pendingCount == 0 && completedCount == 0 ->
            "No tienes tareas pendientes. ¡Disfruta tu día!"
        pendingCount == 0 ->
            "Completaste $completedCount tareas y ganaste $points puntos."
        else ->
            "Tienes $pendingCount pendientes y $completedCount completadas."
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🐙",
                fontSize = 72.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun TaskItem(
    task: Task,
    onToggle: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (task.isCompleted) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                        if (task.isCompleted) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        } else {
                            task.color
                        }
                    )
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = if (task.isCompleted) {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    },
                    textDecoration = if (task.isCompleted) {
                        TextDecoration.LineThrough
                    } else {
                        null
                    }
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = task.time,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (task.isCompleted) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "+${task.pointsReward} pts",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (!task.isCompleted) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = "Editar tarea",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

fun getGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 0..11 -> "¡Buenos días! ☀️"
        in 12..17 -> "¡Buenas tardes! ⛅"
        else -> "¡Buenas noches! 🌙"
    }
}