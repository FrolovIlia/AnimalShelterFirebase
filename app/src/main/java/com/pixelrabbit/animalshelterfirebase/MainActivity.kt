package com.pixelrabbit.animalshelterfirebase

import android.Manifest
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.pixelrabbit.animalshelterfirebase.data.model.Animal
import com.pixelrabbit.animalshelterfirebase.data.model.MainScreenDataObject
import com.pixelrabbit.animalshelterfirebase.data.model.Task
import com.pixelrabbit.animalshelterfirebase.data.model.UserObject
import com.pixelrabbit.animalshelterfirebase.ui.donation_screen.shelter_data.ShelterViewModel
import com.pixelrabbit.animalshelterfirebase.ui.add_animal_screen.AddAnimalScreen
import com.pixelrabbit.animalshelterfirebase.ui.navigation.AddTaskNavObject
import com.pixelrabbit.animalshelterfirebase.ui.add_task_screen.AddTaskScreen
import com.pixelrabbit.animalshelterfirebase.ui.navigation.AdoptionNavObject
import com.pixelrabbit.animalshelterfirebase.ui.adoption_screen.AdoptionScreen
import com.pixelrabbit.animalshelterfirebase.ui.authorization.LoginScreen
import com.pixelrabbit.animalshelterfirebase.ui.authorization.LoginScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.authorization.UserViewModel
import com.pixelrabbit.animalshelterfirebase.ui.authorization.createEncryptedPrefs
import com.pixelrabbit.animalshelterfirebase.data.model.AddScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.navigation.AnimalDetailsNavObject
import com.pixelrabbit.animalshelterfirebase.ui.details_animal_screen.AnimalDetailsScreen
import com.pixelrabbit.animalshelterfirebase.ui.navigation.TaskDetailsNavObject
import com.pixelrabbit.animalshelterfirebase.ui.navigation.DonationNavObject
import com.pixelrabbit.animalshelterfirebase.ui.donation_screen.DonationScreen
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.MainScreen
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.MainScreenViewModel
import com.pixelrabbit.animalshelterfirebase.ui.navigation.EditProfileNavObject
import com.pixelrabbit.animalshelterfirebase.ui.profile_screen.EditProfileScreen
import com.pixelrabbit.animalshelterfirebase.ui.registration.RegisterScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.registration.RegisterScreen
import com.pixelrabbit.animalshelterfirebase.ui.start_screen.StartScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.start_screen.StartScreen
import com.pixelrabbit.animalshelterfirebase.ui.task_details_screen.TaskDetailsScreen
import com.pixelrabbit.animalshelterfirebase.ui.navigation.TaskNavObject
import com.pixelrabbit.animalshelterfirebase.ui.tasks_screen.TasksScreen
import com.yandex.mobile.ads.common.InitializationListener
import com.yandex.mobile.ads.common.MobileAds
import androidx.compose.runtime.*
import com.pixelrabbit.animalshelterfirebase.ui.slide_show_screen.SlideShowScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.slide_show_screen.SlideShowScreen
import com.pixelrabbit.animalshelterfirebase.ui.tasks_screen.TasksViewModel

