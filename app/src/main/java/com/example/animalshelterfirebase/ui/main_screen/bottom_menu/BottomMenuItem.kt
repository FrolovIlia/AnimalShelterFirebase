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
        iconId = R.drawable.ic_home
    )

    object Favs : BottomMenuItem(
        route = "",
        title = "Любимцы",
        iconId = R.drawable.ic_favs
    )

    object Settings : BottomMenuItem(
        route = "",
        title = "Настройки",
        iconId = R.drawable.ic_settings
    )
}