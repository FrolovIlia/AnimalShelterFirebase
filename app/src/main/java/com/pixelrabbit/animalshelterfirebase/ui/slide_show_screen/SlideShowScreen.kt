package com.pixelrabbit.animalshelterfirebase.ui.slide_show_screen

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
import com.pixelrabbit.animalshelterfirebase.data.model.Animal
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
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
                    SlideShowItem(animal)
                }

                AdBanner(
                    adUnitId = adUnitId,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
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
                        SlideShowItem(animal)
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

                AdBanner(
                    adUnitId = adUnitId,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                )
            }
        }
    }
}

@Composable
fun SlideShowItem(animal: Animal) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 8.dp),
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
                    .fillMaxWidth()
                    .aspectRatio(4f / 3f),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Composable
fun AdBanner(adUnitId: String, modifier: Modifier) {
    val context = LocalContext.current
    val bannerAdView = remember { BannerAdView(context) }

    DisposableEffect(adUnitId) {
        val listener = object : BannerAdEventListener {
            override fun onAdLoaded() {
                // Реклама успешно загружена. Через 30 секунд перезагрузим её.
                // Можно добавить логику для отслеживания показов
            }
            override fun onAdFailedToLoad(error: AdRequestError) {
                // Ошибка загрузки. Попробуем снова через 30 секунд.
                // Это предотвращает лишние запросы.
            }
            override fun onAdClicked() {}
            override fun onLeftApplication() {}
            override fun onReturnedToApplication() {}
            override fun onImpression(impressionData: ImpressionData?) {}
        }

        bannerAdView.apply {
            setAdUnitId(adUnitId)
            setBannerAdEventListener(listener)
            val displayMetrics = context.resources.displayMetrics
            val adWidth = (displayMetrics.widthPixels / displayMetrics.density).toInt()
            setAdSize(BannerAdSize.stickySize(context, adWidth))
            loadAd(AdRequest.Builder().build())
        }

        onDispose {
            bannerAdView.destroy()
        }
    }

    AndroidView(
        factory = { bannerAdView },
        modifier = modifier
    )
}