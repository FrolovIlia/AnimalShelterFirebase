package com.example.animalshelterfirebase.ui.add_animal_screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.animalshelterfirebase.data.Animal
import com.example.animalshelterfirebase.ui.data.AddScreenObject
import com.example.animalshelterfirebase.ui.theme.AnimalFont
import com.example.animalshelterfirebase.ui.theme.BackgroundGray
import com.example.animalshelterfirebase.utils.ButtonWhite

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.example.animalshelterfirebase.utils.PhoneNumberField

@Composable
fun AddAnimalScreen(
    navData: AddScreenObject = AddScreenObject(),
    onSaved: () -> Unit = {}
) {
    val isLoading = remember { mutableStateOf(false) }
    val context = LocalContext.current

    val selectedCategory = remember { mutableStateOf(navData.category) }
    val navImageUrl = remember { mutableStateOf(navData.imageUrl) }

    val name = remember { mutableStateOf(navData.name) }
    val description = remember { mutableStateOf(navData.descriptionShort) }
    val age = remember { mutableStateOf(navData.age) }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }
    val feature = remember { mutableStateOf(navData.feature) }
    val location = remember { mutableStateOf(navData.location) }
    val curatorPhone = remember { mutableStateOf(navData.curatorPhone) }
    val curatorPhoneError = remember { mutableStateOf(false) }

    val firestore = Firebase.firestore
    val storage = Firebase.storage

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        navImageUrl.value = ""
        selectedImageUri.value = uri
    }

    val defaultImageUrl = "android.resource://${context.packageName}/drawable/default_animal_image"
    val imageModel =
        navImageUrl.value.ifEmpty { selectedImageUri.value?.toString() ?: defaultImageUrl }
    val isEditMode = remember { navData.key.isNotBlank() }

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
                text = if (navData.key.isBlank()) "Добавление нового животного" else "Редактирование карточки животного",
                color = Color.Black,
                fontFamily = AnimalFont,
                fontSize = 24.sp,
                modifier = Modifier
                    .padding(top = 16.dp, bottom = 12.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            // Изображение теперь занимает всю ширину экрана с отступами по краям и скругленными углами
            if (imageModel != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageModel),
                    contentDescription = "animal image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()  // Заполняет всю ширину экрана
                        .height(200.dp)  // Высота изображения фиксированная
                        .clip(RoundedCornerShape(8.dp))  // Скругленные углы
                        .padding(horizontal = 20.dp)  // Отступы по бокам, как у полей ввода
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RoundedCornerDropDownMenu(selectedCategory.value) { selectedItem ->
                    selectedCategory.value = selectedItem
                }
                Spacer(modifier = Modifier.height(8.dp))

                RoundedCornerTextField(text = name.value, label = "Кличка") {
                    name.value = it
                }
                Spacer(modifier = Modifier.height(8.dp))

                RoundedCornerTextField(text = feature.value, label = "Особенность (кратко)") {
                    feature.value = it
                }
                Spacer(modifier = Modifier.height(8.dp))

                RoundedCornerTextField(text = age.value, label = "Возраст") {
                    age.value = it
                }


                Spacer(modifier = Modifier.height(8.dp))

                RoundedCornerTextField(
                    singleLine = false,
                    maxLines = Int.MAX_VALUE,
                    text = description.value,
                    label = "Описание"
                ) {
                    description.value = it
                }
                Spacer(modifier = Modifier.height(8.dp))

                RoundedCornerTextField(text = location.value,
                    label = "Расположение") {
                    location.value = it
                }
                Spacer(modifier = Modifier.height(8.dp))

                PhoneNumberField(
                    value = curatorPhone.value,
                    onValueChange = {
                        curatorPhone.value = it
                        curatorPhoneError.value = !isPhoneValid(it)
                    },
                    isError = curatorPhoneError.value,
                    label = "Номер куратора"
                )
                if (curatorPhoneError.value) {
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

                if (isLoading.value) {
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
                        curatorPhoneError.value = !isPhoneValid(curatorPhone.value)

                        if (name.value.isBlank() ||
                            feature.value.isBlank() ||
                            age.value.isBlank() ||
                            description.value.isBlank() ||
                            location.value.isBlank() ||
                            curatorPhone.value.isBlank() ||
                            curatorPhoneError.value ||
                            selectedCategory.value.isBlank()
                        ) {
                            Toast.makeText(
                                context,
                                "Пожалуйста, заполните все поля корректно",
                                Toast.LENGTH_SHORT
                            ).show()
                            return@ButtonWhite
                        }

                        val animal = Animal(
                            key = navData.key,
                            name = name.value,
                            description = description.value,
                            age = age.value,
                            category = selectedCategory.value,
                            imageUrl = "", // Пустая строка для пустого изображения
                            curatorPhone = curatorPhone.value,
                            location = location.value,
                            feature = feature.value
                        )

                        isLoading.value = true

                        if (selectedImageUri.value != null) {
                            saveAnimalImage(
                                oldImageUrl = navData.imageUrl,
                                uri = selectedImageUri.value!!,
                                storage = storage,
                                firestore = firestore,
                                animal = animal,
                                onSaved = {
                                    isLoading.value = false
                                    Toast.makeText(
                                        context,
                                        "Изменения сохранены",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    onSaved()
                                },
                                onError = {
                                    isLoading.value = false
                                    Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        } else {
                            saveAnimalToFireStore(
                                firestore = firestore,
                                animal = animal.copy(imageUrl = imageModel),
                                onSaved = {
                                    isLoading.value = false
                                    Toast.makeText(
                                        context,
                                        "Изменения сохранены",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    onSaved()
                                },
                                onError = {
                                    isLoading.value = false
                                    Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            )
                        }
                    }

                    if (isEditMode) {
                        Spacer(modifier = Modifier.height(12.dp))

                        ButtonWhite(text = "Удалить", modifier = Modifier.width(140.dp)) {
                            isLoading.value = true

                            // Если imageUrl пустой, не пытаемся удалить изображение
                            if (navData.imageUrl.isNotEmpty() && navData.imageUrl.startsWith("https://firebasestorage.googleapis.com/")) {
                                val imageRef = Firebase.storage.getReferenceFromUrl(navData.imageUrl)
                                imageRef.delete().addOnCompleteListener {
                                    firestore.collection("animals")
                                        .document(navData.key)
                                        .delete()
                                        .addOnSuccessListener {
                                            isLoading.value = false
                                            Toast.makeText(context, "Животное удалено", Toast.LENGTH_SHORT)
                                                .show()
                                            onSaved()
                                        }
                                        .addOnFailureListener {
                                            isLoading.value = false
                                            Toast.makeText(context, "Ошибка удаления", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                }
                            } else {
                                firestore.collection("animals")
                                    .document(navData.key)
                                    .delete()
                                    .addOnSuccessListener {
                                        isLoading.value = false
                                        Toast.makeText(context, "Животное удалено", Toast.LENGTH_SHORT)
                                            .show()
                                        onSaved()
                                    }
                                    .addOnFailureListener {
                                        isLoading.value = false
                                        Toast.makeText(context, "Ошибка удаления", Toast.LENGTH_SHORT)
                                            .show()
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

    storageRef.putFile(uri).addOnSuccessListener {
        storageRef.downloadUrl.addOnSuccessListener { url ->
            saveAnimalToFireStore(
                firestore = firestore,
                animal = animal.copy(imageUrl = url.toString()),
                onSaved = onSaved,
                onError = onError
            )
        }
    }.addOnFailureListener {
        onError()
    }
}

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

private fun isPhoneValid(phone: String): Boolean {
    return Regex("^\\+7\\d{10}$").matches(phone)
}
