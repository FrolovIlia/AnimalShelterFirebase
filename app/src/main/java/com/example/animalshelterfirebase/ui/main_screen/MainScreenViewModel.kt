package com.example.animalshelterfirebase.ui.main_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.animalshelterfirebase.data.Animal
import com.example.animalshelterfirebase.data.Favourite
import com.example.animalshelterfirebase.ui.main_screen.bottom_menu.BottomMenuItem
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel : ViewModel() {

    private val _animals = MutableStateFlow<List<Animal>>(emptyList())
    val animals: StateFlow<List<Animal>> = _animals

    private val _favoriteIds = MutableStateFlow<List<String>>(emptyList())
    val favoriteIds: StateFlow<List<String>> = _favoriteIds

    private val _selectedCategory = MutableStateFlow("Все")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _selectedTab = MutableStateFlow<BottomMenuItem>(BottomMenuItem.Home)
    val selectedTab: StateFlow<BottomMenuItem> = _selectedTab

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectTab(tab: BottomMenuItem) {
        _selectedTab.value = tab
    }

    fun setAnimals(list: List<Animal>) {
        _animals.value = list
    }

    /**
     * Загружает животных из Firestore с учетом категории и избранных.
     * Если uid == "guest", то игнорирует избранное.
     */
    fun loadAnimals(db: FirebaseFirestore, uid: String) {
        viewModelScope.launch {
            if (uid == "guest") {
                // Гость — загружаем всех животных в выбранной категории без избранных
                getAnimals(db, emptyList(), _selectedCategory.value) { list ->
                    _animals.value = list
                }
            } else {
                // Зарегистрированный пользователь — загружаем избранные id и затем животных с учетом категории и избранных
                getAllFavsIds(db, uid) { favs ->
                    _favoriteIds.value = favs
                    getAnimals(db, favs, _selectedCategory.value) { list ->
                        _animals.value = list
                    }
                }
            }
        }
    }

    /**
     * Загружает только избранных животных (для вкладки "Избранное")
     */
    fun loadFavorites(db: FirebaseFirestore, uid: String) {
        if (uid == "guest") {
            _animals.value = emptyList()
            return
        }
        viewModelScope.launch {
            getAllFavsIds(db, uid) { favs ->
                _favoriteIds.value = favs
                getAllFavsAnimals(favs) { list ->
                    _animals.value = list
                }
            }
        }
    }

    /**
     * Переключает избранное (добавить или удалить) и обновляет локальный список животных.
     */
    fun toggleFavorite(db: FirebaseFirestore, uid: String, animalKey: String) {
        val currentAnimals = _animals.value.toMutableList()
        val animalIndex = currentAnimals.indexOfFirst { it.key == animalKey }
        if (animalIndex == -1) return

        val animal = currentAnimals[animalIndex]
        val isCurrentlyFav = animal.isFavourite
        val newFavState = !isCurrentlyFav

        val favourite = Favourite(animalKey)

        if (newFavState) {
            // Добавляем в избранное
            db.collection("users")
                .document(uid)
                .collection("favourites")
                .document(animalKey)
                .set(favourite)
                .addOnSuccessListener {
                    Log.d("MainScreenViewModel", "Added to favorites: $animalKey")
                }
                .addOnFailureListener {
                    Log.e("MainScreenViewModel", "Failed to add favorite: $animalKey", it)
                }
        } else {
            // Удаляем из избранного
            db.collection("users")
                .document(uid)
                .collection("favourites")
                .document(animalKey)
                .delete()
                .addOnSuccessListener {
                    Log.d("MainScreenViewModel", "Removed from favorites: $animalKey")
                }
                .addOnFailureListener {
                    Log.e("MainScreenViewModel", "Failed to remove favorite: $animalKey", it)
                }
        }

        // Обновляем локальный список животных
        currentAnimals[animalIndex] = animal.copy(isFavourite = newFavState)
        _animals.value = currentAnimals
    }

    // --- Вспомогательные функции для работы с Firestore ---

    private fun getAnimals(
        db: FirebaseFirestore,
        favIds: List<String>,
        category: String,
        onAnimals: (List<Animal>) -> Unit
    ) {
        val collection = db.collection("animals")
        val query = if (category == "Все") {
            collection
        } else {
            collection.whereEqualTo("category", category)
        }

        query.get()
            .addOnSuccessListener { snapshot ->
                val animalsList = snapshot.toObjects(Animal::class.java).map {
                    if (favIds.contains(it.key)) it.copy(isFavourite = true) else it
                }
                onAnimals(animalsList)
            }
            .addOnFailureListener { e ->
                Log.e("MainScreenViewModel", "Error loading animals", e)
                onAnimals(emptyList())
            }
    }

    private fun getAllFavsIds(
        db: FirebaseFirestore,
        uid: String,
        onFavs: (List<String>) -> Unit
    ) {
        if (uid.isBlank() || uid == "guest") {
            onFavs(emptyList())
            return
        }

        db.collection("users")
            .document(uid)
            .collection("favourites")
            .get()
            .addOnSuccessListener { snapshot ->
                val ids = snapshot.toObjects(Favourite::class.java).map { it.key }
                onFavs(ids)
            }
            .addOnFailureListener {
                Log.e("MainScreenViewModel", "Failed to load favorite IDs", it)
                onFavs(emptyList())
            }
    }

    private fun getAllFavsAnimals(
        favIds: List<String>,
        onComplete: (List<Animal>) -> Unit
    ) {
        val validIds = favIds.filter { it.isNotBlank() }
        if (validIds.isEmpty()) {
            onComplete(emptyList())
            return
        }

        FirebaseFirestore.getInstance()
            .collection("animals")
            .whereIn(FieldPath.documentId(), validIds)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val animals = querySnapshot.documents.mapNotNull { doc ->
                    doc.toObject(Animal::class.java)?.copy(key = doc.id, isFavourite = true)
                }
                onComplete(animals)
            }
            .addOnFailureListener { e ->
                Log.e("MainScreenViewModel", "Failed to get favorite animals", e)
                onComplete(emptyList())
            }
    }
}
