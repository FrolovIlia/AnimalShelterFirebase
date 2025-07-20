package com.pixelrabbit.animalshelterfirebase.ui.tasks_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pixelrabbit.animalshelterfirebase.data.Task
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.MainScreenViewModel


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
    val db = remember { Firebase.firestore }
    val context = LocalContext.current

    val isAdmin by viewModel.isAdmin.collectAsState()

    LaunchedEffect(navData.uid) {
        viewModel.checkIfUserIsAdmin(navData.uid)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Задачи") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isAdmin) {
                Button(
                    onClick = onAddTaskClick,
                    modifier = Modifier.padding(horizontal = 16.dp)
                ) {
                    Text("Добавить задачу")
                }
            }

            Text(
                text = "Ваши задачи появятся здесь...",
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}
