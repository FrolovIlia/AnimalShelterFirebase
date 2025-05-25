package com.example.animalshelterfirebase.ui.registration.ui

import android.util.Patterns
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.animalshelterfirebase.data.MainScreenDataObject
import com.example.animalshelterfirebase.utils.ButtonBlue
import com.example.animalshelterfirebase.ui.theme.AnimalFont
import com.example.animalshelterfirebase.ui.theme.BackgroundGray
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.TextFieldValue

@Composable
fun RegisterScreen(
    auth: FirebaseAuth,
    onRegistered: (MainScreenDataObject) -> Unit,
    onBack: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var touched by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf(false) }
    var birthDateError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var birthDateField by remember { mutableStateOf(TextFieldValue("")) }


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
                "Регистрация",
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
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            modifier = modifierField(),
            shape = RoundedCornerShape(10.dp),
            colors = fieldColors(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) })
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

                val newTextFieldValue = TextFieldValue(
                    text = formatted,
                    selection = androidx.compose.ui.text.TextRange(
                        formatted.length.coerceAtMost(
                            cursorPos
                        )
                    )
                )

                birthDateField = newTextFieldValue

                // Валидация: ровно 10 символов и корректная дата
                birthDateError = formatted.length != 10 || !isValidDate(formatted)
            },
            label = { Text("Дата рождения (ДД.ММ.ГГГГ)") },
            isError = birthDateError,
            modifier = modifierField(),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }
            ),
            colors = fieldColors()
        )
        if (birthDateError) Text("Неверный формат даты", color = MaterialTheme.colorScheme.error)

        Spacer(Modifier.height(8.dp))

        var phone by remember { mutableStateOf("+7") }

        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = if (it.isEmpty()) "+7"
                else if (!it.startsWith("+7")) "+7" + it.filter { ch -> ch.isDigit() }
                else it
                phoneError = !isPhoneValid(phone)
            },
            label = { Text("Телефон (+7XXXXXXXXXX)") },
            isError = phoneError,
            modifier = modifierField(),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }),
            colors = fieldColors()
        )

        if (phoneError) Text("Неверный номер телефона", color = MaterialTheme.colorScheme.error)

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = !isValidEmail(it)
            },
            label = { Text("Email") },
            isError = emailError,
            modifier = modifierField(),
            shape = RoundedCornerShape(10.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(androidx.compose.ui.focus.FocusDirection.Down) }),
            colors = fieldColors()
        )
        if (emailError) Text("Некорректный email", color = MaterialTheme.colorScheme.error)

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null
                    )
                }
            },
            modifier = modifierField(),
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
            text = if (isLoading) "Загрузка..." else "Зарегистрироваться",
            onClick = {
                touched = true

                // Обновляем birthDate перед проверкой
                birthDate = birthDateField.text

                emailError = !isValidEmail(email)
                phoneError = !isPhoneValid(phone)
                birthDateError = !isValidDate(birthDate)

                if (name.isBlank() || emailError || phoneError || birthDateError || password.isBlank()) {
                    error = "Пожалуйста, заполните все поля корректно"
                    return@ButtonBlue
                }

                isLoading = true
                signUp(
                    auth, email, password, name, birthDate, phone,
                    onSignUpSuccess = {
                        isLoading = false
                        onRegistered(it)
                    },
                    onSignUpFailure = {
                        isLoading = false
                        error = it
                    }
                )
            },
            modifier = modifierField()
        )

        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}

fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    name: String,
    birthDate: String,
    phone: String,
    onSignUpSuccess: (MainScreenDataObject) -> Unit,
    onSignUpFailure: (String) -> Unit
) {
    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                    ?: return@addOnCompleteListener onSignUpFailure("Ошибка: пользователь не найден")
                val userData = mapOf(
                    "name" to name,
                    "birthDate" to birthDate,
                    "phone" to phone,
                    "email" to email
                )
                Firebase.firestore.collection("users")
                    .document(user.uid)
                    .set(userData)
                    .addOnSuccessListener {
                        onSignUpSuccess(
                            MainScreenDataObject(
                                uid = user.uid,
                                email = email
                            )
                        )
                    }
                    .addOnFailureListener { e -> onSignUpFailure("Ошибка при сохранении данных: ${e.message}") }
            } else {
                onSignUpFailure(task.exception?.message ?: "Ошибка регистрации")
            }
        }
}

fun isValidEmail(email: String): Boolean = Patterns.EMAIL_ADDRESS.matcher(email).matches()

fun isValidDate(date: String): Boolean = try {
    val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
    sdf.isLenient = false
    sdf.parse(date)
    true
} catch (e: Exception) {
    false
}

fun isPhoneValid(phone: String): Boolean = Regex("^\\+7\\d{10}$").matches(phone)
