package com.pixelrabbit.animalshelterfirebase.ui.authorization

import androidx.lifecycle.ViewModel
import com.pixelrabbit.animalshelterfirebase.data.repository.UserRepository

class UserViewModel : ViewModel() {
    val currentUser = UserRepository.currentUser
    val isAdmin = UserRepository.isAdmin
    val allUsers = UserRepository.allUsers

    fun loadUser(uid: String) = UserRepository.loadUser(uid)
    fun clearUser() = UserRepository.clearUser()
    fun loadAllUsers() = UserRepository.loadAllUsers()
    fun updateUserRole(userUid: String, isAdmin: Boolean) =
        UserRepository.updateUserRole(userUid, isAdmin)
}
