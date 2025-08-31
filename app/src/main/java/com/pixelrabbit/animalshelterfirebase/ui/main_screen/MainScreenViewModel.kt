package com.pixelrabbit.animalshelterfirebase.ui.main_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelrabbit.animalshelterfirebase.data.model.Animal
import com.pixelrabbit.animalshelterfirebase.data.model.Task
import com.pixelrabbit.animalshelterfirebase.ui.main_screen.bottom_menu.BottomMenuItem
import com.pixelrabbit.animalshelterfirebase.data.repository.AnimalRepository
import com.pixelrabbit.animalshelterfirebase.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainScreenViewModel(
    private val repository: AnimalRepository = AnimalRepository(),
    private val userRepository: UserRepository = UserRepository
) : ViewModel() {

    private val _animals = MutableStateFlow<List<Animal>>(emptyList())
    val animals: StateFlow<List<Animal>> = _animals

    private val _favoriteIds = MutableStateFlow<List<String>>(emptyList())


    private val _selectedCategory = MutableStateFlow("Все")
    val selectedCategory: StateFlow<String> = _selectedCategory

    private val _selectedTab = MutableStateFlow<BottomMenuItem>(BottomMenuItem.Home)
    val selectedTab: StateFlow<BottomMenuItem> = _selectedTab

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks

    // Прокидываем состояние из UserRepository для использования в UI
    val isAdmin = userRepository.isAdmin

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectTab(tab: BottomMenuItem) {
        _selectedTab.value = tab
    }

    fun setAnimals(list: List<Animal>) {
        _animals.value = list
    }

    fun loadAnimals(uid: String) {
        viewModelScope.launch {
            if (uid == "guest") {
                repository.getAnimals(_selectedCategory.value, emptyList()) {
                    _animals.value = it
                }
            } else {
                repository.getFavoriteIds(uid) { favs ->
                    _favoriteIds.value = favs
                    repository.getAnimals(_selectedCategory.value, favs) {
                        _animals.value = it
                    }
                }
            }
        }
    }

    fun loadFavorites(uid: String) {
        if (uid == "guest") {
            _animals.value = emptyList()
            return
        }
        viewModelScope.launch {
            repository.getFavoriteIds(uid) { favs ->
                _favoriteIds.value = favs
                repository.getFavoriteAnimals(favs) {
                    _animals.value = it
                }
            }
        }
    }

    fun toggleFavorite(uid: String, animalKey: String) {
        val currentList = _animals.value.toMutableList()
        val index = currentList.indexOfFirst { it.key == animalKey }
        if (index == -1) return

        val isNowFav = !currentList[index].isFavourite

        // оптимистично сразу меняем в UI
        currentList[index] = currentList[index].copy(isFavourite = isNowFav)
        _animals.value = currentList

        // потом обновляем в Firestore
        repository.toggleFavorite(uid, animalKey, isNowFav) { success ->
            if (!success) {
                // если ошибка — откатываем обратно
                val rollbackList = _animals.value.toMutableList()
                val rollbackIndex = rollbackList.indexOfFirst { it.key == animalKey }
                if (rollbackIndex != -1) {
                    rollbackList[rollbackIndex] =
                        rollbackList[rollbackIndex].copy(isFavourite = !isNowFav)
                    _animals.value = rollbackList
                }
            }
        }
    }


    fun loadTasks() {
        repository.getTasks { loadedTasks ->
            _tasks.value = loadedTasks
        }
    }

    fun getAnimalByKey(key: String): Animal? {
        return _animals.value.find { it.key == key }
    }
}