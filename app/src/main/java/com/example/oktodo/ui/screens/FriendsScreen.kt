package com.example.oktodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.oktodo.ui.components.CreateGroupBottomSheet
import com.example.oktodo.ui.components.CreateSharedEventBottomSheet
import com.example.oktodo.ui.components.FriendCard
import com.example.oktodo.ui.components.FriendsTabSwitcher
import com.example.oktodo.ui.components.FriendsTopBar
import com.example.oktodo.ui.components.GroupCard
import com.example.oktodo.ui.components.SharedEventCard
import com.example.oktodo.ui.viewmodel.FriendsViewModel

@Composable
fun FriendsScreen(
    navController: NavController? = null,
    viewModel: FriendsViewModel
) {
    val friends by viewModel.friends.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val events by viewModel.sharedEvents.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showCreateGroupSheet by remember { mutableStateOf(false) }
    var showCreateEventSheet by remember { mutableStateOf(false) }

    val background = MaterialTheme.colorScheme.background
    val textMain = MaterialTheme.colorScheme.onBackground
    val accent = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
    ) {
        FriendsTopBar(
            onShareClick = { },
            onAddClick = { showCreateEventSheet = true }
        )

        Column(
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            FriendsTabSwitcher(
                tabs = listOf("Amigos", "Grupos", "Eventos"),
                selectedIndex = selectedTab,
                onSelected = { selectedTab = it }
            )

            Spacer(modifier = Modifier.height(18.dp))

            when (selectedTab) {
                0 -> {
                    SectionHeader(
                        icon = {
                            Icon(Icons.Outlined.People, contentDescription = null, tint = textMain)
                        },
                        title = "Mis Amigos",
                        action = "Crear grupo",
                        actionColor = accent,
                        onActionClick = { showCreateGroupSheet = true }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(friends) { friend ->
                            FriendCard(friend = friend)
                        }
                    }
                }

                1 -> {
                    SectionHeader(
                        icon = {
                            Icon(Icons.Outlined.Group, contentDescription = null, tint = textMain)
                        },
                        title = "Mis Grupos",
                        action = "Crear grupo",
                        actionColor = accent,
                        onActionClick = { showCreateGroupSheet = true }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(groups) { group ->
                            GroupCard(
                                group = group,
                                onClick = {
                                    navController?.navigate("group_detail/${group.id}")
                                }
                            )
                        }
                    }
                }

                2 -> {
                    SectionHeader(
                        icon = {
                            Icon(Icons.Outlined.Event, contentDescription = null, tint = textMain)
                        },
                        title = "Eventos Compartidos",
                        action = "",
                        actionColor = accent,
                        onActionClick = { }
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val grouped = events.groupBy { viewModel.getFormattedDateLabel(it.date) }

                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        grouped.forEach { (date, dateEvents) ->
                            item {
                                Text(
                                    text = date,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            items(dateEvents) { event ->
                                SharedEventCard(event = event)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateGroupSheet) {
        CreateGroupBottomSheet(
            friends = friends,
            onDismiss = { showCreateGroupSheet = false },
            onCreateGroup = { name, members ->
                viewModel.createGroup(name, members)
                showCreateGroupSheet = false
            }
        )
    }

    if (showCreateEventSheet) {
        CreateSharedEventBottomSheet(
            groups = groups,
            onDismiss = { showCreateEventSheet = false },
            onCreateEvent = { title, groupId, date, time, location ->
                viewModel.createSharedEvent(
                    title = title,
                    groupId = groupId,
                    date = date,
                    time = time,
                    location = location
                )
                showCreateEventSheet = false
            }
        )
    }
}

@Composable
private fun SectionHeader(
    icon: @Composable () -> Unit,
    title: String,
    action: String,
    actionColor: androidx.compose.ui.graphics.Color,
    onActionClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        if (action.isNotBlank()) {
            Text(
                text = action,
                color = actionColor,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable { onActionClick() }
            )
        }
    }
}