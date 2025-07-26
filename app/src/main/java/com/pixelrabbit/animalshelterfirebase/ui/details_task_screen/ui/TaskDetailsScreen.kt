package com.pixelrabbit.animalshelterfirebase.ui.details_task_screen.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.pixelrabbit.animalshelterfirebase.R
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundGray
import com.pixelrabbit.animalshelterfirebase.utils.ButtonBlue
import com.pixelrabbit.animalshelterfirebase.utils.InfoTag

@Composable
fun TaskDetailsScreen(
    imageUrl: String = "",
    title: String,
    description: String,
    category: String,
    location: String,
    onBackClick: () -> Unit = {},
    onRespondClick: () -> Unit = {}
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .systemBarsPadding()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(360.dp)
                .padding(16.dp)
        ) {
            if (imageUrl.isBlank()) {
                Image(
                    painter = painterResource(id = R.drawable.default_animal_image),
                    contentDescription = "Placeholder image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(30.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = title,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(30.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.default_animal_image),
                    placeholder = painterResource(id = R.drawable.default_animal_image)
                )
            }

            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Назад",
                    tint = Color.Unspecified
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = title,
                color = Color.Black,
                fontFamily = AnimalFont,
                fontSize = 28.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            InfoTag(text = category, backgroundColor = Color(0xFF90CAF9))

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = description,
                color = Color.Gray,
                fontFamily = AnimalFont,
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Место выполнения: $location",
                color = Color.Gray,
                fontFamily = AnimalFont,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ButtonBlue(
                text = "Откликнуться",
                modifier = Modifier.fillMaxWidth(),
                onClick = onRespondClick
            )
        }
    }
}
