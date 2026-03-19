package com.example.oktodo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun FriendsTabSwitcher(
    tabs: List<String>,
    selectedIndex: Int,
    onSelected: (Int) -> Unit
) {
    val container = MaterialTheme.colorScheme.surface
    val active = MaterialTheme.colorScheme.primary
    val activeText = MaterialTheme.colorScheme.onPrimary
    val inactiveText = MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(container, RoundedCornerShape(18.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        tabs.forEachIndexed { index, title ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(
                        color = if (selectedIndex == index) active else androidx.compose.ui.graphics.Color.Transparent,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onSelected(index) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    color = if (selectedIndex == index) activeText else inactiveText,
                    fontWeight = if (selectedIndex == index) FontWeight.Bold else FontWeight.Medium
                )
            }
        }
    }
}