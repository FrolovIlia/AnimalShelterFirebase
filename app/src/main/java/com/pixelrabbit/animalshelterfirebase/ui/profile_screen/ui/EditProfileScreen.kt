package com.pixelrabbit.animalshelterfirebase.ui.profile_screen.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pixelrabbit.animalshelterfirebase.data.UserObject
import com.pixelrabbit.animalshelterfirebase.ui.registration.ui.isPhoneValid
import com.pixelrabbit.animalshelterfirebase.ui.registration.ui.isValidDate
import com.pixelrabbit.animalshelterfirebase.ui.registration.ui.isValidEmail
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
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
    var birthDateField by remember { mutableStateOf(TextFieldValue("")) } // загружается отдельно, если нужно
    var phone by remember { mutableStateOf(user.phone.ifBlank { "+7" }) }
    var email by remember { mutableStateOf(user.email) }

    var error by remember { mutableStateOf<String?>(null) }

    var birthDateError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("Редактирование профиля", fontSize = 20.sp, fontFamily = AnimalFont)

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth()
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
                        formatted.length.coerceAtMost(cursorPos)
                    )
                )

                birthDateField = newTextFieldValue
                birthDateError = formatted.length != 10 || !isValidDate(formatted)
            },
            label = { Text("Дата рождения (ДД.ММ.ГГГГ)") },
            isError = birthDateError,
            modifier = Modifier.fillMaxWidth()
        )

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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                emailError = !isValidEmail(it)
            },
            label = { Text("Email") },
            isError = emailError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
            Spacer(Modifier.height(8.dp))
        }

        ButtonBlue(
            text = "Сохранить",
            onClick = {
                val birthDate = birthDateField.text

                emailError = !isValidEmail(email)
                phoneError = !isPhoneValid(phone)
                birthDateError = !isValidDate(birthDate)

                if (name.isBlank() || emailError || phoneError || birthDateError) {
                    error = "Заполните все поля корректно"
                    return@ButtonBlue
                }

                val updatedData = mapOf(
                    "name" to name,
                    "birthDate" to birthDate,
                    "phone" to phone,
                    "email" to email,
                )

                db.collection("users").document(user.uid)
                    .update(updatedData)
                    .addOnSuccessListener {
                        onProfileUpdated()
                        onBack()
                    }
                    .addOnFailureListener { e -> error = "Ошибка обновления: ${e.message}" }
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
