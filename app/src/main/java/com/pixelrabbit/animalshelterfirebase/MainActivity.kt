package com.pixelrabbit.animalshelterfirebase

import android.Manifest
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.toRoute
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.pixelrabbit.animalshelterfirebase.data.model.*
import com.pixelrabbit.animalshelterfirebase.ui.add_animal_screen.AddAnimalScreen
import com.pixelrabbit.animalshelterfirebase.ui.add_task_screen.AddTaskScreen
import com.pixelrabbit.animalshelterfirebase.ui.adoption_screen.AdoptionScreen
import com.pixelrabbit.animalshelterfirebase.ui.authorization.LoginScreen
import com.pixelrabbit.animalshelterfirebase.ui.authorization.LoginScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.authorization.UserViewModel
import com.pixelrabbit.animalshelterfirebase.ui.authorization.createEncryptedPrefs
import com.pixelrabbit.animalshelterfirebase.ui.details_animal_screen.AnimalDetailsScreen
import com.pixelrabbit.animalshelterfirebase.ui.donation_screen.DonationScreen
import com.pixelrabbit.animalshelterfirebase.ui.donation_screen.shelter_data.ShelterViewModel
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.MainScreen
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.MainScreenViewModel
import com.pixelrabbit.animalshelterfirebase.ui.navigation.*
import com.pixelrabbit.animalshelterfirebase.ui.profile_screen.EditProfileScreen
import com.pixelrabbit.animalshelterfirebase.ui.registration.RegisterScreen
import com.pixelrabbit.animalshelterfirebase.ui.registration.RegisterScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.slide_show_screen.SlideShowScreen
import com.pixelrabbit.animalshelterfirebase.ui.slide_show_screen.SlideShowScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.start_screen.StartScreen
import com.pixelrabbit.animalshelterfirebase.ui.start_screen.StartScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.task_details_screen.TaskDetailsScreen
import com.pixelrabbit.animalshelterfirebase.ui.tasks_screen.TasksScreen
import com.pixelrabbit.animalshelterfirebase.ui.tasks_screen.TasksViewModel
import com.pixelrabbit.animalshelterfirebase.utils.AppOpenAdManager

class MainActivity : ComponentActivity() {

