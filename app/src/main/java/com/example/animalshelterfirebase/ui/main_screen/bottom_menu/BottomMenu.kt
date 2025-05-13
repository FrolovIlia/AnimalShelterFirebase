package com.example.animalshelterfirebase.ui.main_screen.bottom_menu

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.animalshelterfirebase.R
import com.example.animalshelterfirebase.ui.theme.ButtonColorBlue

@Composable
fun BottomMenu(
    selectedTab: BottomMenuItem,
    onTabSelected: (BottomMenuItem) -> Unit,
    isRegistered: Boolean // Добавим параметр для проверки регистрации пользователя
) {
    val context = LocalContext.current

    NavigationBar {
        NavigationBarItem(
            icon = {
                CircularIcon(
                    resId = R.drawable.home_menu,
                    backgroundColor = if (selectedTab == BottomMenuItem.Home) ButtonColorBlue else Color.Transparent,
                    iconTint = Color.Gray
                )
            },
            selected = selectedTab == BottomMenuItem.Home,
            onClick = { onTabSelected(BottomMenuItem.Home) },
            alwaysShowLabel = false,
            colors = NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent
            )
        )

        NavigationBarItem(
            icon = {
                CircularIcon(
                    resId = R.drawable.favourite_menu,
                    backgroundColor = if (selectedTab == BottomMenuItem.Favs) ButtonColorBlue else Color.Transparent,
                    iconTint = Color.Gray
                )
            },
            selected = selectedTab == BottomMenuItem.Favs,
            onClick = {
                if (isRegistered) {
                    // Переход на экран избранных для зарегистрированного пользователя
                    onTabSelected(BottomMenuItem.Favs)
                } else {
                    // Показать уведомление для незарегистрированных пользователей
                    Toast.makeText(context, "Только для зарегистрированных пользователей", Toast.LENGTH_SHORT).show()
                }
            },
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
            selected = selectedTab == BottomMenuItem.Settings,
            onClick = { onTabSelected(BottomMenuItem.Settings) },
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

