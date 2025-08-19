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
import androidx.compose.runtime.remember
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
import com.pixelrabbit.animalshelterfirebase.data.model.AddScreenObject
import com.pixelrabbit.animalshelterfirebase.data.model.Animal
import com.pixelrabbit.animalshelterfirebase.data.model.MainScreenDataObject
import com.pixelrabbit.animalshelterfirebase.data.model.Task
import com.pixelrabbit.animalshelterfirebase.data.model.UserObject
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
import com.pixelrabbit.animalshelterfirebase.ui.navigation.AdoptionNavObject
import com.pixelrabbit.animalshelterfirebase.ui.navigation.AddTaskNavObject
import com.pixelrabbit.animalshelterfirebase.ui.navigation.AnimalDetailsNavObject
import com.pixelrabbit.animalshelterfirebase.ui.navigation.DonationNavObject
import com.pixelrabbit.animalshelterfirebase.ui.navigation.EditProfileNavObject
import com.pixelrabbit.animalshelterfirebase.ui.navigation.TaskDetailsNavObject
import com.pixelrabbit.animalshelterfirebase.ui.navigation.TaskNavObject
import com.pixelrabbit.animalshelterfirebase.ui.profile_screen.EditProfileScreen
import com.pixelrabbit.animalshelterfirebase.ui.registration.RegisterScreen
import com.pixelrabbit.animalshelterfirebase.ui.registration.RegisterScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.slide_show_screen.SlideShowScreen
import com.pixelrabbit.animalshelterfirebase.ui.slide_show_screen.SlideShowScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.start_screen.StartScreen
import com.pixelrabbit.animalshelterfirebase.ui.start_screen.StartScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.task_details_screen.TaskDetailsScreen
import com.pixelrabbit.animalshelterfirebase.ui.tasks_screen.TasksScreen
import com.yandex.mobile.ads.common.InitializationListener
import com.yandex.mobile.ads.common.MobileAds
import com.pixelrabbit.animalshelterfirebase.ui.tasks_screen.TasksViewModel

