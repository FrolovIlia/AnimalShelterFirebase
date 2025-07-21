package com.pixelrabbit.animalshelterfirebase.ui.add_task_screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.pixelrabbit.animalshelterfirebase.data.Task
import com.pixelrabbit.animalshelterfirebase.ui.common.RoundedCornerDropDownMenu
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundGray
import com.pixelrabbit.animalshelterfirebase.utils.AnimalImage
import com.pixelrabbit.animalshelterfirebase.utils.ButtonWhite
import com.pixelrabbit.animalshelterfirebase.utils.PhoneNumberField
import com.pixelrabbit.animalshelterfirebase.ui.common.RoundedCornerTextField

@Composable
fun AddTaskScreen(
    taskData: Task = Task(),
    onSaved: () -> Unit = {}
) {
    val context = LocalContext.current
    val firestore = FirebaseFirestore.getInstance()
    val storage = Firebase.storage

    var imageUrl by remember { mutableStateOf(taskData.imageUrl) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var shortDescription by remember { mutableStateOf(taskData.shortDescription) }
    var fullDescription by remember { mutableStateOf(taskData.fullDescription) }
    var curatorName by remember { mutableStateOf(taskData.curatorName) }
    var curatorPhone by remember { mutableStateOf(taskData.curatorPhone) }
    var location by remember { mutableStateOf(taskData.location) }
    var urgency by remember { mutableStateOf(taskData.urgency) }
    var category by remember { mutableStateOf(taskData.category) }
    var curatorPhoneError by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val taskCategoryOptions = listOf("С животными", "По хозяйству", "Орг. вопросы")

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            imageUri = it
            imageUrl = ""
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
            .padding(WindowInsets.systemBars.asPaddingValues())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Добавление новой задачи",
                color = Color.Black,
                fontFamily = AnimalFont,
                fontSize = 24.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 12.dp),
                textAlign = TextAlign.Center
            )

            AnimalImage(
                imageUri = imageUri,
                imageUrl = imageUrl,
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

                RoundedCornerTextField(text = urgency, label = "Срочность") {
                    urgency = it
                }
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

                ButtonWhite(text = "+ Файл") {
                    imagePickerLauncher.launch("image/*")
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                }

                ButtonWhite(text = "Сохранить") {
                    curatorPhoneError = !isPhoneValid(curatorPhone)

                    if (
                        shortDescription.isBlank() || fullDescription.isBlank() ||
                        curatorName.isBlank() || curatorPhone.isBlank() || curatorPhoneError ||
                        location.isBlank() || urgency.isBlank() || category.isBlank()
                    ) {
                        Toast.makeText(context, "Пожалуйста, заполните все поля", Toast.LENGTH_SHORT).show()
                        return@ButtonWhite
                    }

                    isLoading = true

                    val task = Task(
                        imageUrl = "",
                        shortDescription = shortDescription,
                        fullDescription = fullDescription,
                        curatorName = curatorName,
                        curatorPhone = curatorPhone,
                        location = location,
                        urgency = urgency,
                        category = category
                    )

                    if (imageUri != null) {
                        val storageRef = storage.reference.child("task_images/task_${System.currentTimeMillis()}.jpg")
                        storageRef.putFile(imageUri!!)
                            .addOnSuccessListener {
                                storageRef.downloadUrl.addOnSuccessListener { uri ->
                                    saveTask(
                                        firestore,
                                        task.copy(imageUrl = uri.toString()),
                                        onSuccess = {
                                            isLoading = false
                                            Toast.makeText(context, "Задача сохранена", Toast.LENGTH_SHORT).show()
                                            onSaved()
                                        },
                                        onError = {
                                            isLoading = false
                                            Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
                                        }
                                    )
                                }
                            }
                            .addOnFailureListener {
                                isLoading = false
                                Toast.makeText(context, "Ошибка загрузки изображения", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        saveTask(
                            firestore,
                            task.copy(imageUrl = imageUrl),
                            onSuccess = {
                                isLoading = false
                                Toast.makeText(context, "Задача сохранена", Toast.LENGTH_SHORT).show()
                                onSaved()
                            },
                            onError = {
                                isLoading = false
                                Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

private fun saveTask(
    firestore: FirebaseFirestore,
    task: Task,
    onSuccess: () -> Unit,
    onError: () -> Unit
) {
    firestore.collection("tasks")
        .add(task)
        .addOnSuccessListener { onSuccess() }
        .addOnFailureListener { onError() }
}

private fun isPhoneValid(phone: String): Boolean {
    return Regex("^\\+7\\d{10}$").matches(phone)
}
