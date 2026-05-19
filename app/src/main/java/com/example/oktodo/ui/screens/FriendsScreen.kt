package com.example.oktodo.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.oktodo.ui.components.CreateGroupBottomSheet
import com.example.oktodo.ui.components.CreateSharedEventBottomSheet
import com.example.oktodo.ui.components.FriendCard
import com.example.oktodo.ui.components.FriendsTabSwitcher
import com.example.oktodo.ui.components.FriendsTopBar
import com.example.oktodo.ui.components.GroupCard
import com.example.oktodo.ui.components.SearchFriendBottomSheet
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
        AuthContent(
            isSignupMode = socialState.isSignupMode,
            authError = socialState.authError,
            isLoading = socialState.isAuthLoading,
            onSignup = { username, password, confirm ->
                viewModel.signup(username, password, confirm)
            },
            onLogin = { username, password ->
                viewModel.login(username, password)
            },
            onToggleMode = { viewModel.toggleMode() }
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
    val searchResults by viewModel.searchResults.collectAsState()

    var selectedTab by remember { mutableStateOf(0) }
    var showCreateGroupSheet by remember { mutableStateOf(false) }
    var showCreateEventSheet by remember { mutableStateOf(false) }
    var showSearchFriendSheet by remember { mutableStateOf(false) }

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
                        action = "Buscar amigo",
                        actionColor = accent,
                        onActionClick = { showSearchFriendSheet = true }
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

    if (showSearchFriendSheet) {
        SearchFriendBottomSheet(
            searchResults = searchResults,
            onQueryChange = { viewModel.setSearchQuery(it) },
            onAddFriend = { /* TODO: enviar solicitud de amistad */ },
            onDismiss = {
                showSearchFriendSheet = false
                viewModel.setSearchQuery("")
            }
        )
    }
}

@Composable
private fun AuthContent(
    isSignupMode: Boolean,
    authError: String?,
    isLoading: Boolean,
    onSignup: (username: String, password: String, confirmPassword: String) -> Unit,
    onLogin: (username: String, password: String) -> Unit,
    onToggleMode: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(60.dp))

        Text(
            text = if (isSignupMode) "Crear cuenta" else "Iniciar sesión",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isSignupMode)
                "Conecta con tus amigos, crea grupos y comparte eventos"
            else
                "Bienvenido de nuevo",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        OutlinedTextField(
            value = username,
            onValueChange = {
                username = it.lowercase().replace(" ", "_")
            },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Usuario") },
            placeholder = { Text("tu_usuario") },
            prefix = { Text("@") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None
                else PasswordVisualTransformation(),
            shape = RoundedCornerShape(16.dp)
        )

        if (isSignupMode) {
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                visualTransformation = if (showPassword) VisualTransformation.None
                    else PasswordVisualTransformation(),
                isError = authError != null,
                shape = RoundedCornerShape(16.dp)
            )
        }

        if (authError != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = authError,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (isSignupMode) onSignup(username, password, confirmPassword)
                else onLogin(username, password)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(16.dp),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = MaterialTheme.colorScheme.onPrimary,
                    strokeWidth = 2.dp
                )
            } else {
                Text(
                    text = if (isSignupMode) "Crear cuenta" else "Iniciar sesión",
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isSignupMode)
                "¿Ya tienes cuenta? Inicia sesión"
            else
                "¿No tienes cuenta? Regístrate",
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.clickable { onToggleMode() }
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
