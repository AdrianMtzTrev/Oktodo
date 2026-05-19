package com.example.oktodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
    val socialState by viewModel.socialState.collectAsState()

    if (socialState.isRegistered) {
        SocialContent(
            viewModel = viewModel,
            navController = navController
        )
    } else {
        RegisterContent(
            displayName = socialState.displayName,
            avatarEmoji = socialState.avatarEmoji,
            usernameError = socialState.usernameError,
            isRegistering = socialState.isRegistering,
            onRegister = { name, username, avatar ->
                viewModel.register(name, username, avatar)
            }
        )
    }
}

@Composable
private fun SocialContent(
    viewModel: FriendsViewModel,
    navController: NavController?
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
private fun RegisterContent(
    displayName: String,
    avatarEmoji: String,
    usernameError: String?,
    isRegistering: Boolean,
    onRegister: (displayName: String, username: String, avatarEmoji: String) -> Unit
) {
    var name by remember { mutableStateOf(displayName) }
    var username by remember { mutableStateOf("") }
    var selectedAvatar by remember { mutableStateOf(avatarEmoji) }

    val avatarOptions = listOf("🐙", "🐱", "🐶", "🦊", "🐼", "🐸", "🦄", "🐻")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Únete a la comunidad",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Conecta con tus amigos, crea grupos y comparte eventos",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        Surface(
            modifier = Modifier.size(96.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = selectedAvatar,
                    style = MaterialTheme.typography.displayMedium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Tu avatar",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            avatarOptions.chunked(4).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    rowItems.forEach { avatar ->
                        FilterChip(
                            selected = selectedAvatar == avatar,
                            onClick = { selectedAvatar = avatar },
                            label = { Text(avatar) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre") },
            placeholder = { Text("Tu nombre") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it.lowercase().replace(" ", "_")
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Nombre de usuario") },
            placeholder = { Text("tu_usuario") },
            prefix = { Text("@") },
            singleLine = true,
            isError = usernameError != null,
            supportingText = usernameError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { onRegister(name, username, selectedAvatar) },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = !isRegistering
        ) {
            if (isRegistering) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Unirse a la comunidad", fontWeight = FontWeight.Bold)
            }
        }
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
