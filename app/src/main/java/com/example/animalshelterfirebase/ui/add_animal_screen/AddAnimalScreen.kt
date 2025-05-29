package com.example.animalshelterfirebase.ui.add_animal_screen

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.animalshelterfirebase.data.Animal
import com.example.animalshelterfirebase.ui.data.AddScreenObject
import com.example.animalshelterfirebase.utils.ButtonWhite
import com.example.animalshelterfirebase.ui.theme.BackgroundGray
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

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
    val description = remember { mutableStateOf(navData.description) }
    val age = remember { mutableStateOf(navData.age) }
    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    val firestore = Firebase.firestore
    val storage = Firebase.storage

    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        navImageUrl.value = ""
        selectedImageUri.value = uri
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 50.dp, end = 50.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Добавление нового животного",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Serif,
                fontSize = 25.sp
            )

            if (isLoading.value) {
                CircularProgressIndicator()
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.size(10.dp))

            val imageModel = navImageUrl.value.ifEmpty { selectedImageUri.value?.toString() }

            if (imageModel != null) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageModel),
                    contentDescription = "animal image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                )
            }

            Spacer(modifier = Modifier.size(10.dp))

            RoundedCornerDropDownMenu(selectedCategory.value) { selectedItem ->
                selectedCategory.value = selectedItem
            }
            Spacer(modifier = Modifier.size(10.dp))

            RoundedCornerTextField(
                text = name.value,
                label = "Кличка"
            ) {
                name.value = it
            }
            Spacer(modifier = Modifier.height(10.dp))

            RoundedCornerTextField(
                singleLine = false,
                maxLines = 5,
                text = description.value,
                label = "Описание"
            ) {
                description.value = it
            }
            Spacer(modifier = Modifier.height(10.dp))

            RoundedCornerTextField(
                text = age.value,
                label = "Возраст"
            ) {
                age.value = it
            }
            Spacer(modifier = Modifier.height(8.dp))

            ButtonWhite(text = "Выбрать изображение") {
                imageLauncher.launch("image/*")
            }

            Spacer(modifier = Modifier.height(8.dp))

            ButtonWhite(text = "Сохранить") {
                val animal = Animal(
                    key = navData.key,
                    name = name.value,
                    description = description.value,
                    age = age.value,
                    category = selectedCategory.value,
                    imageUrl = ""
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
                            Toast.makeText(context, "Изменения сохранены", Toast.LENGTH_SHORT)
                                .show()
                            onSaved()
                        },
                        onError = {
                            isLoading.value = false
                            Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    saveAnimalToFireStore(
                        firestore = firestore,
                        animal = animal.copy(imageUrl = navImageUrl.value),
                        onSaved = {
                            isLoading.value = false
                            Toast.makeText(context, "Изменения сохранены", Toast.LENGTH_SHORT)
                                .show()
                            onSaved()
                        },
                        onError = {
                            isLoading.value = false
                            Toast.makeText(context, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
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
