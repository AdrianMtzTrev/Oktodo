@file:Suppress("SpellCheckingInspection")

package com.example.oktodo.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.oktodo.ui.model.ShopItem
import com.example.oktodo.ui.viewmodel.ProfileViewModel
import com.example.oktodo.ui.viewmodel.PurchaseEvent

@Composable
fun OctoShopScreen(
    navController: NavController,
    viewModel: ProfileViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val shopItems by viewModel.shopItems.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var selectedItemForPurchase by remember { mutableStateOf<ShopItem?>(null) }

    LaunchedEffect(Unit) {
        viewModel.purchaseEvent.collect { event ->
            when (event) {
                is PurchaseEvent.Success -> {
                    snackbarHostState.showSnackbar("¡Has comprado ${event.itemName}!")
                }
                is PurchaseEvent.InsufficientPoints -> {
                    snackbarHostState.showSnackbar("No tienes suficientes puntos")
                }
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            OctoShopHeader(
                points = uiState.points,
                onBackClick = { navController.popBackStack() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = uiState.avatarEmoji,
                        style = MaterialTheme.typography.displayMedium
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Personaliza a tu pulpito",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Compra accesorios con tus puntos",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
            ) {
                items(shopItems, key = { it.id }) { item ->
                    ShopItemCard(
                        item = item,
                        userPoints = uiState.points,
                        onBuyClick = { selectedItemForPurchase = item },
                        onEquipClick = { viewModel.equipItem(item.id) },
                        onUnequipClick = { viewModel.unequipItem(item.id) }
                    )
                }
            }
        }
    }

    selectedItemForPurchase?.let { item ->
        PurchaseConfirmDialog(
            item = item,
            onConfirm = {
                viewModel.purchaseItem(item.id)
                selectedItemForPurchase = null
            },
            onDismiss = { selectedItemForPurchase = null }
        )
    }
}

@Composable
private fun ShopItemCard(
    item: ShopItem,
    userPoints: Int,
    onBuyClick: () -> Unit,
    onEquipClick: () -> Unit,
    onUnequipClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = item.emoji,
                style = MaterialTheme.typography.displaySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = item.category,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (item.isPurchased) {
                if (item.isEquipped) {
                    Button(
                        onClick = onUnequipClick,
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Equipado ✓")
                    }
                } else {
                    OutlinedButton(
                        onClick = onEquipClick,
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text("Equipar")
                    }
                }
            } else {
                Button(
                    onClick = onBuyClick,
                    shape = RoundedCornerShape(14.dp),
                    enabled = userPoints >= item.price
                ) {
                    Text("⭐ ${item.price}")
                }
            }
        }
    }
}

@Composable
private fun PurchaseConfirmDialog(
    item: ShopItem,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Text(text = item.emoji, style = MaterialTheme.typography.displayMedium)
        },
        title = {
            Text("¿Comprar ${item.title}?")
        },
        text = {
            Column {
                Text("¿Estás seguro de que quieres comprar este accesorio?")
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Precio: ⭐ ${item.price}",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "Se descontarán de tus puntos",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Comprar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun OctoShopHeader(
    points: Int,
    onBackClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }

            Text(
                text = "Tienda del pulpito",
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            AssistChip(
                onClick = {},
                label = {
                    Text("⭐ $points")
                }
            )
        }
    }
}