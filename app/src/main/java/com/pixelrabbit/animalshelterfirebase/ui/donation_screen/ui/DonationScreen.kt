package com.pixelrabbit.animalshelterfirebase.ui.donation_screen.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelrabbit.animalshelterfirebase.ui.donation_screen.data.DonationNavObject
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationScreen(
    navObject: DonationNavObject,
    onBack: () -> Unit = {}
) {
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

                Text(
                    text = navObject.donation,
                    color = Color.Gray,
                    fontFamily = AnimalFont,
                    fontSize = 16.sp
                )
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

        HorizontalDivider(thickness = 2.dp, color = Color.LightGray)

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
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Text("Здесь будет реклама", color = Color.DarkGray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DonationScreenPreview() {
    val fakeNavObject = DonationNavObject(
        donation = "Наш приют существует исключительно благодаря вашей поддержке.\n\n" +
                "Каждый день мы заботимся о десятках животных, нуждающихся в еде, уходе, медицинской помощи и тепле.\n\n" +
                "Даже небольшое пожертвование помогает нам покупать корм, оплачивать ветеринарные услуги и поддерживать чистоту в вольерах.\n\n" +
                "Мы верим, что добрые дела объединяют людей, и благодарим каждого, кто помогает нам создавать безопасное место для бездомных животных.\n\n" +
                "Если у вас нет возможности помочь финансово — вы можете стать волонтёром, рассказать о нас друзьям или просто поделиться ссылкой в соцсетях. Каждое действие имеет значение!\n\n" +
                "Спасибо, что вы с нами ❤\uFE0F\n\n" +
                "Наши реквизиты:\n" +
                "📍 Сбербанк: 5469 3800 5678 1234\n" +
                "📍 Тинькофф: 5536 9140 1234 5678\n" +
                "📍 PayPal: animalhelp@example.com\n\n" +
                "Поддержите хвостатых — они нуждаются в нас!"
    )

    DonationScreen(navObject = fakeNavObject)
}
