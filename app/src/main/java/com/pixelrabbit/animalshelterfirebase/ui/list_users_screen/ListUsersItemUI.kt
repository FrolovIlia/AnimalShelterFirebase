package com.pixelrabbit.animalshelterfirebase.ui.list_users_screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelrabbit.animalshelterfirebase.data.model.UserObject
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundWhite
import com.pixelrabbit.animalshelterfirebase.ui.theme.TextBlack
import com.pixelrabbit.animalshelterfirebase.utils.ButtonBlue
import com.pixelrabbit.animalshelterfirebase.utils.ButtonWhite
import com.pixelrabbit.animalshelterfirebase.ui.authorization.UserViewModel

@Composable
fun ListUsersItemUI(
    user: UserObject,
    userViewModel: UserViewModel,
    onRoleChanged: (UserObject, Boolean) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var isAdminState by remember { mutableStateOf(user.isAdmin) }

    val currentRole = if (isAdminState) "Редактор" else "Пользователь"

    fun updateRole(user: UserObject, isAdmin: Boolean) {
        userViewModel.updateUserRole(user.uid, isAdmin)
        isAdminState = isAdmin
        onRoleChanged(user, isAdmin)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
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
                Text(user.name, fontSize = 16.sp, fontFamily = AnimalFont, color = TextBlack)
                Spacer(Modifier.height(2.dp))
                Text(user.phone, fontSize = 12.sp, fontFamily = AnimalFont)
                Spacer(Modifier.height(2.dp))
                Text(user.email, fontSize = 12.sp, fontFamily = AnimalFont)
            }

            Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                if (isAdminState) {
                    ButtonBlue(
                        text = currentRole,
                        onClick = { expanded = true },
                        modifier = Modifier.height(36.dp)
                    )
                } else {
                    ButtonWhite(
                        text = currentRole,
                        onClick = { expanded = true },
                        modifier = Modifier.height(36.dp)
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Редактор", fontFamily = AnimalFont, fontSize = 12.sp) },
                        onClick = {
                            updateRole(user, true)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Пользователь", fontFamily = AnimalFont, fontSize = 12.sp) },
                        onClick = {
                            updateRole(user, false)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}
