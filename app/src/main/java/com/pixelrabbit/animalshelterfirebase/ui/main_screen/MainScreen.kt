package com.pixelrabbit.animalshelterfirebase.ui.main_screen

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pixelrabbit.animalshelterfirebase.R
import com.pixelrabbit.animalshelterfirebase.data.model.Animal
import com.pixelrabbit.animalshelterfirebase.data.model.AnimalCategories
import com.pixelrabbit.animalshelterfirebase.data.model.MainScreenDataObject
import com.pixelrabbit.animalshelterfirebase.data.model.UserObject
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.bottom_menu.BottomMenu
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.bottom_menu.BottomMenuItem
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundGray
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundSecondary
import com.pixelrabbit.animalshelterfirebase.ui.theme.ButtonColorBlue
import com.pixelrabbit.animalshelterfirebase.ui.theme.ButtonColorWhite
import com.pixelrabbit.animalshelterfirebase.ui.theme.TextSecondary
import com.pixelrabbit.animalshelterfirebase.utils.SearchField
import com.yandex.mobile.ads.common.AdRequestConfiguration
import com.yandex.mobile.ads.interstitial.InterstitialAd
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.interstitial.InterstitialAdLoadListener
import com.yandex.mobile.ads.interstitial.InterstitialAdLoader
import com.pixelrabbit.animalshelterfirebase.ui.authorization.UserViewModel
import com.pixelrabbit.animalshelterfirebase.ui.slide_show_screen.SlideShowScreenObject

import android.content.pm.ActivityInfo
import androidx.compose.runtime.DisposableEffect
import com.pixelrabbit.animalshelterfirebase.ui.animal_list_item_UI.AnimalListItemUI
import com.pixelrabbit.animalshelterfirebase.ui.theme.TextBlack

@Composable
fun MainScreen(
    navData: MainScreenDataObject,
    navController: NavController,
    viewModel: MainScreenViewModel,
    userViewModel: UserViewModel,
    onAnimalEditClick: (Animal) -> Unit,
    onAnimalClick: (Animal) -> Unit,
    onAdminClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = remember(context) {
        generateSequence(context) { ctx ->
            when (ctx) {
                is android.app.Activity -> null
                is android.content.ContextWrapper -> ctx.baseContext
                else -> null
            }
        }.firstOrNull { it is android.app.Activity } as? android.app.Activity
    }
    val isGuest = navData.uid == "guest"


    LaunchedEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }


    // Собираем состояние экрана
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val animals by viewModel.animals.collectAsState()
    val isAdmin by userViewModel.isAdmin.collectAsState()
    val user by userViewModel.currentUser.collectAsState()
    val userName = user?.name ?: ""

    var isFavoritesOnly by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    // Инициализация межстраничной рекламы Яндекс
    val interstitialAd = remember { mutableStateOf<InterstitialAd?>(null) }

    LaunchedEffect(Unit) {
        activity?.let {
            val adLoader = InterstitialAdLoader(it)

            adLoader.setAdLoadListener(object : InterstitialAdLoadListener {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd.value = ad
                }

                override fun onAdFailedToLoad(error: AdRequestError) {
                    interstitialAd.value = null
                }
            })

            val adRequestConfiguration =
                AdRequestConfiguration.Builder("R-M-16111641-1").build() // замените на ваш ID
            adLoader.loadAd(adRequestConfiguration)
        }
    }

    // Загрузка пользователя только при изменении uid
    LaunchedEffect(navData.uid) {
        if (!isGuest) {
            userViewModel.loadUser(navData.uid)
        } else {
            userViewModel.clearUser()
        }
    }

    // Загрузка животных при смене вкладки или категории
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
                viewModel.loadFavorites(navData.uid)
            }
        } else {
            isFavoritesOnly = false
            viewModel.loadAnimals(navData.uid)
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

    // UI-код
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomMenu(
                selectedTab = selectedTab,
                onTabSelected = {
                    viewModel.selectTab(it)
                    viewModel.selectCategory("Все")
                },
                isRegistered = !isGuest,
                navController = navController,
                currentUser = user ?: UserObject(
                    uid = "guest",
                    name = "Гость",
                    phone = "",
                    email = ""
                )
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
            // Этот Row будет отображаться всегда
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Левая часть (приветствие или просто Spacer, если гость)
                if (!isGuest) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Привет,", fontSize = 16.sp, fontFamily = AnimalFont)
                        Text(
                            userName.ifEmpty { "..." },
                            fontSize = 24.sp,
                            fontFamily = AnimalFont
                        )
                    }
                } else {
                    // Можно добавить Spacer, чтобы выровнять кнопки, или оставить пустым
                    Spacer(modifier = Modifier.weight(1f))
                }

                // Кнопка администратора (отображается только для админов)
                if (isAdmin) {
                    val shape = RoundedCornerShape(30.dp)
                    Card(
                        modifier = Modifier
                            .width(105.dp) // <- подобрать под ширину, обычно 100-110
                            .height(52.dp)
                            .border(1.dp, BackgroundSecondary, shape)
                            .clip(shape)
                            .clickable { onAdminClick() },
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
                                text = "Добавить\nживотное",
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

                // Слайд-шоу (теперь всегда отображается)
                Image(
                    painter = painterResource(id = R.drawable.ic_presentation),
                    contentDescription = "Слайд-шоу",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            navController.navigate(SlideShowScreenObject)
                        }
                )

                Spacer(modifier = Modifier.width(10.dp))

                // Реклама (теперь всегда отображается)
                Image(
                    painter = painterResource(id = R.drawable.ic_ad_play),
                    contentDescription = "Реклама",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable {
                            val ad = interstitialAd.value
                            if (ad != null && activity != null) {
                                ad.show(activity)
                                Toast.makeText(
                                    context,
                                    "Спасибо за просмотр!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Реклама ещё не загрузилась",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                )
            }

            SearchField(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = "Поиск по животным"
            )

            // Категории
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                val categories = listOf(
                    AnimalCategories(R.drawable.ic_all_animals, "Все"),
                    AnimalCategories(R.drawable.ic_cats, "Котики"),
                    AnimalCategories(R.drawable.ic_dogs, "Собачки"),
                    AnimalCategories(R.drawable.ic_other_anim, "Остальные")
                )

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
                            .clickable { viewModel.selectCategory(category.categoryName) },
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
                                color = if (isSelected) TextBlack else TextSecondary
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
                            showEditButton = isAdmin,
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
                                viewModel.toggleFavorite(navData.uid, animal.key)
                            }
                        )
                    }
                }
            }
        }
    }
}