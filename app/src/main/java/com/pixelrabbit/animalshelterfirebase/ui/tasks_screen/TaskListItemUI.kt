package com.pixelrabbit.animalshelterfirebase.ui.tasks_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pixelrabbit.animalshelterfirebase.R
import com.pixelrabbit.animalshelterfirebase.data.model.Task
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundWhite

@Composable
fun TaskListItemUI(
    task: Task,
    isAdmin: Boolean = false,
    onEditClick: (Task) -> Unit = {},
    onTaskClick: (Task) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(195.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(BackgroundWhite)
            .clickable { onTaskClick(task) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            if (task.imageUrl.isNullOrBlank()) {
                Image(
                    painter = painterResource(id = R.drawable.default_animal_image),
                    contentDescription = "Placeholder image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(15.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = task.imageUrl,
                    contentDescription = task.shortDescription,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(15.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.default_animal_image),
                    placeholder = painterResource(id = R.drawable.default_animal_image)
                )
            }

            // Получаем цвета для точки и ободка
            val (mainColor, borderColor) = getUrgencyColors(task.urgency)

            // Точка с ободком, цвет которого зависит от цвета точки
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 8.dp, end = 8.dp)
                    .size(16.dp)
                    .clip(RoundedCornerShape(50))
                    .border(1.dp, borderColor, RoundedCornerShape(50)) // Используем цвет для ободка
                    .background(mainColor)
            )

            if (isAdmin) {
                IconButton(
                    onClick = { onEditClick(task) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = Color.Black
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Text(
                text = task.shortDescription,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

// Теперь функция возвращает пару цветов: основной и для ободка
@Composable
fun getUrgencyColors(urgency: String): Pair<Color, Color> {
    return when (urgency.trim()) {
        "Низкая" -> Pair(Color.Green, Color(0xFF006400)) // Темно-зеленый
        "Средняя" -> Pair(Color(0xFFFFA500), Color(0xFFCC8400)) // Темно-оранжевый
        "Высокая" -> Pair(Color.Red, Color(0xFF8B0000)) // Темно-красный
        "Критическая" -> Pair(Color(0xFF8B0000), Color(0xFF5A0000)) // Очень темно-красный
        else -> Pair(Color.Gray, Color.DarkGray)
    }
}