    private val TAG = "FCM_DEBUG"
    private var currentIntent by mutableStateOf(intent)
    private lateinit var adManager: AppOpenAdManager

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        currentIntent = intent
    }


    override fun onResume() {
        super.onResume()
        adManager.setCurrentActivity(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        adManager = AppOpenAdManager(application, "R-M-16111641-8")

        setContent {
            val navController = rememberNavController()
            val encryptedPrefs = createEncryptedPrefs(this)
            val userViewModel = viewModel<UserViewModel>()
            val mainScreenViewModel: MainScreenViewModel = viewModel()
            val tasksViewModel: TasksViewModel = viewModel()
            val context = LocalContext.current


            val requestPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { isGranted ->
                    if (isGranted) Log.d(TAG, "Notification permission granted.")
                    else Log.w(TAG, "Notification permission denied.")
                }
            )

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                FirebaseMessaging.getInstance().subscribeToTopic("new_animals")
                FirebaseMessaging.getInstance().subscribeToTopic("new_tasks")
            }

            fun processIntent(intent: Intent?) {
                val animalKey = intent?.extras?.getString("animalKey")
                val taskKey = intent?.extras?.getString("taskKey")
                val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"

                if (animalKey != null) {
                    adManager.showAdIfAvailable()
                    val animalRef =
                        FirebaseFirestore.getInstance().collection("animals").document(animalKey)
                    animalRef.get().addOnSuccessListener { doc ->
                        val animal = doc.toObject(Animal::class.java)
                        if (animal != null) {
                            navController.navigate(
                                AnimalDetailsNavObject(
                                    uid = currentUid,
                                    imageUrl = animal.imageUrl,
                                    name = animal.name,
                                    age = animal.age,
                                    category = animal.category,
                                    description = animal.description,
                                    feature = animal.feature,
                                    location = animal.location,
                                    curatorPhone = animal.curatorPhone
                                )
                            )
                        }
                    }
                } else if (taskKey != null) {
                    val taskRef =
                        FirebaseFirestore.getInstance().collection("tasks").document(taskKey)
                    taskRef.get().addOnSuccessListener { doc ->
                        val task = doc.toObject(Task::class.java)
                        if (task != null) {
                            navController.navigate(
                                TaskDetailsNavObject(
                                    uid = task.key ?: "",
                                    imageUrl = task.imageUrl ?: "",
                                    shortDescription = task.shortDescription ?: "",
                                    fullDescription = task.fullDescription ?: "",
                                    curatorName = task.curatorName ?: "",
                                    curatorPhone = task.curatorPhone ?: "",
                                    location = task.location ?: "",
                                    urgency = task.urgency ?: "Низкая",
                                    category = task.category ?: "Общее"
                                )
                            )
                        }
                    }
                }
            }

            LaunchedEffect(currentIntent) {
                processIntent(currentIntent)
            }

            val auth = Firebase.auth
            val startDestination = if (auth.currentUser != null) {
                MainScreenDataObject(
                    uid = auth.currentUser!!.uid,
                    email = auth.currentUser!!.email ?: "email_not_found"
                )
            } else StartScreenObject

            NavHost(navController = navController, startDestination = startDestination) {

                composable<StartScreenObject> {
                    StartScreen(
                        onLoginClick = { navController.navigate(LoginScreenObject) },
                        onRegisterClick = { navController.navigate(RegisterScreenObject) },
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

                composable<LoginScreenObject> {
                    LoginScreen(
                        auth = FirebaseAuth.getInstance(),
                        prefs = encryptedPrefs,
                        onNavigateToMainScreen = { navData ->
                            navController.navigate(navData) {
                                popUpTo(StartScreenObject::class) {
                                    inclusive = true
                                }
                            }
                        },
                        onBackClick = { navController.popBackStack() }
                    )
                }

                composable<MainScreenDataObject> { navEntry ->
                    val navData = navEntry.toRoute<MainScreenDataObject>()
                    MainScreen(
                        navData = navData,
                        navController = navController,
                        viewModel = mainScreenViewModel,
                        userViewModel = userViewModel,
                        onAnimalClick = { animal ->
                            navController.navigate(
                                AnimalDetailsNavObject(
                                    uid = navData.uid,
                                    key = animal.key,
                                    imageUrl = animal.imageUrl,
                                    name = animal.name,
                                    age = animal.age,
                                    category = animal.category,
                                    description = animal.description,
                                    feature = animal.feature,
                                    isFavourite = animal.isFavourite,
                                    location = animal.location,
                                    curatorPhone = animal.curatorPhone
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
                        onAdminClick = { navController.navigate(AddScreenObject()) }
                    )
                }

                composable<AddScreenObject> { navEntry ->
                    val navData = navEntry.toRoute<AddScreenObject>()
                    AddAnimalScreen(navData) { navController.popBackStack() }
                }

                composable<AnimalDetailsNavObject> { navEntry ->
                    val navData = navEntry.toRoute<AnimalDetailsNavObject>()
                    AnimalDetailsScreen(
                        navObject = navData,
                        userViewModel = userViewModel,
                        mainScreenViewModel = mainScreenViewModel,
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
                            navController.navigate(
                                donationNavObject.toRoute()
                            )
                        },
                        savedStateHandle = navEntry.savedStateHandle
                    )
                }

                composable(DonationNavObject.routeWithArgs) {
                    DonationScreen(
                        viewModel = viewModel<ShelterViewModel>(),
                        onBack = { navController.popBackStack() })
                }

                composable<EditProfileNavObject> { navEntry ->
                    val navData = navEntry.toRoute<EditProfileNavObject>()
                    userViewModel.loadUser(navData.uid)
                    EditProfileScreen(
                        userViewModel = userViewModel,
                        onBack = { navController.popBackStack() },
                        onLogout = {
                            navController.navigate(StartScreenObject) {
                                popUpTo(0) {
                                    inclusive = true
                                }
                            }
                        },
                        // 💡 Временная заглушка
                        onOwnerClick = {
                            // Здесь пока ничего не происходит.
                            // Когда страница будет готова, замените этот код на переход:
                            // navController.navigate("название_вашего_экрана")
                        }
                    )
                }

                composable<RegisterScreenObject> {
                    RegisterScreen(
                        auth = FirebaseAuth.getInstance(),
                        onRegistered = { navData ->
                            navController.navigate(navData) {
                                popUpTo(
                                    StartScreenObject::class
                                ) { inclusive = true }
                            }
                        },
                        onBack = { navController.popBackStack() }
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
                        animal = animal, user = user,
                        onBack = { navController.popBackStack() },
                        onSubmitSuccess = {
                            navController.previousBackStackEntry?.savedStateHandle?.set(
                                "showAdoptionSuccess",
                                true
                            )
                            navController.navigateUp()
                        }
                    )
                }

                composable<TaskNavObject> { navEntry ->
                    val navData = navEntry.toRoute<TaskNavObject>()
                    val task = Task(
                        imageUrl = navData.imageUrl,
                        shortDescription = navData.shortDescription,
                        fullDescription = navData.fullDescription,
                        curatorName = navData.curatorName,
                        curatorPhone = navData.curatorPhone,
                        location = navData.location,
                        urgency = navData.urgency,
                        category = navData.category
                    )
                    TasksScreen(
                        task = task,
                        onSubmitSuccess = { navController.navigateUp() },
                        onAddTaskClick = { navController.navigate(AddTaskNavObject) },
                        onTaskEditClick = { taskId -> navController.navigate("edit_task_screen/$taskId") },
                        viewModel = mainScreenViewModel,
                        userViewModel = userViewModel,
                        tasksViewModel = tasksViewModel,
                        navData = navData,
                        navController = navController
                    )
                }

                composable(
                    "edit_task_screen/{taskId}",
                    arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                ) { navBackStackEntry ->
                    val taskId = navBackStackEntry.arguments?.getString("taskId")
                    AddTaskScreen(taskKey = taskId, onSaved = { navController.popBackStack() })
                }

                composable<AddTaskNavObject> {
                    AddTaskScreen(onSaved = { navController.popBackStack() })
                }

                composable<TaskDetailsNavObject> { navEntry ->
                    val navData = navEntry.toRoute<TaskDetailsNavObject>()
                    TaskDetailsScreen(
                        navObject = navData,
                        onBackClick = { navController.popBackStack() })
                }

                composable<SlideShowScreenObject> {
                    val animals by mainScreenViewModel.animals.collectAsState()
                    SlideShowScreen(
                        animals = animals,
                        onBackClick = { navController.popBackStack() })
                }
            }
        }
    }
}
