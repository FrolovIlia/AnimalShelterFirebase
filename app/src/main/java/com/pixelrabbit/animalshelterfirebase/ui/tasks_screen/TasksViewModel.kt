package com.pixelrabbit.animalshelterfirebase.ui.tasks_screen

import androidx.lifecycle.ViewModel
import com.pixelrabbit.animalshelterfirebase.data.repository.UserRepository

class TasksViewModel(
    private val userRepository: UserRepository = UserRepository
) : ViewModel() {
    val isAdmin = userRepository.isAdmin
}