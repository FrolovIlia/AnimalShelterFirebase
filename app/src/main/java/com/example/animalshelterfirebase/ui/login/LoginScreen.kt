package com.example.animalshelterfirebase.ui.login

import android.content.Context
import androidx.compose.ui.platform.LocalContext

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animalshelterfirebase.R
import com.example.animalshelterfirebase.data.MainScreenDataObject
import com.example.animalshelterfirebase.ui.theme.AnimalFont

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import com.google.accompanist.systemuicontroller.rememberSystemUiController


@Composable
fun LoginScreen(
    onNavigateToMainScreen: (MainScreenDataObject) -> Unit
) {
    val auth = remember { Firebase.auth }
    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE) }


    val errorState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf(prefs.getString("email", "") ?: "") }
    val passwordState = remember { mutableStateOf(prefs.getString("password", "") ?: "") }

    // Управление статус баром
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Transparent) // Прозрачный статус бар

    Box(modifier = Modifier.fillMaxSize()) {
        // Фон
        Image(
            painter = painterResource(id = R.drawable.unsplash_img),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.3f))
        )

        // ЛОГО + ЗАГОЛОВОК (в линию, по центру, сверху)
        Row(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 100.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "logo",
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = "Майский \nДень",
                color = Color.Black,
                fontFamily = AnimalFont,
                fontSize = 32.sp
            )
        }

        // Форма и кнопки внизу, с отступами
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 16.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                text = "Счастье ближе чем ты думаешь!",
                color = Color.Black,
                fontFamily = AnimalFont,
                fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            RoundedCornerTextField(
                text = emailState.value,
                label = "Email"
            ) { emailState.value = it }

            Spacer(modifier = Modifier.height(10.dp))

            RoundedCornerTextField(
                text = passwordState.value,
                label = "Пароль"
            ) { passwordState.value = it }

            Spacer(modifier = Modifier.height(10.dp))

            if (errorState.value.isNotEmpty()) {
                Text(
                    text = errorState.value,
                    color = Color.Red,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            ButtonBlue(
                text = "Войти",
                modifier = Modifier.fillMaxWidth(1f)
            ) {
                signIn(
                    auth,
                    emailState.value,
                    passwordState.value,
                    onSignInSuccess = { navData ->
                        // Сохраняем email и пароль
                        with(prefs.edit()) {
                            putString("email", emailState.value)
                            putString("password", passwordState.value)
                            apply()
                        }
                        onNavigateToMainScreen(navData)
                    },
                    onSignInFailure = { errorState.value = it }
                )
            }


            Spacer(modifier = Modifier.height(8.dp))

            ButtonWhite(
                text = "Зарегистрироваться",
                modifier = Modifier.fillMaxWidth(1f)
            ) {
                signUp(
                    auth,
                    emailState.value,
                    passwordState.value,
                    onSignUpSuccess = { navData -> onNavigateToMainScreen(navData) },
                    onSignUpFailure = { errorState.value = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            ButtonTransparent(
                text = "Открыть без регистрации",
                modifier = Modifier.fillMaxWidth(1f)
            ) {
                // Переход без регистрации, передаём заглушку или пустые данные
                onNavigateToMainScreen(
                    MainScreenDataObject(
                        uid = "guest",
                        email = "guest@anonymous.com"
                    )
                )
            }
        }
    }
}


// Firebase auth methods (не менялись)
fun signUp(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignUpSuccess: (MainScreenDataObject) -> Unit,
    onSignUpFailure: (String) -> Unit,
) {
    if (email.isBlank() || password.isBlank()) {
        onSignUpFailure("Email и пароль не могут быть пустыми")
        return
    }

    auth.createUserWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSignUpSuccess(
                    MainScreenDataObject(
                        task.result.user?.uid!!,
                        task.result.user?.email!!
                    )
                )
            }
        }
        .addOnFailureListener {
            onSignUpFailure(it.message ?: "Sign Up Error")
        }
}

fun signIn(
    auth: FirebaseAuth,
    email: String,
    password: String,
    onSignInSuccess: (MainScreenDataObject) -> Unit,
    onSignInFailure: (String) -> Unit,
) {
    if (email.isBlank() || password.isBlank()) {
        onSignInFailure("Email и пароль не могут быть пустыми")
        return
    }

    auth.signInWithEmailAndPassword(email, password)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSignInSuccess(
                    MainScreenDataObject(
                        task.result.user?.uid!!,
                        task.result.user?.email!!
                    )
                )
            }
        }
        .addOnFailureListener {
            onSignInFailure(it.message ?: "Sign In Error")
        }
}
