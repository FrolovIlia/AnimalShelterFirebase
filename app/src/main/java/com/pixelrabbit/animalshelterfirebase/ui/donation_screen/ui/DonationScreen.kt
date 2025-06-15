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

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelrabbit.animalshelterfirebase.model.ShelterViewModel
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import androidx.compose.material3.Divider

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonationScreen(
    viewModel: ShelterViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
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
                    text = viewModel.shelterData.value?.donation ?: "Загрузка...",
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
