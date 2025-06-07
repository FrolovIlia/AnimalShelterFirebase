package com.pixelrabbit.animalshelterfirebase.ui.main_screen.bottom_menu

import com.pixelrabbit.animalshelterfirebase.R

sealed class BottomMenuItem(
    val route: String,
    val title: String,
    val iconId: Int
) {
    object Home : BottomMenuItem(
        route = "",
        title = "",
        iconId = R.drawable.home_menu
    )

    object Favs : BottomMenuItem(
        route = "",
        title = "",
        iconId = R.drawable.favourite_menu
    )

    object Settings : BottomMenuItem(
        route = "",
        title = "",
        iconId = R.drawable.profile_menu
    )
}