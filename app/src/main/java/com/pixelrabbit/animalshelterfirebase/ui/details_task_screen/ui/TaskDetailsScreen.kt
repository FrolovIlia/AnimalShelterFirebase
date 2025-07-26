package com.pixelrabbit.animalshelterfirebase.ui.task_details_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.pixelrabbit.animalshelterfirebase.R
import com.pixelrabbit.animalshelterfirebase.ui.details_task_screen.data.TaskDetailsNavObject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsScreen(
    navObject: TaskDetailsNavObject,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(navObject.shortDescription) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (navObject.imageUrl.isNotBlank()) {
                AsyncImage(
                    model = navObject.imageUrl,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(id = R.drawable.default_animal_image),
                    error = painterResource(id = R.drawable.default_animal_image)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            Text("Описание:", style = MaterialTheme.typography.titleMedium)
            Text(navObject.fullDescription, modifier = Modifier.padding(vertical = 8.dp))

            Text("Куратор: ${navObject.curatorName} — ${navObject.curatorPhone}")
            Text("Локация: ${navObject.location}")
            Text("Срочность: ${navObject.urgency}")
            Text("Категория: ${navObject.category}")
        }
    }
}
