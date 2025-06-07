package com.pixelrabbit.animalshelterfirebase.ui.authorization

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.pixelrabbit.animalshelterfirebase.data.MainScreenDataObject
import com.pixelrabbit.animalshelterfirebase.utils.ButtonBlue
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundGray
import com.google.firebase.auth.FirebaseAuth

@Composable
fun LoginScreen(
    auth: FirebaseAuth,
    prefs: SharedPreferences,
    onNavigateToMainScreen: (MainScreenDataObject) -> Unit,
    onBackClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        email = prefs.getString("email", "") ?: ""
        password = prefs.getString("password", "") ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        // Верхняя панель с заголовком и кнопкой "назад"
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
        ) {
            TextButton(
                onClick = onBackClick,
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Text(
                    modifier = Modifier,
                    fontSize = 16.sp,
                    fontFamily = AnimalFont,
                    text = "Назад",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text(
                fontSize = 20.sp,
                fontFamily = AnimalFont,
                text = "Авторизация",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Center),
                fontWeight = FontWeight.Bold

            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Электронная почта") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = BackgroundGray,
                    unfocusedContainerColor = BackgroundGray,
                    disabledContainerColor = Color.LightGray,
                    errorContainerColor = Color.LightGray,
                    // Другие цвета, если необходимо
                    focusedIndicatorColor = Color.Transparent, // Чтобы убрать цвет индикатора фокуса
                    unfocusedIndicatorColor = Color.Transparent, // Чтобы убрать цвет индикатора не в фокусе
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )

            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = BackgroundGray,
                    unfocusedContainerColor = BackgroundGray,
                    disabledContainerColor = Color.LightGray,
                    errorContainerColor = Color.LightGray,
                    // Другие цвета, если необходимо
                    focusedIndicatorColor = Color.Transparent, // Чтобы убрать цвет индикатора фокуса
                    unfocusedIndicatorColor = Color.Transparent, // Чтобы убрать цвет индикатора не в фокусе
                    disabledIndicatorColor = Color.Transparent,
                    errorIndicatorColor = Color.Transparent
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            ButtonBlue(
                text = "Войти",
                onClick = {
                    if (email.isBlank() || password.isBlank()) {
                        errorMessage = "Пожалуйста, заполните все поля."
                    } else {
                        errorMessage = null // Очистим предыдущее сообщение об ошибке
                        signIn(
                            auth = auth,
                            email = email,
                            password = password,
                            onSignInSuccess = { userData ->
                                with(prefs.edit()) {
                                    putString("email", email)
                                    putString("password", password)
                                    apply()
                                }
                                onNavigateToMainScreen(userData)
                            },
                            onSignInFailure = { error ->
                                errorMessage = error
                            }
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )


            errorMessage?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}


fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignInSuccess: (MainScreenDataObject) -> Unit,
    onSignInFailure: (String) -> Unit
) {
    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val user = auth.currentUser
                if (user != null) {
                    onSignInSuccess(
                        MainScreenDataObject(
                            uid = user.uid,
                            email = user.email ?: ""
                        )
                    )
                } else {
                    onSignInFailure("Ошибка: пользователь не найден.")
                }
            } else {
                onSignInFailure(task.exception?.message ?: "Ошибка входа")
            }
        }
}

fun createEncryptedPrefs(context: Context): SharedPreferences {
    val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    return EncryptedSharedPreferences.create(
        "secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
}