class MainActivity : ComponentActivity() {
    private val TAG = "FCM_DEBUG"
    private var currentIntent by mutableStateOf(intent)

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        currentIntent = intent
    }

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
            val context = LocalContext.current

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

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }

                FirebaseMessaging.getInstance().subscribeToTopic("new_animals")
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.e(TAG, "FCM Topic Subscription FAILED: ${task.exception?.message}", task.exception)
                        } else {
                            Log.d(TAG, "Successfully subscribed to topic 'new_animals'.")
                        }
                    }

                FirebaseMessaging.getInstance().subscribeToTopic("new_tasks")
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Log.e(TAG, "FCM Topic Subscription FAILED: ${task.exception?.message}", task.exception)
                        } else {
                            Log.d(TAG, "Successfully subscribed to topic 'new_tasks'.")
                        }
                    }

                FirebaseMessaging.getInstance().token.addOnCompleteListener { tokenTask ->
                    if (tokenTask.isSuccessful) {
                        val token = tokenTask.result
                        Log.d(TAG, "FCM Token obtained: $token")
                    } else {
                        Log.e(TAG, "Fetching FCM registration token failed", tokenTask.exception)
                    }
                }
            }

            fun processIntent(intent: Intent?) {
                val animalKeyFromIntent = intent?.extras?.getString("animalKey")
                val taskKeyFromIntent = intent?.extras?.getString("taskKey")
                val currentUid = FirebaseAuth.getInstance().currentUser?.uid ?: "guest"
                val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email ?: "guest@anonymous.com"

                if (animalKeyFromIntent != null) {
                    Log.d(TAG, "processIntent: Found animalKeyFromIntent = $animalKeyFromIntent")
                    val animalRef = FirebaseFirestore.getInstance().collection("animals").document(animalKeyFromIntent)
                    animalRef.get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            val animal = document.toObject(Animal::class.java)
                            if (animal != null) {
                                val mainScreenNavObject = MainScreenDataObject(
                                    uid = currentUid,
                                    email = currentUserEmail
                                )
                                navController.navigate(mainScreenNavObject) {
                                    popUpTo(0) { inclusive = true }
                                }
                                val navObject = AnimalDetailsNavObject(
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
                                navController.navigate(navObject)
                            } else {
                                Log.e(TAG, "Failed to parse Animal object from document.")
                            }
                        } else {
                            Log.e(TAG, "Document with animalKey $animalKeyFromIntent does not exist.")
                        }
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Error fetching animal document: ${e.message}", e)
                    }
                } else if (taskKeyFromIntent != null) {
                    Log.d(TAG, "processIntent: Found taskKeyFromIntent = $taskKeyFromIntent")
                    val taskRef = FirebaseFirestore.getInstance().collection("tasks").document(taskKeyFromIntent)
                    taskRef.get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            val task = document.toObject(Task::class.java)
                            if (task != null) {
                                val mainScreenNavObject = MainScreenDataObject(
                                    uid = currentUid,
                                    email = currentUserEmail
                                )
                                navController.navigate(mainScreenNavObject) {
                                    popUpTo(0) { inclusive = true }
                                }
                                val taskNavObject = TaskNavObject(
                                    uid = currentUid,
                                    imageUrl = "",
                                    shortDescription = "",
                                    fullDescription = "",
                                    curatorName = "",
                                    curatorPhone = "",
                                    location = "",
                                    urgency = "Низкая",
                                    category = "Общее"
                                )
                                navController.navigate(taskNavObject)
                                val navObject = TaskDetailsNavObject(
                                    imageUrl = task.imageUrl ?: "",
                                    shortDescription = task.shortDescription ?: "",
                                    fullDescription = task.fullDescription ?: "",
                                    curatorName = task.curatorName ?: "",
                                    curatorPhone = task.curatorPhone ?: "",
                                    location = task.location ?: "",
                                    urgency = task.urgency ?: "Низкая",
                                    category = task.category ?: "Общее",
                                    uid = task.key ?: ""
                                )
                                navController.navigate(navObject)
                            } else {
                                Log.e(TAG, "Failed to parse Task object from document.")
                            }
                        } else {
                            Log.e(TAG, "Document with taskKey $taskKeyFromIntent does not exist.")
                        }
                    }.addOnFailureListener { e ->
                        Log.e(TAG, "Error fetching task document: ${e.message}", e)
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
            } else {
                StartScreenObject
            }

            NavHost(
                navController = navController,
                startDestination = startDestination
            ) {

                composable<LoginScreenObject> {
                    LoginScreen(
                        auth = FirebaseAuth.getInstance(),
                        prefs = encryptedPrefs,
                        onNavigateToMainScreen = { navData ->
                            navController.navigate(navData) {
                                popUpTo(StartScreenObject::class) { inclusive = true }
                            }
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
                    arguments = listOf()
                ) { navBackStackEntry ->
                    val shelterViewModel: ShelterViewModel = viewModel()
                    DonationScreen(
                        viewModel = shelterViewModel,
                        onBack = { navController.popBackStack() }
                    )
                }

                composable<EditProfileNavObject> { navEntry ->
                    val navData = navEntry.toRoute<EditProfileNavObject>()
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
                            navController.navigate(navData) {
                                popUpTo(StartScreenObject::class) { inclusive = true }
                            }
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
                        onTaskEditClick = { taskId ->
                            navController.navigate("edit_task_screen/${taskId}")
                        },
                        viewModel = mainScreenViewModel,
                        userViewModel = userViewModel,
                        tasksViewModel = tasksViewModel,
                        navData = navData,
                        navController = navController
                    )
                }

                composable(
                    route = "edit_task_screen/{taskId}",
                    arguments = listOf(navArgument("taskId") { type = NavType.StringType })
                ) { navBackStackEntry ->
                    val taskId = navBackStackEntry.arguments?.getString("taskId")
                    AddTaskScreen(
                        taskKey = taskId,
                        onSaved = {
                            navController.popBackStack()
                        }
                    )
                }

                composable<AddTaskNavObject> {
                    AddTaskScreen(
                        onSaved = {
                            navController.popBackStack()
                        }
                    )
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