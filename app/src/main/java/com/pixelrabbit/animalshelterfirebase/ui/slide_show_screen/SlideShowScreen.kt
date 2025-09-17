package com.pixelrabbit.animalshelterfirebase.ui.slide_show_screen

import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.WindowManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.pixelrabbit.animalshelterfirebase.R
import com.pixelrabbit.animalshelterfirebase.data.model.Animal
import com.pixelrabbit.animalshelterfirebase.utils.AdUnitIds
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData
import kotlinx.coroutines.delay
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SlideShowScreen(
    animals: List<Animal>,
    onBackClick: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity

    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setNavigationBarColor(
            color = Color.Transparent,
            darkIcons = false
        )
    }

    val shuffledAnimals = remember(animals) { animals.shuffled() }

    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    val adUnitId = if (isPortrait) {
        AdUnitIds.slideShowPortrait(context)
    } else {
        AdUnitIds.slideShowLandscape(context)
    }

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

    // Автопереключение слайдов
    LaunchedEffect(Unit) {
        while (true) {
            delay(10_000L)
            val nextPage = (pagerState.currentPage + 1) % shuffledAnimals.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    BackHandler { onBackClick() }

    Scaffold(
        containerColor = Color.Transparent,
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
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = R.drawable.background_gray_dark),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

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
                        SlideShowItem(animal = shuffledAnimals[page], isPortrait = isPortrait)
                    }

                    AdBanner(adUnitId = adUnitId, modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp))
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.fillMaxSize()
                        ) { page ->
                            SlideShowItem(animal = shuffledAnimals[page], isPortrait = isPortrait)
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

                    AdBanner(adUnitId = adUnitId, modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp))
                }
            }
        }
    }
}

@Composable
fun SlideShowItem(animal: Animal, isPortrait: Boolean) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.fillMaxSize().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = animal.imageUrl,
                contentDescription = animal.name,
                modifier = if (isPortrait) {
                    Modifier.fillMaxWidth().aspectRatio(4f / 3f)
                } else {
                    Modifier.height(screenHeight).aspectRatio(4f / 3f)
                },
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
            override fun onAdLoaded() {}
            override fun onAdFailedToLoad(error: AdRequestError) {}
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

        onDispose { bannerAdView.destroy() }
    }

    AndroidView(factory = { bannerAdView }, modifier = modifier)
}
