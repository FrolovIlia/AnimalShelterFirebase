package com.pixelrabbit.animalshelterfirebase.ui.animal_list_item_UI

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.pixelrabbit.animalshelterfirebase.R
import com.pixelrabbit.animalshelterfirebase.data.model.Animal
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundWhite


@Composable
fun AnimalListItemUI(
    showEditButton: Boolean = false,
    animal: Animal,
    isFavourite: Boolean,
    onEditClick: (Animal) -> Unit = {},
    onFavouriteClick: () -> Unit = {},
    onAnimalClick: (Animal) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable{
                onAnimalClick(animal)
            }
            .clip(RoundedCornerShape(15.dp))
            .background(BackgroundWhite)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            if (animal.imageUrl.isNullOrBlank()) {
                Image(
                    painter = painterResource(id = R.drawable.default_animal_image), // заглушка
                    contentDescription = "Placeholder image",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(15.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                AsyncImage(
                    model = animal.imageUrl,
                    contentDescription = animal.name,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(15.dp)),
                    contentScale = ContentScale.Crop,
                    error = painterResource(id = R.drawable.default_animal_image), // если загрузка не удалась
                    placeholder = painterResource(id = R.drawable.default_animal_image) // placeholder во время загрузки
                )
            }

            IconButton(
                onClick = onFavouriteClick,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .size(24.dp)
                    .zIndex(1f)
            ) {
                Image(
                    painter = painterResource(
                        if (isFavourite) {
                            R.drawable.favourite
                        } else {
                            R.drawable.favourite_border
                        }
                    ),
                    contentDescription = "Favorite icon"
                )

            }

            if (showEditButton) {
                IconButton(
                    onClick = { onEditClick(animal) },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .size(24.dp)
                        .zIndex(1f)
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

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = animal.name,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = animal.age,
                    color = Color.LightGray,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = animal.feature,
                color = Color.Gray,
                fontSize = 14.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}