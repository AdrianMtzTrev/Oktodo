@file:Suppress("SpellCheckingInspection")

package com.example.oktodo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.IosShare
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun FriendsTopBar(
    points: Int = 0,
    onShareClick: () -> Unit,
    onAddClick: () -> Unit
) {
    val bg = MaterialTheme.colorScheme.background
    val primary = MaterialTheme.colorScheme.primary
    val accentContainer = MaterialTheme.colorScheme.primaryContainer
    val accentText = MaterialTheme.colorScheme.onPrimaryContainer
    val textMain = MaterialTheme.colorScheme.onBackground
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val buttonOnPrimary = MaterialTheme.colorScheme.onPrimary

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(bg)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Surface(
                shape = CircleShape,
                color = accentContainer,
                shadowElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Star,
                        contentDescription = null,
                        tint = accentText,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = points.toString(),
                        color = accentText,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                FilledIconButton(
                    onClick = onShareClick,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = primary,
                        contentColor = buttonOnPrimary
                    )
                ) {
                    Icon(Icons.Outlined.IosShare, contentDescription = "Compartir")
                }

                FilledIconButton(
                    onClick = onAddClick,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = primary,
                        contentColor = buttonOnPrimary
                    )
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = "Agregar")
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = "Amigos",
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = textMain
        )

        Text(
            text = "Organiza y comparte eventos",
            fontSize = 14.sp,
            color = textSecondary
        )
    }
}
