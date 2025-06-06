package com.pixelrabbit.animalshelterfirebase.ui.add_animal_screen

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.pixelrabbit.animalshelterfirebase.R
import com.pixelrabbit.animalshelterfirebase.data.Animal
import com.pixelrabbit.animalshelterfirebase.ui.data.AddScreenObject
import com.pixelrabbit.animalshelterfirebase.ui.theme.AnimalFont
import com.pixelrabbit.animalshelterfirebase.ui.theme.BackgroundGray
import com.pixelrabbit.animalshelterfirebase.utils.AnimalImage
import com.pixelrabbit.animalshelterfirebase.utils.ButtonWhite
import com.pixelrabbit.animalshelterfirebase.utils.PhoneNumberField

@Composable
fun AddAnimalScreen(
    navData: AddScreenObject = AddScreenObject(),
    onSaved: () -> Unit = {}
) {
    val context = LocalContext.current
    val firestore = Firebase.firestore
    val storage = Firebase.storage

    // UI state holders
    var selectedCategory by remember { mutableStateOf(navData.category) }
    var navImageUrl by remember { mutableStateOf(navData.imageUrl) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    var name by remember { mutableStateOf(navData.name) }
    var description by remember { mutableStateOf(navData.descriptionShort) }
    var age by remember { mutableStateOf(navData.age) }
    var feature by remember { mutableStateOf(navData.feature) }
    var location by remember { mutableStateOf(navData.location) }
    var curatorPhone by remember { mutableStateOf(navData.curatorPhone) }
    var curatorPhoneError by remember { mutableStateOf(false) }

    var isLoading by remember { mutableStateOf(false) }

    val isEditMode = navData.key.isNotBlank()

    // Image picker launcher
    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            selectedImageUri = uri
            navImageUrl = "" // сбросим url, чтобы показывать выбранную картинку
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
                text = if (isEditMode) "Редактирование карточки животного" else "Добавление нового животного",
                color = Color.Black,
                fontFamily = AnimalFont,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 12.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            AnimalImage(
                imageUri = selectedImageUri,
                imageUrl = navImageUrl,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(horizontal = 0.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Dropdown для категории
                RoundedCornerDropDownMenu(selectedCategory) { selectedItem ->
                    selectedCategory = selectedItem
                }
                Spacer(modifier = Modifier.height(8.dp))

                RoundedCornerTextField(text = name, label = "Кличка") {
                    name = it
                }
                Spacer(modifier = Modifier.height(8.dp))

                RoundedCornerTextField(text = feature, label = "Особенность (кратко)") {
                    feature = it
                }
                Spacer(modifier = Modifier.height(8.dp))

                RoundedCornerTextField(text = age, label = "Возраст") {
                    age = it
                }
                Spacer(modifier = Modifier.height(8.dp))

                RoundedCornerTextField(
                    text = description,
                    label = "Описание",
                    singleLine = false,
                    maxLines = Int.MAX_VALUE
                ) {
                    description = it
                }
                Spacer(modifier = Modifier.height(8.dp))

                RoundedCornerTextField(text = location, label = "Расположение") {
                    location = it
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
                        text = "Неверный номер (должен быть формата +7XXXXXXXXXX)",
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                ButtonWhite(text = "Выбрать изображение") {
                    imageLauncher.launch("image/*")
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .wrapContentWidth()
                        .align(Alignment.CenterHorizontally)
                ) {
                    ButtonWhite(
                        text = if (isEditMode) "Сохранить" else "Добавить",
                        modifier = Modifier.width(140.dp)
                    ) {
                        // Проверка валидации
                        curatorPhoneError = !isPhoneValid(curatorPhone)
                        if (
                            name.isBlank() || feature.isBlank() || age.isBlank() ||
                            description.isBlank() || location.isBlank() || curatorPhone.isBlank() ||
                            curatorPhoneError || selectedCategory.isBlank()
                        ) {
                            Toast.makeText(context, "Пожалуйста, заполните все поля корректно", Toast.LENGTH_SHORT).show()
                            return@ButtonWhite
                        }

                        isLoading = true

                        val animal = Animal(
                            key = navData.key,
                            name = name,
                            description = description,
                            age = age,
                            category = selectedCategory,
                            imageUrl = "", // будет установлен ниже после загрузки
                            curatorPhone = curatorPhone,
                            location = location,
                            feature = feature
                        )

                        if (selectedImageUri != null) {
                            saveAnimalImage(
                                oldImageUrl = navImageUrl,
                                uri = selectedImageUri!!,
                                storage = storage,
                                firestore = firestore,
                                animal = animal,
                                onSaved = {
                                    isLoading = false
                                    Toast.makeText(context, "Изменения сохранены", Toast.LENGTH_SHORT).show()
                                    onSaved()
                                },
                                onError = {
                                    isLoading = false
                                    Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
                                }
                            )
                        } else {
                            // Если новая картинка не выбрана, сохраняем с текущим url
                            saveAnimalToFireStore(
                                firestore = firestore,
                                animal = animal.copy(imageUrl = navImageUrl),
                                onSaved = {
                                    isLoading = false
                                    Toast.makeText(context, "Изменения сохранены", Toast.LENGTH_SHORT).show()
                                    onSaved()
                                },
                                onError = {
                                    isLoading = false
                                    Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }

                    if (isEditMode) {
                        ButtonWhite(text = "Удалить", modifier = Modifier.width(140.dp)) {
                            isLoading = true

                            // Удаляем изображение если есть и оно из Firebase Storage
                            if (navImageUrl.isNotEmpty() && navImageUrl.startsWith("https://firebasestorage.googleapis.com/")) {
                                val imageRef = storage.getReferenceFromUrl(navImageUrl)
                                imageRef.delete().addOnCompleteListener {
                                    firestore.collection("animals")
                                        .document(navData.key)
                                        .delete()
                                        .addOnSuccessListener {
                                            isLoading = false
                                            Toast.makeText(context, "Животное удалено", Toast.LENGTH_SHORT).show()
                                            onSaved()
                                        }
                                        .addOnFailureListener {
                                            isLoading = false
                                            Toast.makeText(context, "Ошибка удаления", Toast.LENGTH_SHORT).show()
                                        }
                                }
                            } else {
                                firestore.collection("animals")
                                    .document(navData.key)
                                    .delete()
                                    .addOnSuccessListener {
                                        isLoading = false
                                        Toast.makeText(context, "Животное удалено", Toast.LENGTH_SHORT).show()
                                        onSaved()
                                    }
                                    .addOnFailureListener {
                                        isLoading = false
                                        Toast.makeText(context, "Ошибка удаления", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// Сохраняет изображение в Firebase Storage, затем сохраняет данные животного
private fun saveAnimalImage(
    oldImageUrl: String,
    uri: Uri,
    storage: FirebaseStorage,
    firestore: FirebaseFirestore,
    animal: Animal,
    onSaved: () -> Unit,
    onError: () -> Unit
) {
    val timeStamp = System.currentTimeMillis()
    val storageRef = if (oldImageUrl.isEmpty()) {
        storage.reference.child("animal_images").child("image_$timeStamp.jpg")
    } else {
        storage.getReferenceFromUrl(oldImageUrl)
    }

    storageRef.putFile(uri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { url ->
                saveAnimalToFireStore(
                    firestore = firestore,
                    animal = animal.copy(imageUrl = url.toString()),
                    onSaved = onSaved,
                    onError = onError
                )
            }.addOnFailureListener {
                onError()
            }
        }
        .addOnFailureListener {
            onError()
        }
}

// Сохраняет животное в Firestore
private fun saveAnimalToFireStore(
    firestore: FirebaseFirestore,
    animal: Animal,
    onSaved: () -> Unit,
    onError: () -> Unit
) {
    val db = firestore.collection("animals")
    val key = if (animal.key.isNotEmpty()) animal.key else db.document().id

    db.document(key)
        .set(animal.copy(key = key))
        .addOnSuccessListener {
            onSaved()
        }
        .addOnFailureListener {
            onError()
        }
}

// Валидация телефона +7XXXXXXXXXX
private fun isPhoneValid(phone: String): Boolean {
    return Regex("^\\+7\\d{10}$").matches(phone)
}
