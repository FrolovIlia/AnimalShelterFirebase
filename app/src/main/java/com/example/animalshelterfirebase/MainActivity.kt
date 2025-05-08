package com.example.animalshelterfirebase


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.animalshelterfirebase.data.MainScreenDataObject
import com.example.animalshelterfirebase.ui.add_animal_screen.AddAnimalScreen
import com.example.animalshelterfirebase.ui.data.AddScreenObject
import com.example.animalshelterfirebase.ui.login.LoginScreen
import com.example.animalshelterfirebase.ui.login.LoginScreenObject
import com.example.animalshelterfirebase.ui.main_screen.MainScreen
import com.example.animalshelterfirebase.ui.registration.RegisterScreen
import com.example.animalshelterfirebase.ui.registration.RegisterScreenObject


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = LoginScreenObject
            ) {

                composable<LoginScreenObject> {
                    LoginScreen { navData ->
                        navController.navigate(navData)
                    }
                }

                composable<MainScreenDataObject> { navEntry ->
                    val navData = navEntry.toRoute<MainScreenDataObject>()
                    MainScreen(
                        navData,
                        onAnimalEditClick = { animal ->
                            navController.navigate(
                                AddScreenObject(
                                    key = animal.key,
                                    name = animal.name,
                                    description = animal.description,
                                    age = animal.age,
                                    category = animal.category,
                                    imageUrl = animal.imageUrl
                                )
                            )
                        }
                    ) {
                        navController.navigate(AddScreenObject())
                    }
                }

                composable<AddScreenObject> { navEntry ->
                    val navData = navEntry.toRoute<AddScreenObject>()
                    AddAnimalScreen(
                        navData = navData,
                        onSaved = {
                            navController.popBackStack() // возвращение назад
                        }
                    )
                }

                composable<RegisterScreenObject> {
                    RegisterScreen(
                        onRegistered = { navData ->
                            navController.navigate(navData) // переходит в MainScreenDataObject
                        },
                        onBack = {
                            navController.popBackStack() // вернуться на экран логина
                        }
                    )
                }

            }
        }
    }
}