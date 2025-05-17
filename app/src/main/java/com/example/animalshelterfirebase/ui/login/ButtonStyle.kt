package com.example.animalshelterfirebase.ui.login

import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animalshelterfirebase.ui.theme.AnimalFont
import com.example.animalshelterfirebase.ui.theme.BackgroundSecondary

import com.example.animalshelterfirebase.ui.theme.BackgroundWhite
import com.example.animalshelterfirebase.ui.theme.ButtonColorBlue
import com.example.animalshelterfirebase.ui.theme.ButtonColorWhite


@Composable
fun ButtonBlue(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = if (isPressed) BackgroundWhite else ButtonColorBlue
    val shape = RoundedCornerShape(30.dp)

    Button(
        onClick = onClick,
        modifier = modifier.height(52.dp),
        shape = shape,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.Black
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = if (isPressed) BackgroundWhite else ButtonColorWhite

    val shape = RoundedCornerShape(30.dp) // или любой другой радиус

    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .border(1.dp, BackgroundSecondary, shape) // теперь рамка повторяет форму
            .clip(shape), // опционально, для предотвращения "выпадения" содержимого за края
        shape = shape,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.Black
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
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = if (isPressed) BackgroundWhite else Color.Transparent
    val shape = RoundedCornerShape(30.dp)

    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp),
        shape = shape,
        interactionSource = interactionSource,
        colors = ButtonDefaults.buttonColors(
            containerColor = backgroundColor,
            contentColor = Color.Black
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
