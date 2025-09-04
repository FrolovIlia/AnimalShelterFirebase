package com.pixelrabbit.animalshelterfirebase.ui.profile_screen

import android.widget.Toast
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.text.style.TextAlign
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
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundSecondary
import com.pixelrabbit.animalshelterfirebase.ui.theme.ButtonColorWhite
import com.pixelrabbit.animalshelterfirebase.ui.theme.TextSecondary
import com.pixelrabbit.animalshelterfirebase.utils.ButtonBlue
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    userViewModel: UserViewModel,
    onProfileUpdated: () -> Unit = {},
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onOwnerClick: () -> Unit
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

    // Состояния полей, инициализируем данными из ViewModel
    var name by remember(user) { mutableStateOf(user?.name ?: "") }
    var birthDateField by remember(user) {
        mutableStateOf(
            TextFieldValue(user?.birthDate ?: "", TextRange((user?.birthDate ?: "").length))
        )
    }
    var phone by remember(user) {
        mutableStateOf(user?.phone?.ifBlank { "+7" } ?: "+7")
    }
    var email by remember(user) { mutableStateOf(user?.email ?: "") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var error by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    var birthDateError by remember { mutableStateOf(false) }
    var phoneError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }

    val fieldModifier = Modifier.fillMaxWidth()

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

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Профиль",
                        fontSize = 20.sp,
                        fontFamily = AnimalFont
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text("Назад", fontSize = 16.sp, fontFamily = AnimalFont)
                    }
                },
                actions = {
                    val localUser = user
                    if (localUser?.isOwner == true) {
                        val shape = RoundedCornerShape(30.dp)
                        Card(
                            modifier = Modifier
                                .width(105.dp)
                                .height(52.dp)
                                .border(1.dp, BackgroundSecondary, shape)
                                .clip(shape)
                                .clickable { onOwnerClick() },
                            shape = shape,
                            colors = CardDefaults.cardColors(containerColor = ButtonColorWhite),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .padding(horizontal = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Список\nюзеров",
                                    fontFamily = AnimalFont,
                                    fontSize = 13.sp,
                                    color = TextSecondary,
                                    maxLines = 2,
                                    softWrap = true,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BackgroundGray
                )
            )
        }

    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {

            Spacer(Modifier.height(24.dp))

            // Имя
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Имя") },
                modifier = fieldModifier,
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
                modifier = fieldModifier,
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
                modifier = fieldModifier,
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
                modifier = fieldModifier,
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
                modifier = fieldModifier,
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
                    val localUser = user
                    if (currentUser == null || localUser == null) {
                        error = "Пользователь не авторизован или данные не загружены"
                        isLoading = false
                        return@ButtonBlue
                    }

                    fun updatePasswordAndFirestore() {
                        val updatedData = mapOf(
                            "name" to name,
                            "birthDate" to birthDate,
                            "phone" to phone,
                            "email" to email
                        )

                        db.collection("users").document(localUser.uid)
                            .update(updatedData)
                            .addOnSuccessListener {
                                isLoading = false
                                userViewModel.loadUser(localUser.uid)
                                Toast.makeText(context, "Данные успешно обновлены", Toast.LENGTH_SHORT).show()
                                onProfileUpdated()
                                onBack()
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                error = "Ошибка обновления: ${e.message}"
                            }
                    }

                    if (email != localUser.email) {
                        currentUser.updateEmail(email)
                            .addOnSuccessListener {
                                if (password.isNotBlank()) {
                                    currentUser.updatePassword(password)
                                        .addOnSuccessListener { updatePasswordAndFirestore() }
                                        .addOnFailureListener { e ->
                                            isLoading = false
                                            error = "Ошибка при обновлении пароля: ${e.message}"
                                        }
                                } else {
                                    updatePasswordAndFirestore()
                                }
                            }
                            .addOnFailureListener { e ->
                                isLoading = false
                                error = if (e is FirebaseAuthRecentLoginRequiredException) {
                                    "Для обновления email требуется повторная авторизация. Пожалуйста, войдите заново."
                                } else {
                                    "Ошибка обновления email: ${e.message}"
                                }
                            }
                    } else {
                        if (password.isNotBlank()) {
                            currentUser.updatePassword(password)
                                .addOnSuccessListener { updatePasswordAndFirestore() }
                                .addOnFailureListener { e ->
                                    isLoading = false
                                    error = "Ошибка при обновлении пароля: ${e.message}"
                                }
                        } else {
                            updatePasswordAndFirestore()
                        }
                    }
                },
                modifier = fieldModifier
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
                modifier = fieldModifier
            )
        }
    }
}