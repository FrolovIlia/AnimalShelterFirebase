package com.example.animalshelterfirebase.ui.add_animal_screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.animalshelterfirebase.data.Animal
import com.example.animalshelterfirebase.ui.data.AddScreenObject
import com.example.animalshelterfirebase.ui.login.LoginButton
import com.example.animalshelterfirebase.ui.login.RoundedCornerTextField
import com.example.animalshelterfirebase.ui.theme.BackgroundWhite
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

@Preview(showBackground = true)
@Composable
fun AddAnimalScreen(
    navData: AddScreenObject = AddScreenObject(),
    onSaved: () -> Unit = {}

) {
    var selectedCategory = navData.category
    val name = remember {
        mutableStateOf(navData.name)
    }
    val description = remember {
        mutableStateOf(navData.description)
    }

    val age = remember {
        mutableStateOf(navData.age)
    }
    val selectedImageUri = remember {
        mutableStateOf<Uri?>(null)
    }

    val firestore = remember {
        Firebase.firestore
    }

    val storage = remember {
        Firebase.storage
    }


    val imageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        selectedImageUri.value = uri
    }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 50.dp,
                end = 50.dp
            ),
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
        Spacer(modifier = Modifier.size(10.dp))

        Image(
            painter = rememberAsyncImagePainter(model = selectedImageUri.value),
            contentDescription = "animal image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(16.dp))
        )

        Spacer(modifier = Modifier.size(10.dp))

        RoundedCornerDropDownMenu { selectedItem ->
            selectedCategory = selectedItem
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
        Spacer(modifier = Modifier.height(10.dp))

        LoginButton(text = "Выбрать изображение") {
            imageLauncher.launch("image/*")
        }

        LoginButton(text = "Сохранить") {
            saveAnimalImage(
                selectedImageUri.value!!,
                storage,
                firestore,
                Animal(
                    name = name.value,
                    description = description.value,
                    age = age.value,
                    category = selectedCategory
                ),
                onSaved = {
                    onSaved()
                },
                onError = {

                }
            )
        }
    }
}

private fun saveAnimalImage(
    uri: Uri,
    storage: FirebaseStorage,
    firestore: FirebaseFirestore,
    animal: Animal,
    onSaved: () -> Unit,
    onError: () -> Unit


) {

    val timeStamp = System.currentTimeMillis()
    val storageRef = storage.reference
        .child("animal_images")
        .child("image_$timeStamp.jpg")
    val uploadTask = storageRef.putFile(uri)
    uploadTask.addOnSuccessListener {
        storageRef.downloadUrl.addOnCompleteListener { url ->
            saveAnimalToFireStore(
                firestore,
                url.toString(),
                animal,
                onSaved = {
                    onSaved()
                },
                onError = {
                    onError()
                }

            )

        }
    }
}


private fun saveAnimalToFireStore(
    firestore: FirebaseFirestore,
    url: String,
    animal: Animal,
    onSaved: () -> Unit,
    onError: () -> Unit
) {
    val db = firestore.collection("animals")
    val key = db.document().id
    db.document(key)
        .set(
            animal.copy(
                key = key,
                imageUrl = url
            )
        )
        .addOnSuccessListener {
            onSaved()
        }
        .addOnFailureListener {
            onError()
        }
}

