package com.pixelrabbit.animalshelterfirebase

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.pixelrabbit.animalshelterfirebase.data.MainScreenDataObject
import com.pixelrabbit.animalshelterfirebase.ui.add_animal_screen.AddAnimalScreen
import com.pixelrabbit.animalshelterfirebase.ui.authorization.LoginScreen
import com.pixelrabbit.animalshelterfirebase.ui.data.AddScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.details_screen.data.DetailsNavObject
import com.pixelrabbit.animalshelterfirebase.ui.details_screen.ui.DetailsScreen

import com.pixelrabbit.animalshelterfirebase.ui.main_screen.MainScreen
import com.pixelrabbit.animalshelterfirebase.ui.registration.ui.RegisterScreen
import com.pixelrabbit.animalshelterfirebase.ui.registration.RegisterScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.start_screen.ui.StartScreen
import com.pixelrabbit.animalshelterfirebase.ui.start_screen.data.StartScreenObject
import com.google.firebase.auth.FirebaseAuth
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixelrabbit.animalshelterfirebase.data.Animal
import com.pixelrabbit.animalshelterfirebase.data.UserObject
import com.pixelrabbit.animalshelterfirebase.ui.adoption_screen.AdoptionNavObject
import com.pixelrabbit.animalshelterfirebase.ui.adoption_screen.AdoptionScreen
import com.pixelrabbit.animalshelterfirebase.ui.authorization.LoginScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.authorization.UserViewModel
import com.pixelrabbit.animalshelterfirebase.ui.authorization.createEncryptedPrefs

import com.pixelrabbit.animalshelterfirebase.ui.donation_screen.data.DonationNavObject
import com.pixelrabbit.animalshelterfirebase.ui.donation_screen.ui.DonationScreen


import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import android.widget.Toast

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.pixelrabbit.animalshelterfirebase.model.ShelterViewModel

class MainActivity : ComponentActivity() {
    private val TAG = "FCM_DEBUG" // TAG определен здесь, чтобы был доступен во всем классе

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- НАЧАЛО: Блок настройки FCM. ЗАМЕНИТЬ ЭТОТ БЛОК ПОЛНОСТЬЮ! ---
        Log.d(TAG, "MainActivity onCreate called. Starting FCM setup.")

        // Для Android 13+ запросите разрешение на уведомления (если не сделано в другом месте)
        // Если у вас есть функция askNotificationPermission(), ее можно раскомментировать
        // askNotificationPermission()
        // Log.d(TAG, "askNotificationPermission called (if implemented).")


        FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
            if (!tokenTask.isSuccessful) {
                Log.e(TAG, "Fetching FCM registration token failed", tokenTask.exception)
                return@addOnCompleteListener
            }

            // Получить FCM registration token
            val token = tokenTask.result
            Log.d(TAG, "FCM Token obtained: $token")

            // --- Блок отписки, а затем подписки на тему "new_animals" ---
            Log.d(TAG, "Attempting to unsubscribe from topic 'new_animals' before re-subscribing.")
            FirebaseMessaging.getInstance().unsubscribeFromTopic("new_animals")
                .addOnCompleteListener { unsubscribeTask ->
                    if (unsubscribeTask.isSuccessful) {
                        Log.d(
                            TAG,
                            "Successfully unsubscribed from new_animals topic (if previously subscribed)."
                        )
                    } else {
                        Log.e(
                            TAG,
                            "Failed to unsubscribe from new_animals topic: ${unsubscribeTask.exception?.message}",
                            unsubscribeTask.exception
                        )
                    }

                    // Теперь, после попытки отписки, выполним подписку
                    FirebaseMessaging.getInstance().subscribeToTopic("new_animals")
                        .addOnCompleteListener { subscribeTask ->
                            var msg = "Subscribed to new_animals topic"
                            if (!subscribeTask.isSuccessful) {
                                // Если подписка НЕ успешна
                                msg =
                                    "FCM Topic Subscription FAILED: ${subscribeTask.exception?.message}"
                                Log.e(TAG, msg, subscribeTask.exception)
                                // Выводим более заметный Toast для ошибки
//                                Toast.makeText(baseContext, msg, Toast.LENGTH_LONG).show()
                            } else {
                                // Если подписка успешна
//                                Log.d(TAG, msg)
//                                Toast.makeText(
//                                    baseContext,
//                                    "FCM Topic Subscribed: new_animals",
//                                    Toast.LENGTH_SHORT
//                                ).show()
                            }
                        }
                }
        }
        Log.d(TAG, "FirebaseMessaging.getInstance().token initiated.")
        // --- КОНЕЦ: Блок настройки FCM. ---


        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val navController = rememberNavController()
            val encryptedPrefs = createEncryptedPrefs(this)
            val userViewModel = viewModel<UserViewModel>()

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
                    userViewModel.loadUser(navData.uid)
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

                    val user = userViewModel.currentUser.value
                    val currentUser = user ?: UserObject( // если пользователь не загружен
                        uid = "guest",
                        name = "Гость",
                        phone = "",
                        email = ""
                    )

                    DetailsScreen(
                        navObject = navData,
                        currentUser = currentUser,
                        userViewModel = userViewModel,
                        onBackClick = { navController.popBackStack() },
                        onAdoptClick = { animal, user ->
                            navController.navigate(
                                AdoptionNavObject(
                                    name = animal.name,
                                    age = animal.age,
                                    curatorPhone = animal.curatorPhone,
                                    location = animal.location,
                                    description = animal.description,

                                    userUid = user.uid,
                                    userName = user.name,
                                    userPhone = user.phone,
                                    userEmail = user.email
                                )
                            )
                        },
                        onDonateClick = { donationNavObject ->
                            navController.navigate(donationNavObject.toRoute())
                        },
                        savedStateHandle = navEntry.savedStateHandle
                    )
                }


                composable(
                    route = DonationNavObject.routeWithArgs,
                    arguments = listOf(navArgument("donation") {
                        type = NavType.StringType
                        defaultValue = ""
                    })
                ) { navBackStackEntry ->
                    val shelterViewModel: ShelterViewModel = viewModel()
                    DonationScreen(
                        viewModel = shelterViewModel,
                        onBack = { navController.popBackStack() }
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

                    val animal = Animal(
                        name = navData.name,
                        age = navData.age,
                        curatorPhone = navData.curatorPhone,
                        location = navData.location,
                        description = navData.description
                    )

                    val user = UserObject(
                        uid = navData.userUid,
                        name = navData.userName,
                        phone = navData.userPhone,
                        email = navData.userEmail
                    )

                    AdoptionScreen(
                        animal = animal,
                        user = user,
                        onBack = { navController.popBackStack() },
                        onSubmitSuccess = {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("showAdoptionSuccess", true)
                            navController.navigateUp()
                        }
                    )
                }
            }
        }
    }
}