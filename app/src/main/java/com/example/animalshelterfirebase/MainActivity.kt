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
import com.example.animalshelterfirebase.ui.authorization.LoginScreen
import com.example.animalshelterfirebase.ui.data.AddScreenObject
import com.example.animalshelterfirebase.ui.details_screen.data.DetailsNavObject
import com.example.animalshelterfirebase.ui.details_screen.ui.DetailsScreen

import com.example.animalshelterfirebase.ui.main_screen.MainScreen
import com.example.animalshelterfirebase.ui.registration.ui.RegisterScreen
import com.example.animalshelterfirebase.ui.registration.RegisterScreenObject
import com.example.animalshelterfirebase.ui.start_screen.ui.StartScreen
import com.example.animalshelterfirebase.ui.start_screen.data.StartScreenObject
import com.google.firebase.auth.FirebaseAuth
import androidx.core.view.WindowCompat
import com.example.animalshelterfirebase.data.Animal
import com.example.animalshelterfirebase.ui.adoption_screen.AdoptionNavObject
import com.example.animalshelterfirebase.ui.adoption_screen.AdoptionScreen
import com.example.animalshelterfirebase.ui.authorization.LoginScreenObject
import com.example.animalshelterfirebase.ui.authorization.createEncryptedPrefs

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val navController = rememberNavController()
            val encryptedPrefs = createEncryptedPrefs(this)

            NavHost(
                navController = navController,
                startDestination = StartScreenObject
            ) {

                composable<LoginScreenObject> {
                    LoginScreen(
                        auth = FirebaseAuth.getInstance(),
                        prefs = encryptedPrefs,
                        onNavigateToMainScreen = { navData ->
                            navController.navigate(navData)
                        },
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }

                composable<StartScreenObject> {
                    StartScreen(
                        onLoginClick = {
                            navController.navigate(LoginScreenObject)
                        },
                        onRegisterClick = {
                            navController.navigate(RegisterScreenObject)
                        },
                        onGuestClick = {
                            navController.navigate(
                                MainScreenDataObject(
                                    uid = "guest",
                                    email = "guest@anonymous.com"
                                )
                            )
                        }
                    )
                }

                composable<MainScreenDataObject> { navEntry ->
                    val navData = navEntry.toRoute<MainScreenDataObject>()
                    MainScreen(
                        navData,
                        onAnimalClick = { anim ->
                            navController.navigate(
                                DetailsNavObject(
                                    uid = navData.uid,
                                    imageUrl = anim.imageUrl,
                                    name = anim.name,
                                    age = anim.age,
                                    category = anim.category,
                                    description = anim.description,
                                    feature = anim.feature,
                                    location = anim.location,
                                    curatorPhone = anim.curatorPhone
                                )
                            )
                        },
                        onAnimalEditClick = { animal ->
                            navController.navigate(
                                AddScreenObject(
                                    key = animal.key,
                                    name = animal.name,
                                    descriptionShort = animal.description,
                                    age = animal.age,
                                    category = animal.category,
                                    imageUrl = animal.imageUrl,
                                    feature = animal.feature,
                                    location = animal.location,
                                    curatorPhone = animal.curatorPhone
                                )
                            )
                        }
                    ) {
                        navController.navigate(AddScreenObject())
                    }
                }

                composable<AddScreenObject> { navEntry ->
                    val navData = navEntry.toRoute<AddScreenObject>()
                    AddAnimalScreen(navData) {
                        navController.popBackStack()
                    }

                }

                composable<DetailsNavObject> { navEntry ->
                    val navData = navEntry.toRoute<DetailsNavObject>()
                    DetailsScreen(
                        navObject = navData,
                        onBackClick = { navController.popBackStack() },
                        onAdoptClick = { animal ->
                            navController.navigate(
                                AdoptionNavObject(
                                    name = animal.name,
                                    age = animal.age,
                                    curatorPhone = animal.curatorPhone,
                                    location = animal.location,
                                    description = animal.description
                                )
                            )
                        },
                        savedStateHandle = navEntry.savedStateHandle
                    )
                }


                composable<RegisterScreenObject> {
                    RegisterScreen(
                        auth = FirebaseAuth.getInstance(),
                        onRegistered = { navData ->
                            navController.navigate(navData)
                        },
                        onBack = {
                            navController.popBackStack()
                        }
                    )
                }


                composable<AdoptionNavObject> { navEntry ->
                    val navData = navEntry.toRoute<AdoptionNavObject>()
                    AdoptionScreen(
                        animal = Animal(
                            name = navData.name,
                            age = navData.age,
                            curatorPhone = navData.curatorPhone,
                            location = navData.location,
                            description = navData.description
                        ),
                        onBack = { navController.popBackStack() },
                        onSubmitSuccess = {
                            // Передаем флаг в предыдущий экран
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("showAdoptionSuccess", true)
                            // Корректный возврат на предыдущий экран (DetailsScreen)
                            navController.navigateUp()
                        }
                    )
                }



            }
        }
    }
}