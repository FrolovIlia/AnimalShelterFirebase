package com.pixelrabbit.animalshelterfirebase.ui.tasks_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import com.pixelrabbit.animalshelterfirebase.data.Task
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

        Spacer(modifier = Modifier.height(10.dp))

        Column(modifier = Modifier.padding(8.dp)) {
//            Text(
//                text = task.shortDescription,
//                color = Color.Black,
//                fontWeight = FontWeight.Bold,
//                fontSize = 20.sp,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis
//            )
//
//            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = task.shortDescription,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
//
//            Spacer(modifier = Modifier.height(4.dp))

//            Text(
//                text = "Куратор: ${task.curatorName} | Тел: ${task.curatorPhone}",
//                color = Color.LightGray,
//                fontSize = 12.sp
//            )

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
