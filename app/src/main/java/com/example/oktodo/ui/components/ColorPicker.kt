package com.example.oktodo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border  // ← IMPORTANTE: Para .border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons  // ← Para Icons
import androidx.compose.material.icons.filled.Check  // ← Para Icons.Default.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

val EventColors = listOf(
    Color(0xFF9F7AEA), // Morado (tu color principal)
    Color(0xFFF06292), // Rosa
    Color(0xFFFF6B6B), // Rojo coral
    Color(0xFFFFB347), // Naranja
    Color(0xFF4ECDC4), // Turquesa
    Color(0xFF45B7D1), // Azul claro
    Color(0xFF96CEB4), // Verde menta
    Color(0xFFFFEEAD), // Amarillo pastel
    Color(0xFFD4A5A5), // Rosa viejo
    Color(0xFF9B9B9B)  // Gris
)

@Composable
fun ColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Color del evento",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(EventColors.size) { index ->
                val color = EventColors[index]
                ColorItem(
                    color = color,
                    isSelected = color == selectedColor,
                    onClick = { onColorSelected(color) }
                )
            }
        }
    }
}

@Composable
fun ColorItem(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(color)
            .clickable { onClick() }
            .then(
                if (isSelected) {
                    Modifier.border(
                        width = 3.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                } else {
                    Modifier
                }
            )
            .padding(4.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}