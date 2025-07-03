package com.pixelrabbit.animalshelterfirebase.ui.donation_screen.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelrabbit.animalshelterfirebase.model.ShelterViewModel
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import androidx.compose.ui.text.withStyle

import androidx.compose.ui.viewinterop.AndroidView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationScreen(
    viewModel: ShelterViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onBack: () -> Unit = {}
) {
    val context = LocalContext.current

    Scaffold(
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Донат",
                        fontSize = 20.sp,
                        fontFamily = AnimalFont
                    )
                },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(
                            text = "Назад",
                            fontSize = 16.sp,
                            fontFamily = AnimalFont
                        )
                    }
                }
            )
        },
        bottomBar = {
            AdBlock()
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                // Основной текст из БД
                Text(
                    text = viewModel.shelterData.value?.donation ?: "Загрузка...",
                    color = Color.Gray,
                    fontFamily = AnimalFont,
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Ссылка "Оформить пожертвование онлайн"
                val annotatedLinkString = buildAnnotatedString {
                    val linkText = "Оформить пожертвование онлайн"
                    val linkTag = "URL"

                    append(" ")

                    pushStringAnnotation(tag = linkTag, annotation = "https://leyka.iv-priyut.ru/campaign/donation/")
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xFF1976D2), // синий
                            textDecoration = TextDecoration.Underline,
                            fontFamily = AnimalFont,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    ) {
                        append(linkText)
                    }
                    pop()
                }

                ClickableText(
                    text = annotatedLinkString,
                    onClick = { offset ->
                        annotatedLinkString.getStringAnnotations(tag = "URL", start = offset, end = offset)
                            .firstOrNull()?.let { annotation ->
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                                context.startActivity(intent)
                            }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    )
}

@Composable
private fun AdBlock() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Divider(thickness = 2.dp, color = Color.LightGray)

        Text(
            "Просмотр рекламы",
            fontSize = 20.sp,
            fontFamily = AnimalFont,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Вы можете поддержать приют просмотром рекламы на этой странице или нажимая на иконку в правом верхнем углу, на экране со всеми животными",
            color = Color.Gray,
            fontFamily = AnimalFont,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            factory = { context ->
                BannerAdView(context).apply {
                    setAdUnitId("R-M-16111641-2") // ⚠️ Заменить на свой ID из кабинета Яндекс
                    setAdSize(BannerAdSize.fixedSize(context, 320, 250))
                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}
