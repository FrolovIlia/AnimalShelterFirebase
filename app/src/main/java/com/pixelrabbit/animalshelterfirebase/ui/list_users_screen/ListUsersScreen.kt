package com.pixelrabbit.animalshelterfirebase.ui.list_users_screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pixelrabbit.animalshelterfirebase.data.model.UserObject
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundGray
import com.pixelrabbit.animalshelterfirebase.ui.theme.TextBlack
import com.pixelrabbit.animalshelterfirebase.ui.authorization.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListUsersScreen(
    users: List<UserObject>,
    userViewModel: UserViewModel,
    navController: NavController
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundGray,
        topBar = {
            TopAppBar(
                title = {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Список Пользователей",
                            fontSize = 20.sp,
                            fontFamily = AnimalFont,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                },
                navigationIcon = {
                    TextButton(onClick = { navController.navigateUp() }) {
                        Text(
                            text = "Назад",
                            fontSize = 16.sp,
                            fontFamily = AnimalFont,
                            color = TextBlack
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundGray
                )
            )
        }
    ) { paddingValues ->
        if (users.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Пользователей пока нет.",
                    fontFamily = AnimalFont,
                    color = TextBlack
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(users) { user ->
                    ListUsersItemUI(
                        user = user,
                        userViewModel = userViewModel,
                        onRoleChanged = { updatedUser, isAdmin ->
                            val index = users.indexOfFirst { it.uid == updatedUser.uid }
                            if (index != -1) {
                                val mutableList = users.toMutableList()
                                mutableList[index] = mutableList[index].copy(isAdmin = isAdmin)
                            }
                        }
                    )
                }
                item { Spacer(modifier = Modifier.height(16.dp)) }
            }
        }
    }
}
