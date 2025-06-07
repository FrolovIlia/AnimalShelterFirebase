package com.pixelrabbit.animalshelterfirebase.utils

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.pixelrabbit.animalshelterfirebase.ui.theme.ButtonColorBlue

@Composable
fun PhoneNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    label: String = "Номер куратора",
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember {
        mutableStateOf(TextFieldValue(text = value, selection = TextRange(value.length)))
    }

    TextField(
        value = textFieldValue,
        onValueChange = { newValue ->
            val input = newValue.text
            val digitsOnly = input.filter { it.isDigit() }

            val newText = when {
                input.startsWith("+7") -> "+7" + digitsOnly.drop(1).take(10)
                input.startsWith("7") -> "+7" + digitsOnly.drop(1).take(10)
                input.startsWith("8") -> "+7" + digitsOnly.drop(1).take(10)
                else -> "+7" + digitsOnly.take(10)
            }

            val cursorPosition = newText.length.coerceAtMost(12)  // курсор в конце
            textFieldValue = TextFieldValue(text = newText, selection = TextRange(cursorPosition))
            onValueChange(newText)
        },
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focusState ->
                if (focusState.isFocused && value.isEmpty()) {
                    val initial = "+7"
                    textFieldValue = TextFieldValue(text = initial, selection = TextRange(initial.length))
                    onValueChange(initial)
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
