package com.example.animalshelterfirebase.ui.login

import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animalshelterfirebase.ui.theme.AnimalFont
import com.example.animalshelterfirebase.ui.theme.ButtonColorBlue
import com.example.animalshelterfirebase.ui.theme.ButtonColorWhite


@Composable
fun ButtonBlue(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp), // без fillMaxWidth
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonColorBlue
        )
    ) {
        Text(
            text = text,
            color = Color.Black,
            fontFamily = AnimalFont,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 36.sp
            )
        )
    }
}

@Composable
fun ButtonWhite(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp), // без fillMaxWidth
        colors = ButtonDefaults.buttonColors(
            containerColor = ButtonColorWhite
        )
    ) {
        Text(
            text = text,
            color = Color.Black,
            fontFamily = AnimalFont,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 36.sp
            )
        )
    }
}

@Composable
fun ButtonTransparent(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Button(
        onClick = {
            onClick()
        },
        modifier = modifier.height(52.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        )

    ) {
        Text(
            text = text,
            color = Color.Black,
            fontFamily = AnimalFont,
            style = TextStyle(
                fontSize = 16.sp,
                lineHeight = 36.sp
            )
        )
    }
}