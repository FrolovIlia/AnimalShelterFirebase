package com.pixelrabbit.animalshelterfirebase.ui.task_details_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import com.pixelrabbit.animalshelterfirebase.R
import com.pixelrabbit.animalshelterfirebase.ui.navigation.TaskDetailsNavObject
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundGray
import com.pixelrabbit.animalshelterfirebase.utils.AdUnitIds
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import kotlinx.coroutines.delay

@Composable
fun TaskDetailsScreen(
    navObject: TaskDetailsNavObject,
    onBackClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val adUnitId = AdUnitIds.taskBanner(context)

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
            AsyncImage(
                model = navObject.imageUrl.ifBlank { null },
                contentDescription = null,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(30.dp)),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(id = R.drawable.default_animal_image),
                error = painterResource(id = R.drawable.default_animal_image)
            )

            IconButton(
                onClick = onBackClick,
                modifier = Modifier.padding(24.dp)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Назад",
                    tint = Color.Unspecified
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
                text = navObject.shortDescription,
                fontSize = 36.sp,
                fontFamily = AnimalFont,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            InfoBlock(title = "Описание", value = navObject.fullDescription)
            CuratorBlock(
                name = navObject.curatorName,
                phone = navObject.curatorPhone
            )
            InfoBlock(title = "Локация", value = navObject.location)

            Spacer(modifier = Modifier.height(16.dp))
        }

        AdBannerBlock(adUnitId = adUnitId)
    }
}

@Composable
private fun InfoBlock(title: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            fontSize = 13.sp,
            color = Color.Gray,
            fontFamily = AnimalFont,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Text(
            text = value,
            fontSize = 16.sp,
            color = Color.Gray,
            fontFamily = AnimalFont
        )
    }
}

@Composable
private fun CuratorBlock(name: String, phone: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Куратор",
                fontSize = 13.sp,
                color = Color.Gray,
                fontFamily = AnimalFont,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = name,
                fontSize = 16.sp,
                color = Color.Gray,
                fontFamily = AnimalFont
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Номер куратора",
                fontSize = 13.sp,
                color = Color.Gray,
                fontFamily = AnimalFont,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = phone,
                fontSize = 16.sp,
                color = Color.Gray,
                fontFamily = AnimalFont
            )
        }
    }
}

@Composable
private fun AdBannerBlock(adUnitId: String) {
    val context = LocalContext.current

    val bannerAdView = remember {
        BannerAdView(context).apply {
            setAdUnitId(adUnitId)
            setAdSize(BannerAdSize.fixedSize(context, 320, 50))
            loadAd(AdRequest.Builder().build())
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(31000)
            bannerAdView.loadAd(AdRequest.Builder().build())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(BackgroundGray)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            factory = { bannerAdView }
        )
    }
}
