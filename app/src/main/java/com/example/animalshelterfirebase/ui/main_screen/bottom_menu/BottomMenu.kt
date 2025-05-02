package com.example.animalshelterfirebase.ui.main_screen.bottom_menu

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import com.example.animalshelterfirebase.R

@Composable
fun BottomMenu(
    isFavoritesOnly: Boolean,
    onHomeClick: () -> Unit,
    onProfile: () -> Unit,
    onFavsClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.home_menu),
                    contentDescription = "Все"
                )
            },
            selected = !isFavoritesOnly,
            onClick = onHomeClick,
            alwaysShowLabel = false
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.favourite_menu),
                    contentDescription = "Избранные"
                )
            },
            selected = isFavoritesOnly,
            onClick = onFavsClick,
            alwaysShowLabel = false
        )

        NavigationBarItem(
            icon = {
                Icon(
                    painter = painterResource(id = R.drawable.profile_menu),
                    contentDescription = "Профиль"
                )
            },
            selected = false,
            onClick = onProfile,
            alwaysShowLabel = false
        )
    }
}
