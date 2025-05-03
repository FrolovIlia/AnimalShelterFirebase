package com.example.animalshelterfirebase.ui.main_screen

import androidx.compose.foundation.Image
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

    // Состояние текущей вкладки: false = Все, true = Избранные
    var isFavoritesOnly by remember { mutableStateOf(false) }


    // Создаём список категорий
    val categories = listOf(
        AnimalCategories(R.drawable.ic_all_animals, "Все"),
        AnimalCategories(R.drawable.ic_cats, "Котики"),
        AnimalCategories(R.drawable.ic_dogs, "Собачки")
    )


    // При старте сразу грузим все животные:
    LaunchedEffect(Unit) {
        loadAllAnimals(db, navData.uid) { list ->
            animalsListState.value = list
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
                    }
                ) {
                    coroutineScope.launch {
                        drawerState.close()
                    }
                    onAdminClick()
                }
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
                        loadAllAnimals(db, navData.uid) { list ->
                            animalsListState.value = list
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
                // LazyRow — фиксированная категория
                var selectedCategory by remember { mutableStateOf<String?>(null) }

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
                            onClick = { selectedCategory = category.categoryName }
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
private fun loadAllAnimals(db: FirebaseFirestore, uid: String, onResult: (List<Animal>) -> Unit) {
    getAllFavsIds(db, uid) { favs ->
        getAllAnimals(db, favs, onResult)
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
    onAnimals: (List<Animal>) -> Unit
) {
    db.collection("animals")
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
