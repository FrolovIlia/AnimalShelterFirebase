package com.pixelrabbit.animalshelterfirebase.ui.profile_screen

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pixelrabbit.animalshelterfirebase.ui.authorization.UserViewModel
import com.pixelrabbit.animalshelterfirebase.ui.authorization.createEncryptedPrefs
import com.pixelrabbit.animalshelterfirebase.ui.registration.isPhoneValid
import com.pixelrabbit.animalshelterfirebase.ui.registration.isValidDate
import com.pixelrabbit.animalshelterfirebase.ui.registration.isValidEmail
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundGray
import com.pixelrabbit.animalshelterfirebase.utils.ButtonBlue

@Composable
fun EditProfileScreen(
    userViewModel: UserViewModel,
    onProfileUpdated: () -> Unit = {},
    onBack: () -> Unit,
    onLogout: () -> Unit
) {
    val db = Firebase.firestore
    val auth = Firebase.auth
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val user by userViewModel.currentUser.collectAsState()
    val currentUserUid = auth.currentUser?.uid

    LaunchedEffect(currentUserUid) {
        if (currentUserUid != null) {
            userViewModel.loadUser(currentUserUid)
        }
    }

    var name by remember { mutableStateOf(user?.name ?: "") }
    var birthDateField by remember {
        mutableStateOf(
            TextFieldValue(user?.birthDate ?: "", TextRange((user?.birthDate ?: "").length))
        )
    }
    var phone by remember { mutableStateOf(if (user?.phone.isNullOrBlank()) "+7" else user!!.phone) }
    var email by remember { mutableStateOf(user?.email ?: "") }
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
                .height(56.dp)
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

        // Имя
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

        // Дата рождения
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

        // Телефон
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

        // Email
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

        // Пароль
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Новый пароль (необязательно)") },
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

        if (password.isNotBlank()) {
            Text(
                "Если долго не входили, может потребоваться повторная авторизация для смены пароля.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

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

                val currentUser = auth.currentUser
                if (currentUser == null) {
                    error = "Пользователь не авторизован"
                    isLoading = false
                    return@ButtonBlue
                }

                fun updateFirestoreAndPassword() {
                    val updatedData = mapOf(
                        "name" to name,
                        "birthDate" to birthDate,
                        "phone" to phone,
                        "email" to email
                    )

                    db.collection("users").document(user!!.uid)
                        .update(updatedData)
                        .addOnSuccessListener {
                            if (password.isNotBlank()) {
                                currentUser.updatePassword(password)
                                    .addOnSuccessListener {
                                        isLoading = false
                                        userViewModel.loadUser(user!!.uid) // was refreshUser()
                                        Toast.makeText(context, "Данные успешно обновлены", Toast.LENGTH_SHORT).show()
                                        onProfileUpdated()
                                        onBack()
                                    }
                                    .addOnFailureListener { e ->
                                        isLoading = false
                                        error = "Ошибка при обновлении пароля: ${e.message}"
                                    }
                            } else {
                                isLoading = false
                                userViewModel.loadUser(user!!.uid) // was refreshUser()
                                Toast.makeText(context, "Данные успешно обновлены", Toast.LENGTH_SHORT).show()
                                onProfileUpdated()
                                onBack()
                            }
                        }
                        .addOnFailureListener { e ->
                            isLoading = false
                            error = "Ошибка обновления: ${e.message}"
                        }
                }

                if (email != user!!.email) {
                    currentUser.updateEmail(email)
                        .addOnSuccessListener { updateFirestoreAndPassword() }
                        .addOnFailureListener { e ->
                            isLoading = false
                            error = if (e is FirebaseAuthRecentLoginRequiredException) {
                                "Для обновления email требуется повторная авторизация. Пожалуйста, войдите заново."
                            } else {
                                "Ошибка обновления email: ${e.message}"
                            }
                        }
                } else {
                    updateFirestoreAndPassword()
                }
            },
            modifier = fieldModifier()
        )

        Spacer(Modifier.height(16.dp))

        ButtonBlue(
            text = "Выйти из профиля",
            onClick = {
                Firebase.auth.signOut()
                val prefs = createEncryptedPrefs(context)
                prefs.edit().clear().apply()

                onLogout()
            },
            modifier = fieldModifier()
        )


        if (isLoading) {
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}