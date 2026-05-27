package com.example.oktodo.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlin.math.abs

@Composable
fun DurationPickerDialog(
    initialSeconds: Int = 25 * 60,
    onDismiss: () -> Unit,
    onConfirm: (totalSeconds: Int) -> Unit
) {
    val initialHours = initialSeconds / 3600
    val initialMinutes = (initialSeconds % 3600) / 60
    val initialSecs = initialSeconds % 60

    val hoursState = rememberPickerState(0..23, initialHours)
    val minutesState = rememberPickerState(0..59, initialMinutes)
    val secondsState = rememberPickerState(0..59, initialSecs)

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = "Temporizador personalizado",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    NumberPickerColumn(
                        state = hoursState,
                        label = "Horas",
                        modifier = Modifier.width(72.dp)
                    )
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    NumberPickerColumn(
                        state = minutesState,
                        label = "Minutos",
                        modifier = Modifier.width(72.dp)
                    )
                    Text(
                        text = ":",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    NumberPickerColumn(
                        state = secondsState,
                        label = "Segundos",
                        modifier = Modifier.width(72.dp)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val total = hoursState.currentValue * 3600 +
                            minutesState.currentValue * 60 +
                            secondsState.currentValue
                onConfirm(if (total == 0) 60 else total)
            }) {
                Text("Aceptar")
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
private fun NumberPickerColumn(
    state: PickerState,
    label: String,
    modifier: Modifier = Modifier
) {
    val extraSpacers = 2
    val itemHeightDp = 48.dp
    val visibleCount = 5
    val totalHeight = itemHeightDp * visibleCount

    val items = remember(state.range) {
        buildList {
            repeat(extraSpacers) { add(null) }
            state.range.forEach { add(it) }
            repeat(extraSpacers) { add(null) }
        }
    }

    val listState = rememberLazyListState()
    var hasUserScrolled by remember { mutableStateOf(false) }
    var isSnapping by remember { mutableStateOf(false) }
    var initialSnapDone by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        repeat(5) { withFrameNanos { } }
        if (initialSnapDone) return@LaunchedEffect
        initialSnapDone = true
        val layoutInfo = listState.layoutInfo
        if (layoutInfo.visibleItemsInfo.isEmpty()) return@LaunchedEffect
        val itemSize = layoutInfo.visibleItemsInfo.first().size
        val targetIdx = extraSpacers + state.initialIndex
        isSnapping = true
        listState.scrollToItem(
            targetIdx,
            itemSize / 2 - layoutInfo.viewportSize.height / 2
        )
        isSnapping = false
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling) {
                    if (!isSnapping) hasUserScrolled = true
                } else if (hasUserScrolled) {
                    hasUserScrolled = false
                    val layoutInfo = listState.layoutInfo
                    val realItems = layoutInfo.visibleItemsInfo.filter {
                        items.getOrNull(it.index) != null
                    }
                    if (realItems.isNotEmpty()) {
                        val center = layoutInfo.viewportSize.height / 2
                        val closest = realItems
                            .minByOrNull { abs((it.offset + it.size / 2) - center) }
                        closest?.let {
                            val newValue = items[it.index]
                            if (newValue != null) {
                                state.currentValue = newValue
                                isSnapping = true
                                listState.scrollToItem(
                                    it.index,
                                    it.size / 2 - center
                                )
                                isSnapping = false
                            }
                        }
                    }
                }
            }
    }

    val centerIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val realItems = layoutInfo.visibleItemsInfo.filter {
                items.getOrNull(it.index) != null
            }
            if (realItems.isEmpty()) return@derivedStateOf extraSpacers
            val center = layoutInfo.viewportSize.height / 2
            val closest = realItems
                .minByOrNull { abs((it.offset + it.size / 2) - center) }
            closest?.index ?: extraSpacers
        }
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .width(72.dp)
                .height(totalHeight)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(totalHeight),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                itemsIndexed(items) { index, value ->
                    if (value == null) {
                        Spacer(modifier = Modifier
                            .fillMaxWidth()
                            .height(itemHeightDp)
                        )
                    } else {
                        val isCenter = index == centerIndex
                        Text(
                            text = String.format("%02d", value),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = if (isCenter) FontWeight.Bold else FontWeight.Normal,
                            color = if (isCenter)
                                MaterialTheme.colorScheme.onSurface
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(itemHeightDp),
                        )
                    }
                }
            }
        }
    }
}

private class PickerState(
    val range: IntRange,
    val initialIndex: Int,
    var currentValue: Int = range.first + initialIndex
)

@Composable
private fun rememberPickerState(range: IntRange, initialValue: Int): PickerState {
    val normalized = initialValue.coerceIn(range)
    return remember(range, normalized) {
        PickerState(range, normalized - range.first, normalized)
    }
}
