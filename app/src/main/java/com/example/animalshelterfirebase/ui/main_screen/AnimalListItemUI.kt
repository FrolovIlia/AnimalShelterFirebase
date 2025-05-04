package com.example.animalshelterfirebase.ui.main_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.animalshelterfirebase.R
import com.example.animalshelterfirebase.data.Animal
import com.example.animalshelterfirebase.ui.theme.BackgroundWhite

@Composable
fun AnimalListItemUI(
    showEditButton: Boolean = false,
    animal: Animal,
    onEditClick: (Animal) -> Unit = {},
    onFavouriteClick: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(BackgroundWhite)
    ) {
        Box(modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
        ) {
            AsyncImage(
                model = animal.imageUrl,
                contentDescription = "",
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(15.dp)),
                contentScale = ContentScale.Crop
            )

            IconButton(
                onClick = onFavouriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .zIndex(1f) // чтобы быть поверх изображения
            ) {
                Image(
                    painter = painterResource(
                        if (animal.isFavourite) {
                            R.drawable.favourite
                        } else {
                            R.drawable.favourite_border
                        }
                    ),
                    contentDescription = "Favorite icon"
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = animal.name,
            color = Color.Black,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(5.dp))

        Text(
            text = animal.description,
            color = Color.Gray,
            fontSize = 16.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(5.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier
                    .weight(1f),
                text = animal.age,
                color = Color.Blue,
                fontSize = 18.sp
            )

            if (showEditButton) {
                IconButton(onClick = { onEditClick(animal) }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
            }
        }
    }
}