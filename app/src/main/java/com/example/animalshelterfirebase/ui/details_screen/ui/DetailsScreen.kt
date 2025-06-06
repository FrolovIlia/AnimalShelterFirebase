package com.example.animalshelterfirebase.ui.details_screen.ui

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import coil.compose.AsyncImage
import com.example.animalshelterfirebase.R
import com.example.animalshelterfirebase.data.Animal
import com.example.animalshelterfirebase.ui.details_screen.data.DetailsNavObject
import com.example.animalshelterfirebase.ui.theme.AnimalFont
import com.example.animalshelterfirebase.ui.theme.BackgroundGray
import com.example.animalshelterfirebase.utils.ButtonBlue
import com.example.animalshelterfirebase.utils.ButtonWhite

import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import com.example.animalshelterfirebase.ui.theme.InfoColorOrange
import com.example.animalshelterfirebase.ui.theme.InfoColorPurple
import com.example.animalshelterfirebase.utils.ButtonTransparent
import com.example.animalshelterfirebase.utils.InfoTag


@Composable
fun DetailsScreen(
    navObject: DetailsNavObject = DetailsNavObject(),
    onBackClick: () -> Unit,
    onAdoptClick: (Animal) -> Unit,
    savedStateHandle: SavedStateHandle? = null
) {
    val adoptionSuccessState = savedStateHandle
        ?.getLiveData<Boolean>("showAdoptionSuccess")
        ?.observeAsState(initial = false) ?: remember { mutableStateOf(false) }

    var showNotification by remember { mutableStateOf(false) }
    val isGuest = navObject.uid == "guest" || navObject.uid.isEmpty()

    LaunchedEffect(adoptionSuccessState.value) {
        if (adoptionSuccessState.value) {
            showNotification = true
            savedStateHandle?.set("showAdoptionSuccess", false)
        }
    }

    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .systemBarsPadding()
        ) {
            // Фиксированное изображение
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .padding(16.dp)
            ) {
                AsyncImage(
                    model = navObject.imageUrl,
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(30.dp)),
                    contentScale = ContentScale.Crop
                )

                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.padding(24.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_back),
                        contentDescription = "Back",
                        tint = Color.Unspecified
                    )
                }
            }

            // Прокручиваемая часть (вся инфа)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = navObject.name,
                    color = Color.Black,
                    fontFamily = AnimalFont,
                    fontSize = 36.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    InfoTag(text = navObject.age, backgroundColor = InfoColorOrange)
                    InfoTag(text = navObject.feature, backgroundColor = InfoColorPurple)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = navObject.description,
                    color = Color.Gray,
                    fontFamily = AnimalFont,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Номер куратора",
                            color = Color.Gray,
                            fontFamily = AnimalFont,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        Text(
                            text = navObject.curatorPhone,
                            color = Color.Gray,
                            fontFamily = AnimalFont,
                            fontSize = 16.sp,
                            maxLines = 4,
                            softWrap = true
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Расположение",
                            color = Color.Gray,
                            fontFamily = AnimalFont,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(bottom = 5.dp)
                        )
                        Text(
                            text = navObject.location,
                            color = Color.Gray,
                            fontFamily = AnimalFont,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp)) // Чтобы не налезало на кнопки
            }

            // Фиксированные кнопки снизу
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ButtonBlue(
                    text = "Усыновить",
                    modifier = Modifier.weight(1f),
                    onClick = {
                        if (isGuest) {
                            Toast.makeText(
                                context,
                                "Авторируйтесь, чтобы подать заявку",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            val animal = Animal(
                                name = navObject.name,
                                age = navObject.age,
                                description = navObject.description,
                                imageUrl = navObject.imageUrl,
                                curatorPhone = navObject.curatorPhone,
                                location = navObject.location,
                                feature = navObject.feature
                            )
                            onAdoptClick(animal)
                        }
                    }
                )

                ButtonWhite(
                    text = "Донат",
                    modifier = Modifier.weight(1f),
                    onClick = { /* обработка доната */ }
                )
            }
        }

        // Уведомление
        if (showNotification) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { showNotification = false }
            )

            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(24.dp),
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(R.drawable.ready_adopt_request),
                        contentDescription = "done_adoption",
                        modifier = Modifier
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally)
                    )

                    Text(
                        "Заявка отправлена!",
                        fontSize = 20.sp,
                        fontFamily = AnimalFont,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        "В течение 3 дней мы рассмотрим \nеё и перезвоним вам.",
                        fontSize = 14.sp,
                        fontFamily = AnimalFont,
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    ButtonTransparent(
                        text = "Закрыть",
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showNotification = false }
                    )
                }
            }
        }
    }
}

