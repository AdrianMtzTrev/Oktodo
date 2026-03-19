package com.example.oktodo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.oktodo.navigation.AppNavigation
import com.example.oktodo.ui.theme.OkTodoTheme
import com.example.oktodo.ui.viewmodel.TasksViewModel
import com.example.oktodo.ui.viewmodel.ThemeViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val themeViewModel: ThemeViewModel = viewModel()
            val tasksViewModel: TasksViewModel = viewModel()

            OkTodoTheme(darkTheme = themeViewModel.isDarkMode) {
                AppNavigation(
                    themeViewModel = themeViewModel,
                    tasksViewModel = tasksViewModel
                )
            }
        }
    }
}