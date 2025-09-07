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
import com.pixelrabbit.animalshelterfirebase.utils.SearchField

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListUsersScreen(
    users: List<UserObject>,
    userViewModel: UserViewModel,
    navController: NavController
) {
    var query by remember { mutableStateOf("") }

    val filteredUsers = remember(query, users) {
        if (query.isBlank()) {
            users
        } else {
            val lowerCaseQuery = query.lowercase()
            users.filter {
                it.name.lowercase().contains(lowerCaseQuery) ||
                        it.email.lowercase().contains(lowerCaseQuery) ||
                        it.phone.contains(lowerCaseQuery)
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = BackgroundGray,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Управление доступом",
                        fontSize = 20.sp,
                        fontFamily = AnimalFont
                    )
                },
                navigationIcon = {
                    TextButton(onClick = { navController.navigateUp() }) {
                        Text(
                            text = "Назад",
                            fontSize = 16.sp,
                            fontFamily = AnimalFont
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = BackgroundGray
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            SearchField(
                query = query,
                onQueryChange = { query = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                placeholder = "Поиск по всем полям"
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredUsers.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Пользователей не найдено.",
                        fontFamily = AnimalFont,
                        color = TextBlack
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredUsers) { user ->
                        ListUsersItemUI(
                            user = user,
                            userViewModel = userViewModel,
                            onRoleChanged = { updatedUser, isAdmin ->
                                val index = filteredUsers.indexOfFirst { it.uid == updatedUser.uid }
                                if (index != -1) {
                                    val mutableList = filteredUsers.toMutableList()
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
}
