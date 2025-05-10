package com.example.animalshelterfirebase.ui.details_screen.ui


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.animalshelterfirebase.R
import com.example.animalshelterfirebase.ui.details_screen.data.DetailsNavObject
import com.example.animalshelterfirebase.ui.login.ButtonBlue
import com.example.animalshelterfirebase.ui.login.ButtonWhite
import com.example.animalshelterfirebase.ui.theme.AnimalFont


@Preview(showBackground = true)
@Composable
fun DetailsScreen(
    navObject: DetailsNavObject = DetailsNavObject()
) {


    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            )
            Column(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(R.drawable.dog),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth(1f)
                        .height(360.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(Color.Blue),
                    contentScale = ContentScale.Crop
                )
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp)
                )


                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {


                    Text(
                        text = "Кличка",
                        color = Color.Black,
                        fontFamily = AnimalFont,
                        fontSize = 36.sp
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(1f)
                            .height(28.dp)
                    ) {
                        Text(text = "Возраст")
                    }

                    Text(
                        text = "На этом месте будет описание животного, ориентировочно на 4 строки.\n" +
                                "пока не знаю, будет ли ограничение по количеству строк или сделаем прокручивающийся",
                        color = Color.Gray,
                        fontFamily = AnimalFont,
                        fontSize = 16.sp
                    )
                    Spacer(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                    )


                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Text(
                                modifier = Modifier.padding(bottom = 5.dp),
                                text = "Номер куратора",
                                color = Color.Gray,
                                fontFamily = AnimalFont,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "+79301234567",
                                color = Color.Gray,
                                fontFamily = AnimalFont,
                                fontSize = 16.sp
                            )
                        }

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                        ) {
                            Text(
                                modifier = Modifier.padding(bottom = 5.dp),
                                text = "Номер вольера",
                                color = Color.Gray,
                                fontFamily = AnimalFont,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "34",
                                color = Color.Gray,
                                fontFamily = AnimalFont,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ButtonBlue(
                text = "Усыновить",
                modifier = Modifier.weight(1f)
            ) {

            }
            ButtonWhite(
                text = "Донат",
                modifier = Modifier.weight(1f)
            ) {

            }
        }
    }
}