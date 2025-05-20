package com.example.animalshelterfirebase.ui.start_screen.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animalshelterfirebase.R
import com.example.animalshelterfirebase.utils.ButtonBlue
import com.example.animalshelterfirebase.utils.ButtonTransparent
import com.example.animalshelterfirebase.utils.ButtonWhite
import com.example.animalshelterfirebase.ui.theme.AnimalFont

@Composable
fun StartScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onGuestClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.unsplash_img),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.3f)))

        // Logo and title
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
                fontFamily = AnimalFont,
                fontSize = 32.sp,
                color = Color.Black
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(16.dp)
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Счастье ближе чем ты думаешь!",
                fontFamily = AnimalFont,
                fontSize = 24.sp,
                color = Color.Black,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(20.dp))

            ButtonBlue(text = "Войти", modifier = Modifier.fillMaxWidth(), onClick = onLoginClick)
            Spacer(modifier = Modifier.height(8.dp))

            ButtonWhite(text = "Зарегистрироваться", modifier = Modifier.fillMaxWidth(), onClick = onRegisterClick)
            Spacer(modifier = Modifier.height(8.dp))

            ButtonTransparent(text = "Открыть без регистрации", modifier = Modifier.fillMaxWidth(), onClick = onGuestClick)
        }
    }
}
