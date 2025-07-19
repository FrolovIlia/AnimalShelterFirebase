package com.pixelrabbit.animalshelterfirebase.ui.tasks_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelrabbit.animalshelterfirebase.data.Task
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.ButtonColorBlue


@Composable
fun TasksScreen(
    onBack: () -> Unit,
    task: Task,
    onSubmitSuccess: () -> Boolean,
    isAdmin: Boolean,
    onAddTaskClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("Назад", fontSize = 16.sp, fontFamily = AnimalFont)
            }

            Text(
                "Список дел",
                fontSize = 20.sp,
                fontFamily = AnimalFont,
                modifier = Modifier.align(Alignment.Center)
            )

            if (isAdmin) {
                TextButton(
                    onClick = onAddTaskClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Text("Добавить\nзадачу", fontSize = 16.sp, fontFamily = AnimalFont)
                }

            }
        }


    }
}
