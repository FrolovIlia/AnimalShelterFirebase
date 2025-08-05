package com.pixelrabbit.animalshelterfirebase.ui.slide_show_screen.ui

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.pixelrabbit.animalshelterfirebase.data.Animal
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlideShowScreen(
    animals: List<Animal>,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val shuffledAnimals = remember(animals) {
        animals.shuffled()
    }

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val portraitAdUnitId = "R-M-16111641-5"
    val landscapeAdUnitId = "R-M-16111641-4"
    val adUnitId = if (isPortrait) portraitAdUnitId else landscapeAdUnitId

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR
        val window = activity?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    val bannerAdViewRef = remember { mutableStateOf<BannerAdView?>(null) }

    LaunchedEffect(adUnitId) {
        while (true) {
            delay(30_000L)
            bannerAdViewRef.value?.loadAd(AdRequest.Builder().build())
        }
    }

    val pagerState = rememberPagerState { shuffledAnimals.size }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        delay(10_000L)
        coroutineScope.launch {
            val nextPage = (pagerState.currentPage + 1) % shuffledAnimals.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    BackHandler {
        onBackClick()
    }

    Scaffold(
        containerColor = Color.DarkGray,
        topBar = {
            if (isPortrait) {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = { onBackClick() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Назад",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        }
    ) { paddingValues ->

        if (isPortrait) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) { page ->
                    val animal = shuffledAnimals[page]

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 8.dp), // Убрал horizontal padding
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = animal.imageUrl,
                                contentDescription = animal.name,
                                modifier = Modifier
                                    .fillMaxWidth() // Изменил с fillMaxHeight(0.9f)
                                    .aspectRatio(4f / 3f),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isPortrait) 100.dp else 50.dp)
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = { context ->
                            BannerAdView(context).apply {
                                setAdUnitId(adUnitId)
                                setAdSize(BannerAdSize.stickySize(context, 320))
                                loadAd(AdRequest.Builder().build())
                                bannerAdViewRef.value = this
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier
                            .fillMaxSize()
                    ) { page ->
                        val animal = shuffledAnimals[page]
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = animal.imageUrl,
                                contentDescription = animal.name,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .aspectRatio(4f / 3f, matchHeightConstraintsFirst = true)
                                    .background(Color.DarkGray),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }

                    TopAppBar(
                        title = { },
                        navigationIcon = {
                            IconButton(onClick = { onBackClick() }) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Назад",
                                    tint = Color.White
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.align(Alignment.TopStart)
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(Color.Black.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    AndroidView(
                        factory = { context ->
                            BannerAdView(context).apply {
                                setAdUnitId(adUnitId)
                                setAdSize(BannerAdSize.stickySize(context, 728))
                                loadAd(AdRequest.Builder().build())
                                bannerAdViewRef.value = this
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}