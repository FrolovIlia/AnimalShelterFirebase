package com.example.animalshelterfirebase.ui.login

import android.util.Log

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

    val errorState = remember { mutableStateOf("") }
    val emailState = remember { mutableStateOf("") }
    val passwordState = remember { mutableStateOf("") }

    // Управление статус баром
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(color = Color.Transparent) // Прозрачный статус бар

    Box(modifier = Modifier.fillMaxSize()) {
        // Фоновое изображение, растянутое на весь экран (включая статус бар)
        Image(
            painter = painterResource(id = R.drawable.unsplash_img), // Убедитесь, что изображение существует
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(), // Растягиваем изображение на весь экран
        )

        // Прозрачная подложка, которая накрывает изображение
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0f)) // полностью прозрачная подложка
        )

        // Основной контент
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp), // отступы по бокам
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Row(
                modifier = Modifier
                    .padding(top = 100.dp),
                verticalAlignment = Alignment.CenterVertically

            ) {
                // Логотип
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "logo",
                    modifier = Modifier.size(120.dp),

                    )

                // Заголовок
                Text(
                    text = "Майский День",
                    color = Color.Black,
                    fontFamily = AnimalFont,
                    fontSize = 32.sp
                )
            }

            Spacer(modifier = Modifier.height(250.dp))





            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp)
                    .padding(bottom = 30.dp), // отступы по бокам
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Text(
                    text = "Счастье ближе чем ты думаешь!",
                    color = Color.Black,
                    fontFamily = AnimalFont,
                    fontSize = 24.sp,

                )
                Spacer(modifier = Modifier.height(20.dp))

                // Поле ввода email
                RoundedCornerTextField(
                    text = emailState.value,
                    label = "Email"
                ) { emailState.value = it }

                Spacer(modifier = Modifier.height(10.dp))

                // Поле ввода пароля
                RoundedCornerTextField(
                    text = passwordState.value,
                    label = "Пароль"
                ) { passwordState.value = it }

                Spacer(modifier = Modifier.height(10.dp))

                // Ошибка при неправильном вводе
                if (errorState.value.isNotEmpty()) {
                    Text(
                        text = errorState.value,
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Кнопка "Войти"
                LoginButton(text = "Войти") {
                    signIn(
                        auth,
                        emailState.value,
                        passwordState.value,
                        onSignInSuccess = { navData ->
                            onNavigateToMainScreen(navData)
                            Log.d("MyLog", "Sign In Success")
                        },
                        onSignInFailure = { error ->
                            errorState.value = error
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Кнопка "Зарегистрироваться"
                LoginButton(text = "Зарегистрироваться") {
                    signUp(
                        auth,
                        emailState.value,
                        passwordState.value,
                        onSignUpSuccess = { navData ->
                            onNavigateToMainScreen(navData)
                            Log.d("MyLog", "Sign Up Success")
                        },
                        onSignUpFailure = { error ->
                            errorState.value = error
                        }
                    )
                }

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
