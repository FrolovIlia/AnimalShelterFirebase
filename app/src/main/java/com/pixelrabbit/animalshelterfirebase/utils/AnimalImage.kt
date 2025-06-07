package com.pixelrabbit.animalshelterfirebase.utils

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.pixelrabbit.animalshelterfirebase.R

@Composable
fun AnimalImage(
    imageUri: Uri?,
    imageUrl: String?,
    modifier: Modifier = Modifier
) {
    val defaultImage = painterResource(id = R.drawable.default_animal_image)

    val painter = when {
        imageUri != null -> rememberAsyncImagePainter(
            model = imageUri,
            placeholder = defaultImage,
            error = defaultImage
        )
        !imageUrl.isNullOrEmpty() -> rememberAsyncImagePainter(
            model = imageUrl,
            placeholder = defaultImage,
            error = defaultImage
        )
        else -> defaultImage
    }

    // Вынесем padding наружу, чтобы Image занимал правильный размер
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
    ) {
        Image(
            painter = painter,
            contentDescription = "Animal image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}


