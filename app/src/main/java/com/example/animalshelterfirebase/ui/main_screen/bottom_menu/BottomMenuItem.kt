package com.example.animalshelterfirebase.ui.main_screen.bottom_menu

import com.example.animalshelterfirebase.R

sealed class BottomMenuItem(
    val route: String,
    val title: String,
    val iconId: Int
) {
    object Home : BottomMenuItem(
        route = "",
        title = "Главная",
        iconId = R.drawable.home_menu
    )

    object Favs : BottomMenuItem(
        route = "",
        title = "Любимцы",
        iconId = R.drawable.favourite_menu
    )

    object Settings : BottomMenuItem(
        route = "",
        title = "Профиль",
        iconId = R.drawable.profile_menu
    )
}