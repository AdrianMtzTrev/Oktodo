package com.example.oktodo.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.oktodo.ui.screens.CalendarScreen
import com.example.oktodo.ui.screens.DashboardScreen
import com.example.oktodo.ui.screens.FocusScreen
import com.example.oktodo.ui.screens.FriendsScreen
import com.example.oktodo.ui.screens.GroupDetailScreen
import com.example.oktodo.ui.screens.NotificationsScreen
import com.example.oktodo.ui.screens.SharedEventDetailScreen
import com.example.oktodo.ui.screens.OctoShopScreen
import com.example.oktodo.ui.screens.ProfileScreen
import com.example.oktodo.ui.screens.SettingsScreen
import com.example.oktodo.ui.viewmodel.CalendarViewModel
import com.example.oktodo.ui.viewmodel.FriendsViewModel
import com.example.oktodo.ui.viewmodel.NotificationViewModel
import com.example.oktodo.ui.viewmodel.ProfileViewModel
import com.example.oktodo.ui.viewmodel.TasksViewModel
import com.example.oktodo.ui.viewmodel.ThemeViewModel

@Composable
fun AppNavigation(themeViewModel: ThemeViewModel) {
    val navController = rememberNavController()
    val tasksViewModel: TasksViewModel = hiltViewModel()
    val calendarViewModel: CalendarViewModel = hiltViewModel()
    val friendsViewModel: FriendsViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val notificationViewModel: NotificationViewModel = hiltViewModel()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "dashboard",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("dashboard") {
                DashboardScreen(
                    navController = navController,
                    themeViewModel = themeViewModel,
                    tasksViewModel = tasksViewModel,
                    notificationViewModel = notificationViewModel
                )
            }
            composable("calendario") {
                CalendarScreen(calendarViewModel = calendarViewModel)
            }
            composable("focus") {
                FocusScreen()
            }
            composable("friends") {
                FriendsScreen(
                    navController = navController,
                    viewModel = friendsViewModel
                )
            }
            composable("group_detail/{groupId}") { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId").orEmpty()
                GroupDetailScreen(
                    navController = navController,
                    groupId = groupId,
                    viewModel = friendsViewModel
                )
            }
            composable("shared_event_detail/{groupId}/{eventId}") { backStackEntry ->
                val groupId = backStackEntry.arguments?.getString("groupId").orEmpty()
                val eventId = backStackEntry.arguments?.getString("eventId").orEmpty()
                SharedEventDetailScreen(
                    navController = navController,
                    groupId = groupId,
                    eventId = eventId,
                    viewModel = friendsViewModel
                )
            }
            composable("profile") {
                ProfileScreen(
                    navController = navController,
                    viewModel = profileViewModel
                )
            }
            composable("notifications") {
                NotificationsScreen(
                    onBack = { navController.popBackStack() },
                    viewModel = notificationViewModel
                )
            }
            composable("settings") {
                SettingsScreen(
                    navController = navController,
                    viewModel = profileViewModel
                )
            }
            composable("octo_shop") {
                OctoShopScreen(
                    navController = navController,
                    viewModel = profileViewModel
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Inicio", Icons.Outlined.Home, "dashboard"),
        BottomNavItem("Calendario", Icons.Outlined.CalendarMonth, "calendario"),
        BottomNavItem("Enfoque", Icons.Outlined.TrackChanges, "focus"),
        BottomNavItem("Amigos", Icons.Outlined.Groups, "friends"),
        BottomNavItem("Perfil", Icons.Outlined.Person, "profile")
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val selected = currentDestination?.hierarchy?.any { destination ->
                destination.route == item.route ||
                    (item.route == "friends" && destination.route?.startsWith("group_detail") == true) ||
                    (item.route == "profile" && (
                        destination.route == "settings" || destination.route == "octo_shop"
                    ))
            } == true

            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (selected) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        color = if (selected) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            )
        }
    }
}

data class BottomNavItem(val label: String, val icon: ImageVector, val route: String)
