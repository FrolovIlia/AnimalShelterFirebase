package com.pixelrabbit.animalshelterfirebase.ui.tasks_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pixelrabbit.animalshelterfirebase.data.Task
import com.pixelrabbit.animalshelterfirebase.ui.details_task_screen.data.TaskDetailsNavObject
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.MainScreenViewModel
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundGray
import com.pixelrabbit.animalshelterfirebase.utils.SearchField
import com.pixelrabbit.animalshelterfirebase.ui.authorization.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    task: Task,
    onSubmitSuccess: () -> Unit,
    onAddTaskClick: () -> Unit,
    viewModel: MainScreenViewModel,
    userViewModel: UserViewModel,
    navData: TaskNavObject,
    navController: NavController
) {
    val user by userViewModel.currentUser.collectAsState()
    val isAdmin by userViewModel.isAdmin.collectAsState()
    val tasks by viewModel.tasks.collectAsState()

    var query by remember { mutableStateOf("") }

    val filteredTasks = remember(query, tasks) {
        tasks.filter {
            it.shortDescription.contains(query, ignoreCase = true) ||
                    it.fullDescription.contains(query, ignoreCase = true) ||
                    it.curatorName.contains(query, ignoreCase = true)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadTasks()
    }

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(color = BackgroundGray, darkIcons = true)
    }

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
        ) {
            SearchField(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                placeholder = "Поиск по задачам"
            )

            if (filteredTasks.isEmpty()) {
                Text(
                    text = "Задач пока нет.",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(
                        horizontal = 8.dp,
                        vertical = 12.dp)
                ) {
                    items(filteredTasks) { taskItem ->
                        TaskListItemUI(
                            task = taskItem,
                            isAdmin = isAdmin,
                            onEditClick = {
                                navController.navigate("edit_task_screen/${taskItem.key}")
                            },
                            onTaskClick = {
                                navController.navigate(
                                    TaskDetailsNavObject(
                                        uid = navData.uid,
                                        imageUrl = taskItem.imageUrl,
                                        shortDescription = taskItem.shortDescription,
                                        fullDescription = taskItem.fullDescription,
                                        curatorName = taskItem.curatorName,
                                        curatorPhone = taskItem.curatorPhone,
                                        location = taskItem.location,
                                        urgency = taskItem.urgency,
                                        category = taskItem.category
                                    )
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}