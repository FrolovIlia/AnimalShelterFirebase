package com.pixelrabbit.animalshelterfirebase.ui.slide_show_screen


import android.content.pm.ActivityInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale

import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.pixelrabbit.animalshelterfirebase.data.Animal
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

import androidx.activity.compose.LocalActivity

@Composable
fun SlideShowScreen(
    navController: NavController,
    animals: List<Animal>
) {
    val activity = LocalActivity.current

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }


    val pagerState = rememberPagerState { animals.size }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        delay(5000L)
        coroutineScope.launch {
            val next = (pagerState.currentPage + 1) % animals.size
            pagerState.animateScrollToPage(next)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val animal = animals[page]
            Image(
                painter = rememberImagePainter(animal.imageUrl),
                contentDescription = animal.name,
                contentScale = ContentScale.Fit,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp)
            )
        }

        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Назад",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
