package com.pixelrabbit.animalshelterfirebase.ui.main_screen


import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip

import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pixelrabbit.animalshelterfirebase.R
import com.pixelrabbit.animalshelterfirebase.data.Animal
import com.pixelrabbit.animalshelterfirebase.data.AnimalCategories
import com.pixelrabbit.animalshelterfirebase.data.MainScreenDataObject
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.bottom_menu.BottomMenu
import com.pixelrabbit.animalshelterfirebase.ui.theme.ButtonColorBlue

import com.pixelrabbit.animalshelterfirebase.ui.theme.TextSecondary
import com.pixelrabbit.animalshelterfirebase.utils.isAdmin
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.bottom_menu.BottomMenuItem
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundSecondary
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundGray

import com.google.accompanist.systemuicontroller.rememberSystemUiController
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.unit.sp
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.ButtonColorWhite
import com.pixelrabbit.animalshelterfirebase.utils.SearchField
import androidx.compose.foundation.layout.size
import androidx.navigation.NavController
import com.pixelrabbit.animalshelterfirebase.data.UserObject

import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.interstitial.InterstitialAd

import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader


@Composable
fun MainScreen(
    navData: MainScreenDataObject,
    navController: NavController,
    currentUser: UserObject,
    viewModel: MainScreenViewModel = viewModel(),
    onAnimalEditClick: (Animal) -> Unit,
    onAnimalClick: (Animal) -> Unit,
    onAdminClick: () -> Unit
) {
    val db = remember { Firebase.firestore }
    val context = LocalContext.current
    val isGuest = navData.uid == "guest"

    Log.d("MainScreen", "Current navData.uid: ${navData.uid}")

    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val animals by viewModel.animals.collectAsState()

    var isFavoritesOnly by remember { mutableStateOf(false) }
    val isAdminState = remember { mutableStateOf(false) }

    val userName by viewModel.userName.collectAsState()
    var query by remember { mutableStateOf("") }


// Инициализация межстраничной рекламы Яндекс
    val interstitialAd = remember { mutableStateOf<InterstitialAd?>(null) }
    val activity = remember(context) {
        generateSequence(context) { ctx ->
            when (ctx) {
                is android.app.Activity -> null
                is android.content.ContextWrapper -> ctx.baseContext
                else -> null
            }
        }.firstOrNull { it is android.app.Activity } as? android.app.Activity
    }

    LaunchedEffect(Unit) {
        activity?.let {
            val adLoader = InterstitialAdLoader(it)

            adLoader.setAdLoadListener(object : InterstitialAdLoadListener {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd.value = ad
//                    Toast.makeText(it, "Реклама загружена", Toast.LENGTH_SHORT).show()
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    interstitialAd.value = null
//                    Toast.makeText(
//                        it,
//                        "Реклама ещё не загружена",
////                        "Ошибка загрузки рекламы: ${error.description}",
//                        Toast.LENGTH_SHORT
//                    ).show()
                }
            })

            val adRequestConfiguration =
                AdRequestConfiguration.Builder("R-M-XXXXXX-Y") // ✅ замените на свой ID
                    .build()

            adLoader.loadAd(adRequestConfiguration)
        }
    }


    // Проверяем права администратора
    LaunchedEffect(navData.uid) {
        isAdmin(navData.uid) { isAdmin ->
            isAdminState.value = isAdmin
        }
        if (!isGuest) {
            viewModel.loadUserName(db, navData.uid)
        }
    }

    // Загрузка данных при смене вкладки или категории
    LaunchedEffect(selectedTab, selectedCategory) {
        if (selectedTab == BottomMenuItem.Favs) {
            isFavoritesOnly = true
            if (isGuest) {
                Toast.makeText(
                    context,
                    "Только для зарегистрированных пользователей",
                    Toast.LENGTH_SHORT
                ).show()
                viewModel.setAnimals(emptyList())
            } else {
                viewModel.loadFavorites(db, navData.uid)
            }
        } else {
            isFavoritesOnly = false
            if (isGuest) {
                viewModel.loadAnimals(db, "guest")
            } else {
                viewModel.loadAnimals(db, navData.uid)
            }
        }
    }

    val filteredAnimals = remember(query, animals, selectedCategory, isFavoritesOnly) {
        val queryLower = query.lowercase()

        animals.filter { animal ->
            val matchesQuery = animal.name.lowercase().contains(queryLower) ||
                    animal.description.lowercase().contains(queryLower) ||
                    animal.location.lowercase().contains(queryLower) ||
                    animal.feature.lowercase().contains(queryLower) ||
                    animal.category.lowercase().contains(queryLower)

            val matchesCategory = selectedCategory == "Все" || animal.category == selectedCategory
            val matchesFavs = !isFavoritesOnly || animal.isFavourite

            matchesQuery && matchesCategory && matchesFavs
        }
    }


    // Устанавливаем цвет статусбара
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(
            color = BackgroundGray,
            darkIcons = true
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomMenu(
                selectedTab = selectedTab,
                onTabSelected = { selected ->
                    viewModel.selectTab(selected)
                    viewModel.selectCategory("Все")
                },
                isRegistered = !isGuest,
                navController = navController,
                currentUser = currentUser
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(
                    top = paddingValues.calculateTopPadding(),
                    bottom = paddingValues.calculateBottomPadding()
                )
        ) {

            if (!isGuest) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Привет,",
                                fontSize = 16.sp,
                                fontFamily = AnimalFont,
                            )
                            Text(
                                text = userName.ifEmpty { "..." },
                                fontSize = 24.sp,
                                fontFamily = AnimalFont,
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        if (isAdminState.value) {
                            Button(
                                onClick = onAdminClick,
                                modifier = Modifier.width(130.dp),
                                colors = ButtonDefaults.buttonColors(ButtonColorBlue)
                            ) {
                                Text(text = "Добавить\nживотное")
                            }
                        }
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_ad_play),
                        contentDescription = "Реклама",
                        modifier = Modifier
                            .size(40.dp) // Короче, чем width + height
                            .clickable {
                                val ad = interstitialAd.value
                                if (ad != null && activity != null) {
                                    ad.show(activity)
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Реклама ещё не загрузилась",
//                                        "Реклама не готова или Activity не доступна",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    )
                }
            }


            val categories = listOf(
                AnimalCategories(R.drawable.ic_all_animals, "Все"),
                AnimalCategories(R.drawable.ic_cats, "Котики"),
                AnimalCategories(R.drawable.ic_dogs, "Собачки")
            )

            SearchField(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 0.dp),
                placeholder = "Поиск по животным"
            )

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category.categoryName
                    val shape = RoundedCornerShape(30.dp)

                    Card(
                        modifier = Modifier
                            .height(52.dp)
                            .then(
                                if (!isSelected) Modifier.border(
                                    1.dp,
                                    BackgroundSecondary,
                                    shape
                                ) else Modifier
                            )
                            .clip(shape)
                            .clickable {
                                viewModel.selectCategory(category.categoryName)
                            },
                        shape = shape,
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) ButtonColorBlue else ButtonColorWhite
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = category.imageCategory),
                                contentDescription = category.categoryName,
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .padding(end = 8.dp),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                text = category.categoryName,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }

            if (filteredAnimals.isEmpty()) {
                EmptyStateScreen()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredAnimals) { animal ->
                        AnimalListItemUI(
                            showEditButton = isAdminState.value,
                            animal = animal,
                            isFavourite = animal.isFavourite,
                            onAnimalClick = onAnimalClick,
                            onEditClick = onAnimalEditClick,
                            onFavouriteClick = {
                                if (isGuest) {
                                    Toast.makeText(
                                        context,
                                        "Только для зарегистрированных пользователей",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@AnimalListItemUI
                                }
                                viewModel.toggleFavorite(db, navData.uid, animal.key)
                            }
                        )
                    }
                }
            }
        }
    }
}

