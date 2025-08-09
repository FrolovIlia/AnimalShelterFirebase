package com.pixelrabbit.animalshelterfirebase.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixelrabbit.animalshelterfirebase.ui.theme.ButtonColorBlue

@Composable
fun RoundedCornerDropDownMenu(
    defValue: String,
    options: List<String>,
    placeholder: String = "Выберите значение",
    onOptionSelected: (String) -> Unit
) {
    val expanded = remember { mutableStateOf(false) }
    val selectedOption = remember { mutableStateOf(defValue) }

    val displayText = if (selectedOption.value.isBlank()) placeholder else selectedOption.value
    val displayColor = if (selectedOption.value.isBlank()) Color.Gray else Color.Black

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .border(1.dp, ButtonColorBlue, RoundedCornerShape(30.dp))
            .clip(RoundedCornerShape(30.dp))
            .background(Color.White)
            .clickable { expanded.value = true }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = displayText,
            color = displayColor
        )

        DropdownMenu(
            expanded = expanded.value,
            onDismissRequest = { expanded.value = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(text = option) },
                    onClick = {
                        selectedOption.value = option
                        expanded.value = false
                        onOptionSelected(option)
                    }
                )
            }
        }
    }
}
