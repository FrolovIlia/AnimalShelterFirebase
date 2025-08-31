package com.pixelrabbit.animalshelterfirebase.ui.details_animal_screen

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
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.SavedStateHandle
import coil.compose.AsyncImage
import com.pixelrabbit.animalshelterfirebase.R
import com.pixelrabbit.animalshelterfirebase.data.model.Animal
import com.pixelrabbit.animalshelterfirebase.data.model.UserObject
import com.pixelrabbit.animalshelterfirebase.ui.authorization.UserViewModel
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.MainScreenViewModel
import com.pixelrabbit.animalshelterfirebase.ui.navigation.AnimalDetailsNavObject
import com.pixelrabbit.animalshelterfirebase.ui.navigation.DonationNavObject
import com.pixelrabbit.animalshelterfirebase.ui.theme.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.pixelrabbit.animalshelterfirebase.utils.*

@Composable
fun AnimalDetailsScreen(
    navObject: AnimalDetailsNavObject,
    userViewModel: UserViewModel,
    mainScreenViewModel: MainScreenViewModel,
    onBackClick: () -> Unit,
    onAdoptClick: (Animal, UserObject) -> Unit,
    savedStateHandle: SavedStateHandle? = null,
    onDonateClick: (DonationNavObject) -> Unit
) {
    val adoptionSuccessState = savedStateHandle
        ?.getLiveData<Boolean>("showAdoptionSuccess")
        ?.observeAsState(initial = false) ?: remember { mutableStateOf(false) }

    var showNotification by remember { mutableStateOf(false) }

    LaunchedEffect(navObject.uid) {
        if (navObject.uid != "guest") {
            userViewModel.loadUser(navObject.uid)
        }
    }

    val userState by userViewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()
    val isGuest = navObject.uid == "guest" || navObject.uid.isEmpty()
    val animal = mainScreenViewModel.getAnimalByKey(navObject.key)

    // Состояние избранного для реактивного обновления иконки
    var isFavouriteState by remember { mutableStateOf(animal?.isFavourite ?: navObject.isFavourite) }

    LaunchedEffect(adoptionSuccessState.value) {
        if (adoptionSuccessState.value) {
            showNotification = true
            savedStateHandle?.set("showAdoptionSuccess", false)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (userState == null && !isGuest) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundGray)
                    .systemBarsPadding()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .padding(16.dp)
                ) {
                    if (navObject.imageUrl.isNullOrBlank()) {
                        Image(
                            painter = painterResource(id = R.drawable.default_animal_image),
                            contentDescription = "Placeholder image",
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(30.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        AsyncImage(
                            model = navObject.imageUrl,
                            contentDescription = navObject.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(30.dp)),
                            contentScale = ContentScale.Crop,
                            error = painterResource(id = R.drawable.default_animal_image),
                            placeholder = painterResource(id = R.drawable.default_animal_image)
                        )
                    }

                    // кнопка назад
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(24.dp)
                            .size(32.dp)

                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_back),
                            contentDescription = "Назад",
                            tint = Color.Unspecified
                        )
                    }

                    // кнопка избранного
                    IconButton(
                        onClick = {
                            if (isGuest) {
                                Toast.makeText(
                                    context,
                                    "Авторизуйтесь, чтобы добавлять в избранное",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                mainScreenViewModel.toggleFavorite(navObject.uid, navObject.key)
                                isFavouriteState = !isFavouriteState // обновляем состояние для UI
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(24.dp)
                            .size(32.dp)
                    ) {
                        Image(
                            painter = painterResource(
                                if (isFavouriteState) R.drawable.favourite else R.drawable.favourite_border
                            ),
                            contentDescription = "Favorite icon",
                            contentScale = ContentScale.Fit, // добавляем, чтобы размер картинок был одинаковым
                            modifier = Modifier.fillMaxSize() // гарантируем, что картинка занимает весь IconButton
                        )
                    }
                }

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
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

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
                            val user = userState
                            if (isGuest || user == null) {
                                Toast.makeText(
                                    context,
                                    "Авторизуйтесь, чтобы подать заявку",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val animalData = Animal(
                                    key = navObject.key,
                                    name = navObject.name,
                                    age = navObject.age,
                                    curatorPhone = navObject.curatorPhone,
                                    location = navObject.location,
                                    description = navObject.description,
                                    imageUrl = navObject.imageUrl,
                                    category = navObject.category,
                                    feature = navObject.feature
                                )
                                onAdoptClick(animalData, user)
                            }
                        }
                    )

                    ButtonWhite(
                        text = "Донат",
                        modifier = Modifier.weight(1f),
                        onClick = {
                            onDonateClick(DonationNavObject())
                        }
                    )
                }
            }
        }

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
                        "В течение 3 дней мы рассмотрим\nеё и перезвоним вам.",
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
