package com.pixelrabbit.animalshelterfirebase.ui.tasks_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pixelrabbit.animalshelterfirebase.data.model.Task
import com.pixelrabbit.animalshelterfirebase.data.model.TaskCategories
import com.pixelrabbit.animalshelterfirebase.ui.authorization.UserViewModel
import com.pixelrabbit.animalshelterfirebase.ui.navigation.TaskDetailsNavObject
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.MainScreenViewModel
import com.pixelrabbit.animalshelterfirebase.ui.navigation.TaskNavObject
import com.pixelrabbit.animalshelterfirebase.ui.theme.*
import com.pixelrabbit.animalshelterfirebase.utils.SearchField
import android.util.Log // Добавили Log для отладки

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TasksScreen(
    task: Task,
    onSubmitSuccess: () -> Unit,
    onAddTaskClick: () -> Unit,
    onTaskEditClick: (String) -> Unit,
    viewModel: MainScreenViewModel,
    userViewModel: UserViewModel,
    tasksViewModel: TasksViewModel,
    navData: TaskNavObject,
    navController: NavController
) {
    val user by userViewModel.currentUser.collectAsState()
    val isAdmin by tasksViewModel.isAdmin.collectAsState()

    val tasks by viewModel.tasks.collectAsState()
    val query by tasksViewModel.query
    val selectedCategory by tasksViewModel.selectedCategory

    // Логика фильтрации и постоянной сортировки
    val sortedAndFilteredTasks = remember(query, tasks, selectedCategory) {
        tasks.filter {
            val matchesQuery = it.shortDescription.contains(query, ignoreCase = true) ||
                    it.fullDescription.contains(query, ignoreCase = true) ||
                    it.curatorName.contains(query, ignoreCase = true)

            val matchesCategory = selectedCategory == "Все" || it.category == selectedCategory

            matchesQuery && matchesCategory
        }.sortedByDescending { task ->
            when (task.urgency.trim()) {
                "Критическая" -> 4
                "Высокая" -> 3
                "Средняя" -> 2
                "Низкая" -> 1
                else -> 0
            }
        }
    }

    val shouldRefresh by tasksViewModel.shouldRefreshTasks
    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            viewModel.loadTasks()
            tasksViewModel.setRefreshTasks(false)
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
                        val shape = RoundedCornerShape(30.dp)
                        Card(
                            modifier = Modifier
                                .width(105.dp)
                                .height(52.dp)
                                .border(1.dp, BackgroundSecondary, shape)
                                .clip(shape)
                                .clickable { onAddTaskClick() },
                            shape = shape,
                            colors = CardDefaults.cardColors(containerColor = ButtonColorWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Box(
                                Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 8.dp)
                            ) {
                                Text(
                                    text = "Добавить задачу",
                                    fontFamily = AnimalFont,
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    maxLines = 2,
                                    softWrap = true,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
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
                onQueryChange = { tasksViewModel.updateQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = "Поиск по задачам"
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
            ) {
                items(TaskCategories.allCategories) { category ->
                    val isSelected = selectedCategory == category.title

                    val backgroundColor = if (isSelected) ButtonColorBlue else ButtonColorWhite
                    val contentColor = if (isSelected) TextBlack else TextSecondary

                    Surface(
                        modifier = Modifier
                            .height(52.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .clickable { tasksViewModel.updateSelectedCategory(category.title) },
                        shape = RoundedCornerShape(30.dp),
                        color = backgroundColor,
                        border = BorderStroke(
                            width = 1.dp,
                            color = if (isSelected) ButtonColorBlue else BackgroundSecondary
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.padding(horizontal = 14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(contentColor.copy(alpha = 0.15f))
                            ) {
                                Image(
                                    painter = painterResource(id = category.iconResId),
                                    contentDescription = category.title,
                                    modifier = Modifier
                                        .size(50.dp)
                                        .align(Alignment.Center)
                                )
                            }

                            Text(
                                text = category.title,
                                fontSize = 13.sp,
                                fontFamily = AnimalFont,
                                color = contentColor
                            )
                        }
                    }
                }
            }

            if (sortedAndFilteredTasks.isEmpty()) {
                Text(
                    text = "Задач пока нет.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 12.dp)
                ) {
                    items(sortedAndFilteredTasks) { taskItem ->
                        TaskListItemUI(
                            task = taskItem,
                            isAdmin = isAdmin,
                            onEditClick = {
                                // Добавлена проверка на null, чтобы избежать вылета
                                if (it.key != null) {
                                    navController.navigate("edit_task_screen/${it.key}")
                                } else {
                                    Log.e("TasksScreen", "Task key is null, cannot navigate to edit screen.")
                                }
                            },
                            onTaskClick = {
                                navController.navigate(
                                    TaskDetailsNavObject(
                                        uid = navData.uid,
                                        imageUrl = it.imageUrl,
                                        shortDescription = it.shortDescription,
                                        fullDescription = it.fullDescription,
                                        curatorName = it.curatorName,
                                        curatorPhone = it.curatorPhone,
                                        location = it.location,
                                        urgency = it.urgency,
                                        category = it.category
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
