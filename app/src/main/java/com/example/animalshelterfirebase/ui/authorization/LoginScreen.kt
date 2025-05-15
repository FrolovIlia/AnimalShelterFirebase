package com.example.animalshelterfirebase.ui.authorization

import android.content.SharedPreferences
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.animalshelterfirebase.data.MainScreenDataObject
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Авторизация", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Электронная почта") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
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
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Войти")
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        TextButton(onClick = onBackClick) {
            Text("Назад")
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
