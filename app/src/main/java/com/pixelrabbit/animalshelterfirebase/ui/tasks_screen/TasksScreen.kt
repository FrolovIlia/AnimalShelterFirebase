package com.pixelrabbit.animalshelterfirebase.ui.tasks_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pixelrabbit.animalshelterfirebase.data.Task
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.MainScreenViewModel
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundGray
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.windowInsetsTopHeight


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    task: Task,
    onSubmitSuccess: () -> Unit,
    onAddTaskClick: () -> Unit,
    viewModel: MainScreenViewModel,
    navData: TaskNavObject,
    navController: NavController
) {
    val isAdmin by viewModel.isAdmin.collectAsState()

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(color = BackgroundGray, darkIcons = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = BackgroundGray,
            topBar = {
                TopAppBar(
                    title = {
                        Box(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Задачи",
                                fontSize = 20.sp,
                                fontFamily = AnimalFont,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                    },
                    navigationIcon = {
                        TextButton(onClick = { navController.navigateUp() }) {
                            Text(
                                text = "Назад",
                                fontSize = 16.sp,
                                fontFamily = AnimalFont
                            )
                        }
                    },
                    actions = {
                        if (isAdmin) {
                            TextButton(onClick = onAddTaskClick) {
                                Text(
                                    text = "Добавить\nзадачу",
                                    fontSize = 16.sp,
                                    fontFamily = AnimalFont
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(80.dp))
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = BackgroundGray
                    )
                )
            }
        ) { paddingValues ->

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "Ваши задачи появятся здесь...",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}