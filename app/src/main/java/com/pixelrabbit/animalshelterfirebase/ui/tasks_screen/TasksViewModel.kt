package com.pixelrabbit.animalshelterfirebase.ui.tasks_screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.pixelrabbit.animalshelterfirebase.data.repository.UserRepository

class TasksViewModel(
    private val userRepository: UserRepository = UserRepository
) : ViewModel() {

    val isAdmin = userRepository.isAdmin

    // Состояние фильтра и поиска
    var query = mutableStateOf("")
        private set

    var selectedCategory = mutableStateOf("Все")
        private set

    // Флаг для сигнализации об обновлении списка задач
    var shouldRefreshTasks = mutableStateOf(false)
        private set

    fun updateQuery(newQuery: String) {
        query.value = newQuery
    }

    fun updateSelectedCategory(newCategory: String) {
        selectedCategory.value = newCategory
    }

    // Метод для установки флага обновления
    fun setRefreshTasks(value: Boolean) {
        shouldRefreshTasks.value = value
    }
}