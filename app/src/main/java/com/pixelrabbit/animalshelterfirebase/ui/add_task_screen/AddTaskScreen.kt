package com.pixelrabbit.animalshelterfirebase.ui.add_task_screen

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pixelrabbit.animalshelterfirebase.data.model.Task
import com.pixelrabbit.animalshelterfirebase.common.RoundedCornerDropDownMenu
import com.pixelrabbit.animalshelterfirebase.common.RoundedCornerTextField
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundGray
import com.pixelrabbit.animalshelterfirebase.utils.AnimalImage
import com.pixelrabbit.animalshelterfirebase.utils.ButtonWhite
import com.pixelrabbit.animalshelterfirebase.utils.PhoneNumberField
import java.io.File

@Composable
fun AddTaskScreen(
    taskKey: String? = null,
    onSaved: () -> Unit = {}
) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val storage = Firebase.storage

    // Локальное состояние для данных формы, которые будут отображаться
    var imageUrl by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var shortDescription by remember { mutableStateOf("") }
    var fullDescription by remember { mutableStateOf("") }
    var curatorName by remember { mutableStateOf("") }
    var curatorPhone by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var urgency by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var curatorPhoneError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val isEditMode = taskKey?.isNotBlank() == true

    val taskCategoryOptions = listOf("С животными", "По хозяйству", "Орг. вопросы")
    val urgencyOptions = listOf("Низкая", "Средняя", "Высокая", "Критическая")

    // Логика для загрузки данных задачи при наличии taskKey
    LaunchedEffect(taskKey) {
        if (isEditMode) {
            isLoading = true
            val docRef = firestore.collection("tasks").document(taskKey!!)
            docRef.get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val task = document.toObject(Task::class.java)
                        if (task != null) {
                            // Логируем загруженные данные для отладки
                            Log.d("AddTaskScreen", "Loaded task data from Firestore:")
                            Log.d("AddTaskScreen", "  imageUrl: ${task.imageUrl}")
                            Log.d("AddTaskScreen", "  shortDescription: ${task.shortDescription}")
                            Log.d("AddTaskScreen", "  fullDescription: ${task.fullDescription}")
                            Log.d("AddTaskScreen", "  curatorName: ${task.curatorName}")
                            Log.d("AddTaskScreen", "  curatorPhone: ${task.curatorPhone}")
                            Log.d("AddTaskScreen", "  location: ${task.location}")
                            Log.d("AddTaskScreen", "  urgency: ${task.urgency}")
                            Log.d("AddTaskScreen", "  category: ${task.category}")

                            // Заполняем поля формы полученными данными, используя null-safe оператор
                            imageUrl = task.imageUrl ?: ""
                            shortDescription = task.shortDescription ?: ""
                            fullDescription = task.fullDescription ?: ""
                            curatorName = task.curatorName ?: ""
                            curatorPhone = task.curatorPhone ?: ""
                            location = task.location ?: ""
                            urgency = task.urgency ?: ""
                            category = task.category ?: ""
                        }
                    } else {
                        Log.e("AddTaskScreen", "Document with key $taskKey does not exist.")
                    }
                    isLoading = false
                }
                .addOnFailureListener { e ->
                    isLoading = false
                    Toast.makeText(context, "Ошибка загрузки задачи", Toast.LENGTH_SHORT).show()
                    Log.e("AddTaskScreen", "Error loading task: ${e.message}", e)
                    onSaved()
                }
        }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            imageUrl = ""
        }
    }

    val photoFile = remember { mutableStateOf<File?>(null) }
    val photoUri = remember { mutableStateOf<Uri?>(null) }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            Toast.makeText(context, "Фото добавлено", Toast.LENGTH_SHORT).show()
            imageUri = photoUri.value
            imageUrl = ""
        } else {
            Toast.makeText(context, "Не удалось сделать снимок", Toast.LENGTH_SHORT).show()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            launchCamera(context, photoFile, photoUri, cameraLauncher)
        } else {
            Toast.makeText(context, "Камера недоступна", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isEditMode) "Редактирование задачи" else "Добавление новой задачи",
                    color = Color.Black,
                    fontFamily = AnimalFont,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
                    textAlign = TextAlign.Center
                )

                TaskImageWithUrgencyBadge(
                    imageUri = imageUri,
                    imageUrl = imageUrl,
                    urgency = urgency,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    RoundedCornerDropDownMenu(
                        defValue = category,
                        options = taskCategoryOptions,
                        placeholder = "Категория задачи"
                    ) { category = it }

                    Spacer(modifier = Modifier.height(8.dp))

                    RoundedCornerDropDownMenu(
                        defValue = urgency,
                        options = urgencyOptions,
                        placeholder = "Срочность"
                    ) { urgency = it }

                    Spacer(modifier = Modifier.height(8.dp))

                    RoundedCornerTextField(text = shortDescription, label = "Краткое описание") {
                        shortDescription = it
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    RoundedCornerTextField(
                        text = fullDescription,
                        label = "Полное описание",
                        singleLine = false,
                        maxLines = Int.MAX_VALUE
                    ) { fullDescription = it }
                    Spacer(modifier = Modifier.height(8.dp))

                    RoundedCornerTextField(text = location, label = "Расположение") {
                        location = it
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    RoundedCornerTextField(text = curatorName, label = "Имя куратора") {
                        curatorName = it
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    PhoneNumberField(
                        value = curatorPhone,
                        onValueChange = {
                            curatorPhone = it
                            curatorPhoneError = !isPhoneValid(it)
                        },
                        isError = curatorPhoneError,
                        label = "Номер куратора"
                    )

                    if (curatorPhoneError) {
                        Text(
                            text = "Неверный номер (формат: +7XXXXXXXXXX)",
                            color = Color.Red,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        ButtonWhite(text = "+ Файл") {
                            imagePickerLauncher.launch("image/*")
                        }

                        ButtonWhite(text = "+ Снимок") {
                            if (androidx.core.content.ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.CAMERA
                                ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                            ) {
                                launchCamera(context, photoFile, photoUri, cameraLauncher)
                            } else {
                                permissionLauncher.launch(android.Manifest.permission.CAMERA)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    ButtonWhite(text = if (isEditMode) "Сохранить изменения" else "Сохранить") {
                        curatorPhoneError = !isPhoneValid(curatorPhone)

                        if (
                            shortDescription.isBlank() || fullDescription.isBlank() ||
                            curatorName.isBlank() || curatorPhone.isBlank() || curatorPhoneError ||
                            location.isBlank() || urgency.isBlank() || category.isBlank()
                        ) {
                            Toast.makeText(
                                context,
                                "Пожалуйста, заполните все поля",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@ButtonWhite
                        }

                        isLoading = true
                        val onSuccess = {
                            isLoading = false
                            Toast.makeText(context, "Задача сохранена", Toast.LENGTH_SHORT).show()
                            onSaved()
                        }
                        val onError = {
                            isLoading = false
                            Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
                        }

                        if (imageUri != null) {
                            val storageRef = storage.reference.child("task_images/task_${System.currentTimeMillis()}.jpg")
                            storageRef.putFile(imageUri!!)
                                .addOnSuccessListener {
                                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                                        saveTask(
                                            firestore,
                                            Task(
                                                key = taskKey ?: "",
                                                imageUrl = uri.toString(),
                                                shortDescription = shortDescription,
                                                fullDescription = fullDescription,
                                                curatorName = curatorName,
                                                curatorPhone = curatorPhone,
                                                location = location,
                                                urgency = urgency,
                                                category = category
                                            ),
                                            isEditMode,
                                            onSuccess,
                                            onError
                                        )
                                    }
                                }
                                .addOnFailureListener { onError() }
                        } else {
                            saveTask(
                                firestore,
                                Task(
                                    key = taskKey ?: "",
                                    imageUrl = imageUrl,
                                    shortDescription = shortDescription,
                                    fullDescription = fullDescription,
                                    curatorName = curatorName,
                                    curatorPhone = curatorPhone,
                                    location = location,
                                    urgency = urgency,
                                    category = category
                                ),
                                isEditMode,
                                onSuccess,
                                onError
                            )
                        }
                    }

                    if (isEditMode) {
                        Spacer(modifier = Modifier.height(12.dp))
                        ButtonWhite(text = "Удалить задачу") {
                            isLoading = true
                            if (imageUrl.isNotEmpty() && imageUrl.startsWith("https://firebasestorage.googleapis.com/")) {
                                val imageRef = storage.getReferenceFromUrl(imageUrl)
                                imageRef.delete().addOnCompleteListener {
                                    firestore.collection("tasks").document(taskKey!!).delete()
                                        .addOnSuccessListener {
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Задача удалена",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            onSaved()
                                        }
                                        .addOnFailureListener {
                                            isLoading = false
                                            Toast.makeText(
                                                context,
                                                "Ошибка удаления",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                            } else {
                                firestore.collection("tasks").document(taskKey!!).delete()
                                    .addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(context, "Задача удалена", Toast.LENGTH_SHORT)
                                            .show()
                                        onSaved()
                                    }
                                    .addOnFailureListener {
                                        isLoading = false
                                        Toast.makeText(context, "Ошибка удаления", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun TaskImageWithUrgencyBadge(
    imageUri: Uri?,
    imageUrl: String?,
    urgency: String?,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
    ) {
        AnimalImage(
            imageUri = imageUri,
            imageUrl = imageUrl,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 16.dp, end = 32.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(urgencyColor(urgency ?: ""))
        )
    }
}


fun urgencyColor(urgency: String): Color = when (urgency) {
    "Низкая" -> Color.Green
    "Средняя" -> Color(0xFFFFA500) // Оранжевый
    "Высокая" -> Color.Red
    "Критическая" -> Color(0xFF8B0000) // Очень темно-красный
    else -> Color.Gray
}

private fun saveTask(
    firestore: FirebaseFirestore,
    task: Task,
    isEdit: Boolean,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    val collection = firestore.collection("tasks")
    val docRef = if (isEdit && task.key.isNotBlank()) {
        collection.document(task.key)
    } else {
        collection.document()
    }

    val taskWithKey = task.copy(key = docRef.id)

    docRef.set(taskWithKey)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onError() }
}

private fun isPhoneValid(phone: String): Boolean {
    return Regex("^\\+7\\d{10}$").matches(phone)
}

fun launchCamera(
    context: Context,
    imageFile: MutableState<File?>,
    photoUri: MutableState<Uri?>,
    cameraLauncher: ActivityResultLauncher<Uri>
) {
    val file = File(context.cacheDir, "temp_task_photo.jpg").apply {
        createNewFile()
    }
    imageFile.value = file

    val uri = FileProvider.getUriForFile(
        context,
        "${context.packageName}.provider",
        file
    )
    photoUri.value = uri

    cameraLauncher.launch(uri)
}
