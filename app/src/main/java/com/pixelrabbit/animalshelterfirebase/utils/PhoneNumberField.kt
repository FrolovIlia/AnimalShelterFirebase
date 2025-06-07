package com.pixelrabbit.animalshelterfirebase.utils

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.pixelrabbit.animalshelterfirebase.ui.theme.ButtonColorBlue
import androidx.compose.ui.text.input.KeyboardType

@Composable
fun PhoneNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    label: String = "Номер куратора",  // добавлен параметр label с дефолтным значением
    modifier: Modifier = Modifier
) {
    val isFocused = remember { mutableStateOf(false) }

    TextField(
        value = value,
        onValueChange = { input ->
            // Удаляем всё, кроме цифр
            val digitsOnly = input.filter { it.isDigit() }

            // Если ввод начался без +7, добавляем
            val newValue = when {
                input.startsWith("+7") -> "+7" + digitsOnly.drop(1).take(10)
                input.startsWith("7") -> "+7" + digitsOnly.drop(1).take(10)
                else -> "+7" + digitsOnly.take(10)
            }

            // Не навязываем +7 если пользователь полностью всё удалил
            if (input.isEmpty()) {
                onValueChange("")
            } else {
                onValueChange(newValue)
            }
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                isFocused.value = focusState.isFocused
                if (focusState.isFocused && value.isEmpty()) {
                    onValueChange("+7")
                }
            }
            .border(1.dp, if (isError) Color.Red else ButtonColorBlue, RoundedCornerShape(30.dp)),
        label = {
            Text(text = label, color = Color.Gray)
        },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(25.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions.Default.copy(
            keyboardType = KeyboardType.Phone
        )
    )
}
