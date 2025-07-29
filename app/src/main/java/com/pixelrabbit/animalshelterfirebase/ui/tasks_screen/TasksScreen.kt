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
import com.pixelrabbit.animalshelterfirebase.data.Task
import com.pixelrabbit.animalshelterfirebase.data.TaskCategories
import com.pixelrabbit.animalshelterfirebase.ui.authorization.UserViewModel
import com.pixelrabbit.animalshelterfirebase.ui.details_task_screen.data.TaskDetailsNavObject
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.MainScreenViewModel
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundGray
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundSecondary
import com.pixelrabbit.animalshelterfirebase.ui.theme.ButtonColorBlue
import com.pixelrabbit.animalshelterfirebase.ui.theme.ButtonColorWhite
import com.pixelrabbit.animalshelterfirebase.ui.theme.TextBlack
import com.pixelrabbit.animalshelterfirebase.ui.theme.TextSecondary
import com.pixelrabbit.animalshelterfirebase.utils.SearchField

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
    var selectedCategory by remember { mutableStateOf("Все") }

    val filteredTasks = remember(query, tasks, selectedCategory) {
        tasks.filter {
            val matchesQuery = it.shortDescription.contains(query, ignoreCase = true) ||
                    it.fullDescription.contains(query, ignoreCase = true) ||
                    it.curatorName.contains(query, ignoreCase = true)

            val matchesCategory = selectedCategory == "Все" || it.category == selectedCategory

            matchesQuery && matchesCategory
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
                onQueryChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                placeholder = "Поиск по задачам"
            )

            // Категории задач — LazyRow
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp),
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
                            .height(52.dp) // как на главной!
                            .clip(RoundedCornerShape(30.dp))
                            .clickable { selectedCategory = category.title },
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
                                    .background(contentColor.copy(alpha = 0.15f)) // мягкий фон под иконкой
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
                                fontSize = 13.sp, // как на главной
                                fontFamily = AnimalFont,
                                color = contentColor
                            )
                        }
                    }
                }
            }




            if (filteredTasks.isEmpty()) {
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
