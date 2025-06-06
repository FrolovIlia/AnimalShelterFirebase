package com.example.animalshelterfirebase.ui.adoption_screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animalshelterfirebase.data.Animal
import com.example.animalshelterfirebase.data.UserObject
import com.example.animalshelterfirebase.ui.theme.AnimalFont
import com.example.animalshelterfirebase.ui.theme.BackgroundGray
import com.example.animalshelterfirebase.utils.ButtonBlue

@Composable
fun AdoptionScreen(
    animal: Animal,
    user: UserObject,
    onBack: () -> Unit,
    onSubmitSuccess: () -> Unit
) {
    var text by remember { mutableStateOf(TextFieldValue("")) }
    var error by remember { mutableStateOf<String?>(null) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val curatorPhoneNumber = if (animal.curatorPhone.isNullOrBlank()) "+79303537553" else animal.curatorPhone!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text("Назад", fontSize = 16.sp, fontFamily = AnimalFont)
            }
            Text(
                "Заявка",
                fontSize = 20.sp,
                fontFamily = AnimalFont,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(Modifier.height(24.dp))

        fun modifierField() = Modifier.fillMaxWidth()

        @Composable
        fun fieldColors() = TextFieldDefaults.colors(
            focusedContainerColor = BackgroundGray,
            unfocusedContainerColor = BackgroundGray,
            disabledContainerColor = Color.LightGray,
            errorContainerColor = Color.LightGray,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent
        )

        OutlinedTextField(
            value = text,
            onValueChange = { newText ->
                text = newText
                error = if (newText.text.length < 10) {
                    "Пожалуйста, опишите опыт подробнее"
                } else {
                    null
                }
            },
            label = { Text("Расскажите о вашем опыте") },
            placeholder = {
                Text(
                    "Опишите ваш опыт с животными, " +
                            "наличие других питомцев и условия проживания"
                )
            },
            isError = error != null,
            minLines = 6,
            modifier = modifierField(),
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() })
        )

        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(Modifier.height(16.dp))

        ButtonBlue(
            text = "Отправить заявку",
            onClick = {
                if (text.text.length < 10) {
                    error = "Пожалуйста, опишите опыт подробнее"
                } else {
                    val smsMessage = listOf(
                        "📢 Заявка на усыновление",
                        "",
                        "👤 Пользователь:",
                        "Имя: ${user.name ?: "не указано"}",
                        "Телефон: ${user.phone ?: "не указано"}",
                        "Email: ${user.email ?: "не указано"}",
                        "",
                        "🐾 Животное:",
                        "Имя: ${animal.name}",
                        "Возраст: ${animal.age}",
                        "Особенности: ${animal.feature ?: "не указано"}",
                        "Расположение: ${animal.location}",
                        "",
                        "📝 Опыт:",
                        text.text.trim()
                    ).joinToString("\n")

                    val smsUri = Uri.parse("smsto:$curatorPhoneNumber")
                    val intent = Intent(Intent.ACTION_SENDTO, smsUri).apply {
                        putExtra("sms_body", smsMessage)
                    }
                    context.startActivity(intent)

                    onSubmitSuccess()
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