class MainActivity : ComponentActivity() {
    private val TAG = "FCM_DEBUG"

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this, initializationListener = object : InitializationListener {
            override fun onInitializationCompleted() {}
        })
        Log.d("YandexAds", "SDK initialized")

        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val navController = rememberNavController()
            val encryptedPrefs = createEncryptedPrefs(this)
            val userViewModel = viewModel<UserViewModel>()
            val mainScreenViewModel: MainScreenViewModel = viewModel()
            val tasksViewModel: TasksViewModel = viewModel()

            // --- Блок настройки FCM и запроса разрешения ---
            // Объявляем лаунчер для запроса разрешения на уведомления
            val requestPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    if (isGranted) {
                        Log.d(TAG, "Notification permission granted.")
                    } else {
                        Log.w(TAG, "Notification permission denied.")
                    }
                }
            )

            // Используем LaunchedEffect для выполнения действий, зависимых от жизненного цикла
            LaunchedEffect(Unit) {
                Log.d(TAG, "MainActivity LaunchedEffect called. Starting FCM setup.")

                // Запрашиваем разрешение на уведомления для Android 13+
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                // Подписываемся на топик "new_animals"
                FirebaseMessaging.getInstance().subscribeToTopic("new_animals")
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.e(TAG, "FCM Topic Subscription FAILED: ${task.exception?.message}", task.exception)
                            return@addOnCompleteListener
                        }
                        Log.d(TAG, "Successfully subscribed to topic 'new_animals'.")
                    }

                // Получаем токен устройства (независимо от подписки на топик)
                FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                    if (!tokenTask.isSuccessful) {
                        Log.e(TAG, "Fetching FCM registration token failed", tokenTask.exception)
                        return@addOnCompleteListener
                    }
                    val token = tokenTask.result
                    Log.d(TAG, "FCM Token obtained: $token")
                }
            }
            Log.d(TAG, "FirebaseMessaging.getInstance().token and subscribeToTopic initiated.")
            // --- Конец блока настройки FCM ---


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

                    // При смене uid - обновляем единственный источник пользователя/админа
                    LaunchedEffect(navData.uid) {
                        if (navData.uid != "guest") {
                            userViewModel.loadUser(navData.uid)
                        } else {
                            userViewModel.clearUser()
                        }
                    }

                    MainScreen(
                        navData = navData,
                        navController = navController,
                        viewModel = mainScreenViewModel,
                        userViewModel = userViewModel,
                        onAnimalClick = { anim ->
                            navController.navigate(
                                AnimalDetailsNavObject(
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
                        },
                        onAdminClick = {
                            navController.navigate(AddScreenObject())
                        }
                    )
                }

                composable<AddScreenObject> { navEntry ->
                    val navData = navEntry.toRoute<AddScreenObject>()
                    AddAnimalScreen(navData) {
                        navController.popBackStack()
                    }
                }

                composable<AnimalDetailsNavObject> { navEntry ->
                    val navData = navEntry.toRoute<AnimalDetailsNavObject>()
                    AnimalDetailsScreen(
                        navObject = navData,
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

                composable<EditProfileNavObject> { navEntry ->
                    val navData = navEntry.toRoute<EditProfileNavObject>()
                    // Для профиля всегда берем UserViewModel
                    userViewModel.loadUser(navData.uid)
                    EditProfileScreen(
                        userViewModel = userViewModel,
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

                composable<TaskNavObject> { navEntry ->
                    val navData = navEntry.toRoute<TaskNavObject>()

                    // Не нужна отдельная проверка admin, только подписка на userViewModel!
                    val task = Task(
                        imageUrl = navData.imageUrl,
                        shortDescription = navData.shortDescription,
                        fullDescription = navData.fullDescription,
                        curatorName = navData.curatorName,
                        curatorPhone = navData.curatorPhone,
                        location = navData.location,
                        urgency = navData.urgency,
                        category = navData.category,
                    )

                    TasksScreen(
                        task = task,
                        onSubmitSuccess = {
                            navController.previousBackStackEntry
                                ?.savedStateHandle
                                ?.set("showAdoptionSuccess", true)
                            navController.navigateUp()
                        },
                        onAddTaskClick = {
                            navController.navigate(AddTaskNavObject)
                        },
                        viewModel = mainScreenViewModel,
                        userViewModel = userViewModel,
                        tasksViewModel = tasksViewModel,
                        navData = navData,
                        navController = navController
                    )
                }

                composable<AddTaskNavObject> {
                    AddTaskScreen(
                        onSaved = {
                            navController.popBackStack()
                        }
                    )
                }

                composable("edit_task_screen/{taskId}") { backStackEntry ->
                    val taskId = backStackEntry.arguments?.getString("taskId") ?: ""
                    val firestore = FirebaseFirestore.getInstance()
                    var task by remember { mutableStateOf<Task?>(null) }

                    LaunchedEffect(taskId) {
                        firestore.collection("tasks").document(taskId).get()
                            .addOnSuccessListener { doc ->
                                task = doc.toObject(Task::class.java)?.copy(key = doc.id)
                            }
                    }

                    task?.let {
                        AddTaskScreen(taskData = it) {
                            navController.popBackStack()
                        }
                    }
                }

                composable<TaskDetailsNavObject> { navEntry ->
                    val navData = navEntry.toRoute<TaskDetailsNavObject>()
                    TaskDetailsScreen(
                        navObject = navData,
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable<SlideShowScreenObject> {
                    val animals by mainScreenViewModel.animals.collectAsState()
                    SlideShowScreen(
                        animals = animals,
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }


            }
        }
    }
}
