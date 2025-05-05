package com.example.animalshelterfirebase.ui.main_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.animalshelterfirebase.R
import com.example.animalshelterfirebase.data.Animal
import com.example.animalshelterfirebase.data.AnimalCategories
import com.example.animalshelterfirebase.data.Favourite
import com.example.animalshelterfirebase.data.MainScreenDataObject
import com.example.animalshelterfirebase.ui.main_screen.bottom_menu.BottomMenu
import com.example.animalshelterfirebase.ui.theme.BackgroundSecondary
import com.example.animalshelterfirebase.ui.theme.BackgroundWhite
import com.example.animalshelterfirebase.ui.theme.TextSecondary
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    navData: MainScreenDataObject,
    onAnimalEditClick: (Animal) -> Unit,
    onAdminClick: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Open)
    val coroutineScope = rememberCoroutineScope()

    val animalsListState = remember {
        mutableStateOf(emptyList<Animal>())
    }

    val isAdminState = remember {
        mutableStateOf(false)
    }

    val db = remember {
        Firebase.firestore
    }

    // LazyRow — фиксированная категория
    var selectedCategory by remember { mutableStateOf<String?>(null) }


    // Состояние текущей вкладки: false = Все, true = Избранные
    var isFavoritesOnly by remember { mutableStateOf(false) }


    // Создаём список категорий
    val categories = listOf(
        AnimalCategories(R.drawable.ic_all_animals, "Все"),
        AnimalCategories(R.drawable.ic_cats, "Котики"),
        AnimalCategories(R.drawable.ic_dogs, "Собачки")
    )


    LaunchedEffect(Unit) {
        getAllFavsIds(db, navData.uid) { favs ->
            getAllAnimals(db, favs, category = "Все") { animals ->
                animalsListState.value = animals
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = Modifier.fillMaxWidth(),
        drawerContent = {
            Column(Modifier.fillMaxWidth(0.7f)) {
                DrawerHeader(navData.email)
                DrawerBody(
                    onAdmin = { isAdmin ->
                        isAdminState.value = isAdmin
                    },
                    onFavClick = {
                        isFavoritesOnly = true
                        loadFavsAnimals(db, navData.uid) { list ->
                            animalsListState.value = list
                        }
                        coroutineScope.launch {
                            drawerState.close()
                        }
                    },
                    onAdminClick = {
                        coroutineScope.launch {
                            drawerState.close()
                        }
                        onAdminClick()
                    },
                    onCategoryClick = { category ->
                        getAllFavsIds(db, navData.uid) { favs ->
                            getAllAnimals(db, favs, category) { animals ->
                                animalsListState.value = animals

                            }
                        }
                    }

                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                BottomMenu(
                    isFavoritesOnly = isFavoritesOnly,
                    onHomeClick = {
                        isFavoritesOnly = false
                        selectedCategory = "Все" // если используешь выделение
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
                        // Логика для кнопки "Профиль", если необходимо
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp)
                ) {
                    items(categories) { category ->

                        Card(
                            modifier = Modifier
                                .height(52.dp),
                            shape = RoundedCornerShape(30.dp),
                            onClick = {
                                selectedCategory = category.categoryName

                                getAllFavsIds(db, navData.uid) { favs ->
                                    getAllAnimals(db, favs, category.categoryName) { animals ->
                                        animalsListState.value = animals
                                    }
                                }
                            }
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

                // LazyVerticalGrid — скроллится отдельно
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    items(animalsListState.value) { animal ->
                        AnimalListItemUI(
                            isAdminState.value,
                            animal,
                            onEditClick = {
                                onAnimalEditClick(it)
                            },
                            onFavouriteClick = {
                                animalsListState.value = animalsListState.value.map { anim ->
                                    if (anim.key == animal.key) {
                                        onFavs(
                                            db,
                                            navData.uid,
                                            Favourite(anim.key),
                                            !anim.isFavourite
                                        )
                                        anim.copy(isFavourite = !anim.isFavourite)
                                    } else anim
                                }
                            }
                        )
                    }
                }
            }
        }

    }
}

// Функция для загрузки всех животных
//private fun loadAllAnimals(
//    db: FirebaseFirestore,
//    uid: String, onResult: (List<Animal>) -> Unit
//) {
//    getAllFavsIds(db, uid) { favs ->
//        getAllAnimals(db, favs, category = "Котики", onResult)
//    }
//}

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
