package com.pixelrabbit.animalshelterfirebase.ui.main_screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelrabbit.animalshelterfirebase.data.Animal
import com.pixelrabbit.animalshelterfirebase.data.Favourite
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.bottom_menu.BottomMenuItem
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

    private val _userName = MutableStateFlow("")
    val userName: StateFlow<String> = _userName

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectTab(tab: BottomMenuItem) {
        _selectedTab.value = tab
    }

    fun setAnimals(list: List<Animal>) {
        _animals.value = list
    }

    fun loadAnimals(db: FirebaseFirestore, uid: String) {
        viewModelScope.launch {
            if (uid == "guest") {
                getAnimals(db, emptyList(), _selectedCategory.value) { list ->
                    _animals.value = list
                }
            } else {
                getAllFavsIds(db, uid) { favs ->
                    _favoriteIds.value = favs
                    getAnimals(db, favs, _selectedCategory.value) { list ->
                        _animals.value = list
                    }
                }
            }
        }
    }

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

    fun toggleFavorite(db: FirebaseFirestore, uid: String, animalKey: String) {
        val currentAnimals = _animals.value.toMutableList()
        val animalIndex = currentAnimals.indexOfFirst { it.key == animalKey }
        if (animalIndex == -1) return

        val animal = currentAnimals[animalIndex]
        val newFavState = !animal.isFavourite
        val favourite = Favourite(animalKey)

        if (newFavState) {
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

        currentAnimals[animalIndex] = animal.copy(isFavourite = newFavState)
        _animals.value = currentAnimals
    }

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

    fun loadUserName(db: FirebaseFirestore, uid: String) {
        if (uid == "guest" || uid.isBlank()) {
            _userName.value = "Гость"
            return
        }

        db.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val name = documentSnapshot.getString("name")
                _userName.value = name ?: ""
            }
            .addOnFailureListener { e ->
                Log.e("MainScreenViewModel", "Failed to load user name for uid: $uid", e)
                _userName.value = ""
            }
    }

    /**
     * Проверка admin-статуса пользователя.
     * Проверяет поле "isAdmin" в документе пользователя в коллекции "users".
     */
    fun checkIfUserIsAdmin(uid: String) {
        if (uid.isBlank() || uid == "guest") {
            _isAdmin.value = false
            return
        }

        FirebaseFirestore.getInstance()
            .collection("users") // проверяем именно коллекцию "users"
            .document(uid)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val adminStatus = doc.getBoolean("isAdmin") == true
                    _isAdmin.value = adminStatus
                    Log.d("MainScreenViewModel", "Admin status for $uid: $adminStatus")
                } else {
                    Log.d("MainScreenViewModel", "User document $uid not found.")
                    _isAdmin.value = false
                }
            }
            .addOnFailureListener { e ->
                Log.e("MainScreenViewModel", "Error checking admin status for UID: $uid", e)
                _isAdmin.value = false
            }
    }
}
