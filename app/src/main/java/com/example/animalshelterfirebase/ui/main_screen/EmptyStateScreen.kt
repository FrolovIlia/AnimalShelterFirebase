package com.example.animalshelterfirebase.ui.main_screen

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

import com.example.animalshelterfirebase.ui.theme.AnimalFont


@Composable
fun EmptyStateScreen(
    message: String = "Здесь пока никого нет"
) {
    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = message,
            color = Color.Black,
            fontFamily = AnimalFont,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 36.sp
            )
        )
    }
}