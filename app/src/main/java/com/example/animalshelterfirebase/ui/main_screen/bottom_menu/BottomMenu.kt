package com.example.animalshelterfirebase.ui.main_screen.bottom_menu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.animalshelterfirebase.R
import com.example.animalshelterfirebase.ui.theme.ButtonColorBlue

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
                CircularIcon(
                    resId = R.drawable.home_menu,
                    backgroundColor = if (!isFavoritesOnly) ButtonColorBlue else Color.Transparent,
                    iconTint = Color.Gray
                )
            },
            selected = !isFavoritesOnly,
            onClick = onHomeClick,
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                CircularIcon(
                    resId = R.drawable.favourite_menu,
                    backgroundColor = if (isFavoritesOnly) ButtonColorBlue else Color.Transparent,
                    iconTint = Color.Gray
                )
            },
            selected = isFavoritesOnly,
            onClick = onFavsClick,
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                CircularIcon(
                    resId = R.drawable.profile_menu,
                    backgroundColor = Color.Transparent,
                    iconTint = Color.Gray
                )
            },
            selected = false,
            onClick = onProfile,
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )
    }
}



@Composable
fun CircularIcon(
    resId: Int,
    backgroundColor: Color,
    iconTint: Color
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = resId),
            contentDescription = null,
            tint = iconTint
        )
    }
}

