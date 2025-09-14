package com.pixelrabbit.animalshelterfirebase.ui.main_screen.bottom_menu

import com.pixelrabbit.animalshelterfirebase.R

sealed class BottomMenuItem(
    val title: String,
    val iconId: Int
) {
    object Home : BottomMenuItem(
        title = "",
        iconId = R.drawable.home_menu
    )

    object Favs : BottomMenuItem(
        title = "",
        iconId = R.drawable.favourite_menu
    )

    object Tasks : BottomMenuItem(
        title = "",
        iconId = R.drawable.task_menu
    )

    object Profile : BottomMenuItem(
        title = "",
        iconId = R.drawable.profile_menu
    )
}