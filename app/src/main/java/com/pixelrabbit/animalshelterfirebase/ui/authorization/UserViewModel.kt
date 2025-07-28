package com.pixelrabbit.animalshelterfirebase.ui.authorization

import androidx.lifecycle.ViewModel
import com.pixelrabbit.animalshelterfirebase.data.repository.UserRepository


class UserViewModel : ViewModel() {
    val currentUser = UserRepository.currentUser
    val isAdmin = UserRepository.isAdmin

    fun loadUser(uid: String) = UserRepository.loadUser(uid)
    fun clearUser() = UserRepository.clearUser()
}