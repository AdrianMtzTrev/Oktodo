package com.example.oktodo.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun OkTodoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = OkTodoPurple,
            onPrimary = Color.White,
            primaryContainer = OkTodoDarkPurple,
            onPrimaryContainer = Color.White,
            secondary = OkTodoLightPurple,
            onSecondary = Color.Black,
            background = OkTodoDarkBackground,
            onBackground = Color.White,
            surface = OkTodoDarkSurface,
            onSurface = Color.White.copy(alpha = 0.95f),
            surfaceVariant = OkTodoDarkSurface.copy(alpha = 0.8f),
            onSurfaceVariant = Color.White.copy(alpha = 0.7f)
        )
    } else {
        lightColorScheme(
            primary = OkTodoPurple,
            onPrimary = Color.White,
            primaryContainer = OkTodoLightPurple,
            onPrimaryContainer = Color.Black,
            secondary = OkTodoDarkPurple,
            onSecondary = Color.White,
            background = OkTodoBackground,
            onBackground = Color.Black,
            surface = OkTodoSurface,
            onSurface = Color.Black.copy(alpha = 0.87f),
            surfaceVariant = OkTodoBackground,
            onSurfaceVariant = Color.Black.copy(alpha = 0.6f)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}