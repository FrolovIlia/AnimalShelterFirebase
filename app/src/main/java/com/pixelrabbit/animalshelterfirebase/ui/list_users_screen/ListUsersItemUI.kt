package com.pixelrabbit.animalshelterfirebase.ui.list_users_screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelrabbit.animalshelterfirebase.data.model.UserObject
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundWhite
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.TextBlack
import com.pixelrabbit.animalshelterfirebase.utils.ButtonBlue
import com.pixelrabbit.animalshelterfirebase.utils.ButtonWhite

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListUsersItemUI(
    user: UserObject,
    onRoleChanged: (UserObject, Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val currentRole = if (user.isAdmin) "Редактор" else "Пользователь"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = BackgroundWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    fontSize = 16.sp,
                    fontFamily = AnimalFont,
                    fontWeight = FontWeight.Bold,
                    color = TextBlack
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = user.phone,
                    fontSize = 12.sp,
                    fontFamily = AnimalFont,
                    color = Color.Gray
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = user.email,
                    fontSize = 12.sp,
                    fontFamily = AnimalFont,
                    color = Color.Gray
                )
            }

            // Правая кнопка с общим стилем
            Box(
                modifier = Modifier
                    .wrapContentSize(Alignment.TopEnd)
                    .height(36.dp)
            ) {
                if (user.isAdmin) {
                    ButtonBlue(
                        text = currentRole,
                        modifier = Modifier,
                        onClick = { expanded = true }
                    )
                } else {
                    ButtonWhite(
                        text = currentRole,
                        modifier = Modifier,
                        onClick = { expanded = true }
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Редактор", fontFamily = AnimalFont, fontSize = 12.sp) },
                        onClick = {
                            onRoleChanged(user, true)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Пользователь", fontFamily = AnimalFont, fontSize = 12.sp) },
                        onClick = {
                            onRoleChanged(user, false)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun UserListItemUIPreview() {
    val sampleUser = UserObject(
        uid = "123",
        name = "Иван Петров",
        phone = "+7 (999) 123-45-67",
        email = "ivan.petrov@mail.ru",
        isAdmin = false,
        isOwner = false
    )

    ListUsersItemUI(
        user = sampleUser,
        onRoleChanged = { _, _ -> }
    )
}