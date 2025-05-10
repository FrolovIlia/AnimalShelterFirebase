package com.example.animalshelterfirebase.ui.main_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.animalshelterfirebase.R
import com.example.animalshelterfirebase.data.Animal
import com.example.animalshelterfirebase.data.AnimalCategories
import com.example.animalshelterfirebase.data.Favourite
import com.example.animalshelterfirebase.data.MainScreenDataObject
import com.example.animalshelterfirebase.ui.main_screen.bottom_menu.BottomMenu
import com.example.animalshelterfirebase.ui.theme.BackgroundGray
import com.example.animalshelterfirebase.ui.theme.ButtonColorBlue

import com.example.animalshelterfirebase.ui.theme.TextSecondary
import com.example.animalshelterfirebase.utils.isAdmin
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


@Composable
fun MainScreen(
    navData: MainScreenDataObject,
    onAnimalEditClick: (Animal) -> Unit,
    onAdminClick: () -> Unit
) {
    val animalsListState = remember { mutableStateOf(emptyList<Animal>()) }
    val isAdminState = remember { mutableStateOf(false) }
    val db = remember { Firebase.firestore }
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var isFavoritesOnly by remember { mutableStateOf(false) }


    val categories = listOf(
        AnimalCategories(R.drawable.ic_all_animals, "Все"),
        AnimalCategories(R.drawable.ic_cats, "Котики"),
        AnimalCategories(R.drawable.ic_dogs, "Собачки")
    )

    LaunchedEffect(Unit) {
        isAdmin { isAdmin ->
            isAdminState.value = isAdmin
        }

        getAllFavsIds(db, navData.uid) { favs ->
            getAllAnimals(db, favs, category = "Все") { animals ->
                animalsListState.value = animals
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            BottomMenu(
                isFavoritesOnly = isFavoritesOnly,
                onHomeClick = {
                    isFavoritesOnly = false
                    selectedCategory = "Все"
                    getAllFavsIds(db, navData.uid) { favs ->
                        getAllAnimals(db, favs, category = "Все") { animals ->
                            animalsListState.value = animals
                        }
                    }
                },
                onFavsClick = {
                    isFavoritesOnly = true
                    loadFavsAnimals(db, navData.uid) { list ->
                        animalsListState.value = list
                    }
                },
                onProfile = {
                    // логика профиля (если нужна)
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGray)
                .padding(paddingValues)
        ) {
            if (isAdminState.value) {
                Button(
                    onClick = { onAdminClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    colors = ButtonDefaults.buttonColors(ButtonColorBlue)
                ) {
                    Text(text = "Добавить животное")
                }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                items(categories) { category ->
                    val isSelected = selectedCategory == category.categoryName

                    Card(
                        modifier = Modifier
                            .height(52.dp)
                            .clickable {
                                selectedCategory = category.categoryName
                                getAllFavsIds(db, navData.uid) { favs ->
                                    getAllAnimals(db, favs, category.categoryName) { animals ->
                                        animalsListState.value = animals
                                    }
                                }
                            },
                        shape = RoundedCornerShape(30.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) ButtonColorBlue else androidx.compose.material3.MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 12.dp)
                        ) {
                            Image(
                                painter = painterResource(id = category.imageCategory),
                                contentDescription = category.categoryName,
                                modifier = Modifier
                                    .width(40.dp)
                                    .height(40.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .padding(end = 8.dp),
                                contentScale = ContentScale.Crop
                            )
                            Text(
                                text = category.categoryName,
                                color = TextSecondary
                            )
                        }
                    }
                }
            }


            if (animalsListState.value.isEmpty()) {
                EmptyStateScreen()
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(animalsListState.value) { animal ->
                        AnimalListItemUI(
                            isAdminState.value,
                            animal,
                            onEditClick = { onAnimalEditClick(it) },
                            onFavouriteClick = {
                                val wasFavourite = animal.isFavourite
                                val updatedFavourite = !wasFavourite

                                onFavs(
                                    db = db,
                                    uid = navData.uid,
                                    favourite = Favourite(animal.key),
                                    isFav = updatedFavourite
                                )

                                animalsListState.value = animalsListState.value.map { current ->
                                    if (current.key == animal.key) {
                                        current.copy(isFavourite = updatedFavourite)
                                    } else current
                                }

                                if (isFavoritesOnly) {
                                    loadFavsAnimals(db, navData.uid) { updatedList ->
                                        animalsListState.value = updatedList
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}



// Функция для загрузки только избранных животных
private fun loadFavsAnimals(db: FirebaseFirestore, uid: String, onResult: (List<Animal>) -> Unit) {
    getAllFavsIds(db, uid) { favs ->
        getAllFavsAnimals(db, favs, onResult)
    }
}

// Функции для получения животных и избранных животных, которые остаются такими же
fun getAllAnimals(
    db: FirebaseFirestore,
    idsList: List<String>,
    category: String,
    onAnimals: (List<Animal>) -> Unit
) {
    val collection = db.collection("animals")

    val query = if (category == "Все") {
        collection // без фильтра
    } else {
        collection.whereEqualTo("category", category)
    }

    query.get()
        .addOnSuccessListener { task ->
            val animalsList = task.toObjects(Animal::class.java).map {
                if (idsList.contains(it.key)) {
                    it.copy(isFavourite = true)
                } else {
                    it
                }
            }
            onAnimals(animalsList)
        }
        .addOnFailureListener {
            // обработка ошибки при необходимости
        }
}

fun getAllFavsAnimals(
    db: FirebaseFirestore,
    idsList: List<String>,
    onAnimals: (List<Animal>) -> Unit
) {
    if (idsList.isNotEmpty()) {


        db.collection("animals")
            .whereIn(FieldPath.documentId(), idsList)
            .get()
            .addOnSuccessListener { task ->
                val animalsList = task.toObjects(Animal::class.java).map {
                    if (idsList.contains(it.key)) {
                        it.copy(isFavourite = true)
                    } else {
                        it
                    }
                }
                onAnimals(animalsList)
            }
            .addOnFailureListener {

            }

    } else {
        onAnimals(emptyList())
    }
}

fun getAllFavsIds(
    db: FirebaseFirestore,
    uid: String,
    onFavs: (List<String>) -> Unit
) {
    db.collection("users")
        .document(uid)
        .collection("favourites")
        .get()
        .addOnSuccessListener { task ->
            val idsList = task.toObjects(Favourite::class.java)
            val keysList = arrayListOf<String>()
            idsList.forEach {
                keysList.add(it.key)
            }
            onFavs(keysList)
        }
}

private fun onFavs(
    db: FirebaseFirestore,
    uid: String,
    favourite: Favourite,
    isFav: Boolean
) {
    if (isFav) {
        db.collection("users")
            .document(uid)
            .collection("favourites")
            .document(favourite.key)
            .set(favourite)
    } else {
        db.collection("users")
            .document(uid)
            .collection("favourites")
            .document(favourite.key)
            .delete()
    }
}
