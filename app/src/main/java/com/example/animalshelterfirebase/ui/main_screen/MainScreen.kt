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
import com.example.animalshelterfirebase.data.MainScreenDataObject
import com.example.animalshelterfirebase.ui.main_screen.bottom_menu.BottomMenu
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch


@Composable
fun MainScreen(
    navData: MainScreenDataObject,
    onAdminClick: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Open)
    val coroutineScope = rememberCoroutineScope()

    val animalsListState = remember {
        mutableStateOf(emptyList<Animal>())
    }

    LaunchedEffect(Unit) {
        val db = Firebase.firestore
        getAllAnimals(db) { animal ->
            animalsListState.value = animal
        }
    }



    ModalNavigationDrawer(
        drawerState = drawerState,
        modifier = Modifier.fillMaxWidth(),
        drawerContent = {
            Column(Modifier.fillMaxWidth(0.7f)) {
                DrawerHeader(navData.email)
                DrawerBody {
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
                BottomMenu()
            }
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                items(animalsListState.value) { animal ->
                    AnimalListItemUI(animal)
                }
            }
        }
    }
}

fun getAllAnimals(
    db: FirebaseFirestore,
    onAnimals: (List<Animal>) -> Unit
) {
    db.collection("animals")
        .get()
        .addOnSuccessListener { task ->
            val animalsList = task.toObjects(Animal::class.java)
            onAnimals(animalsList)

        }
        .addOnFailureListener {

        }
}