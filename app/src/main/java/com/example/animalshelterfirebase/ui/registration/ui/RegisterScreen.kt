package com.example.animalshelterfirebase.ui.registration.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.animalshelterfirebase.data.MainScreenDataObject
import com.google.firebase.auth.FirebaseAuth
import android.util.Patterns
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import java.text.SimpleDateFormat
import java.util.Locale
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape

import androidx.compose.material3.*
import androidx.compose.runtime.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.animalshelterfirebase.ui.login.ButtonBlue
import com.example.animalshelterfirebase.ui.theme.AnimalFont
import com.example.animalshelterfirebase.ui.theme.BackgroundGray
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

@Composable
fun RegisterScreen(
    auth: FirebaseAuth,
    onRegistered: (MainScreenDataObject) -> Unit,
    onBack: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var birthDate by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    var emailError by remember { mutableStateOf(false) }
    var birthDateError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // Верхняя панель
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    text = "Назад",
                    fontSize = 16.sp,
                    fontFamily = AnimalFont,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                text = "Регистрация",
                fontSize = 20.sp,
                fontFamily = AnimalFont,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            fun modifierField() = Modifier
                .fillMaxWidth()

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
                colors = fieldColors()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = birthDate,
                onValueChange = {
                    birthDate = it
                    birthDateError = !isValidDate(it)
                },
                label = { Text("Дата рождения (дд.мм.гггг)") },
                isError = birthDateError,
                modifier = modifierField(),
                shape = RoundedCornerShape(10.dp),
                colors = fieldColors()
            )
            if (birthDateError) {
                Text("Некорректный формат даты", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = {
                    phone = it
                    phoneError = !isPhoneValid(it)
                },
                label = { Text("Телефон (+7XXXXXXXXXX)") },
                isError = phoneError,
                modifier = modifierField(),
                shape = RoundedCornerShape(10.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                colors = fieldColors()
            )
            if (phoneError) {
                Text("Введите корректный номер телефона, начиная с +7", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = !isValidEmail(it)
                },
                label = { Text("Электронная почта") },
                isError = emailError,
                modifier = modifierField(),
                shape = RoundedCornerShape(10.dp),
                colors = fieldColors()
            )
            if (emailError) {
                Text("Некорректный формат email", color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = modifierField(),
                shape = RoundedCornerShape(10.dp),
                colors = fieldColors()
            )

            error?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            ButtonBlue(
                text = "Зарегистрироваться",
                onClick = {
                    signUp(
                        auth,
                        email,
                        password,
                        name,
                        birthDate,
                        phone,
                        onSignUpSuccess = onRegistered,
                        onSignUpFailure = { error = it }
                    )
                },
                modifier = modifierField()
            )
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
                if (user != null) {
                    // Сохраняем расширенные данные пользователя в Firebase Realtime Database или Firestore
                    val userData = mapOf(
                        "name" to name,
                        "birthDate" to birthDate,
                        "phone" to phone,
                        "email" to email
                    )

                    // Пример для Firestore:
                    Firebase.firestore.collection("users")
                        .document(user.uid)
                        .set(userData)
                        .addOnSuccessListener {
                            onSignUpSuccess(MainScreenDataObject(uid = user.uid, email = email))
                        }
                        .addOnFailureListener { e ->
                            onSignUpFailure("Ошибка при сохранении данных: ${e.message}")
                        }

                } else {
                    onSignUpFailure("Ошибка: пользователь не найден после регистрации.")
                }
            } else {
                onSignUpFailure(task.exception?.message ?: "Ошибка регистрации")
            }
        }
}

fun isValidEmail(email: String): Boolean {
    return Patterns.EMAIL_ADDRESS.matcher(email).matches()
}


fun isValidDate(date: String): Boolean {
    return try {
        val sdf = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        sdf.isLenient = false
        sdf.parse(date)
        true
    } catch (e: Exception) {
        false
    }
}

fun isPhoneValid(phone: String): Boolean {
    // Проверка на формат +7 и 11 цифр всего
    val regex = Regex("^\\+7\\d{10}\$")
    return regex.matches(phone)
}