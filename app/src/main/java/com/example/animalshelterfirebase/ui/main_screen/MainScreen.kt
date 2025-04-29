package com.example.animalshelterfirebase.ui.main_screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.example.animalshelterfirebase.data.Animal
import com.example.animalshelterfirebase.data.Favourite
import com.example.animalshelterfirebase.data.MainScreenDataObject
import com.example.animalshelterfirebase.ui.main_screen.bottom_menu.BottomMenu
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

    LaunchedEffect(Unit) {
        getAllFavsIds(db, navData.uid) { favs ->
            getAllAnimals(db, favs) { animal ->
                animalsListState.value = animal
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
                    }
                ) {
                    //если нужно чтобы боковая панель всегда сворачивалась - раскомментировать
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
                    onFavsClick = {
                        getAllFavsIds(db, navData.uid) { favs ->
                            getAllFavsAnimals(db, favs) { animal ->
                                animalsListState.value = animal
                            }
                        }
                    },
                    onHomeClick = {
                        getAllFavsIds(db, navData.uid) { favs ->
                            getAllAnimals(db, favs) { animal ->
                                animalsListState.value = animal
                            }
                        }
                    }

                )
            }
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
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
                                    anim.copy(isFavourite = true)
                                } else {
                                    anim
                                }
                            }
                        }
                    )
                }
            }
        }
    }
}

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