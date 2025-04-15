package com.example.animalshelterfirebase


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.animalshelterfirebase.data.MainScreenDataObject
import com.example.animalshelterfirebase.ui.login.LoginScreen
import com.example.animalshelterfirebase.ui.login.LoginScreenObject
import com.example.animalshelterfirebase.ui.main_screen.MainScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController = navController, startDestination = LoginScreenObject) {

                composable<LoginScreenObject> {
                    LoginScreen { navData->
                        navController.navigate(navData)
                    }
                }

                composable<MainScreenDataObject> { navEntry->
                    val navData = navEntry.toRoute<MainScreenDataObject>()
                    MainScreen(navData)
                }

            }
        }
    }
}