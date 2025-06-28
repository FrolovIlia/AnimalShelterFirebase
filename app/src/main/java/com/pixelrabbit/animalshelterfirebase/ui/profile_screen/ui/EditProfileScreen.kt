package com.pixelrabbit.animalshelterfirebase.ui.profile_screen.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pixelrabbit.animalshelterfirebase.data.UserObject
import com.pixelrabbit.animalshelterfirebase.ui.registration.ui.isPhoneValid
import com.pixelrabbit.animalshelterfirebase.ui.registration.ui.isValidDate
import com.pixelrabbit.animalshelterfirebase.ui.registration.ui.isValidEmail
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundGray
import com.pixelrabbit.animalshelterfirebase.utils.ButtonBlue

@Composable
fun EditProfileScreen(
    user: UserObject,
    onProfileUpdated: () -> Unit = {},
    onBack: () -> Unit
) {
    val db = Firebase.firestore
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf(user.name) }
    var birthDateField by remember {
        mutableStateOf(
            TextFieldValue(
                user.birthDate ?: "",
                selection = TextRange((user.birthDate ?: "").length)
            )
        )
    }
    var phone by remember { mutableStateOf(if (user.phone.isBlank()) "+7" else user.phone) }
    var email by remember { mutableStateOf(user.email) }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var birthDateError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    fun fieldModifier() = Modifier.fillMaxWidth()

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
                "Профиль",
                fontSize = 20.sp,
                fontFamily = AnimalFont,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            modifier = fieldModifier(),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = fieldColors()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = birthDateField,
            onValueChange = { input ->
                val digits = input.text.filter { it.isDigit() }.take(8)
                var formatted = ""
                var cursorPos = input.selection.start

                for (i in digits.indices) {
                    formatted += digits[i]
                    if ((i == 1 || i == 3) && i != digits.lastIndex) {
                        formatted += "."
                        if (cursorPos > i + 1) cursorPos++
                    }
                }

                birthDateField = TextFieldValue(
                    text = formatted,
                    selection = TextRange(formatted.length.coerceAtMost(cursorPos))
                )
                birthDateError = formatted.length != 10 || !isValidDate(formatted)
            },
            label = { Text("Дата рождения (ДД.ММ.ГГГГ)") },
            isError = birthDateError,
            modifier = fieldModifier(),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = fieldColors()
        )

        if (birthDateError) {
            Text("Неверный формат даты", color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = if (it.isEmpty()) "+7"
                else if (!it.startsWith("+7")) "+7" + it.filter { ch -> ch.isDigit() }
                else it
                phoneError = !isPhoneValid(phone)
            },
            label = { Text("Телефон") },
            isError = phoneError,
            modifier = fieldModifier(),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = fieldColors()
        )

        if (phoneError) {
            Text("Неверный номер телефона", color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = !isValidEmail(it)
            },
            label = { Text("Email") },
            isError = emailError,
            modifier = fieldModifier(),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            colors = fieldColors()
        )

        if (emailError) {
            Text("Некорректный email", color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль (необязательно)") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            modifier = fieldModifier(),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            colors = fieldColors()
        )

        error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))

        ButtonBlue(
            text = if (isLoading) "Обновление..." else "Обновить данные",
            onClick = {
                focusManager.clearFocus()
                error = null
                val birthDate = birthDateField.text

                emailError = !isValidEmail(email)
                phoneError = !isPhoneValid(phone)
                birthDateError = !isValidDate(birthDate)

                if (name.isBlank() || emailError || phoneError || birthDateError) {
                    error = "Пожалуйста, заполните все поля корректно"
                    return@ButtonBlue
                }

                isLoading = true
                val updatedData = mutableMapOf<String, Any>(
                    "name" to name,
                    "birthDate" to birthDate,
                    "phone" to phone,
                    "email" to email
                )

                // Если введён пароль, обновим через FirebaseAuth (нужно будет реализовать)
                val docRef = db.collection("users").document(user.uid)
                docRef.update(updatedData)
                    .addOnSuccessListener {
                        isLoading = false
                        onProfileUpdated()
                        onBack()
                    }
                    .addOnFailureListener {
                        isLoading = false
                        error = "Ошибка обновления: ${it.message}"
                    }
            },
            modifier = fieldModifier()
        )

        if (isLoading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

