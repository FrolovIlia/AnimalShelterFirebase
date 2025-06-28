package com.pixelrabbit.animalshelterfirebase.ui.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelrabbit.animalshelterfirebase.data.UserObject
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

import com.google.firebase.firestore.Source

class UserViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow<UserObject?>(null)
    val currentUser: StateFlow<UserObject?> = _currentUser

    fun loadUser(uid: String) {
        viewModelScope.launch {
            try {
                val document = Firebase.firestore
                    .collection("users")
                    .document(uid)
                    .get(Source.SERVER)
                    .await()

                val user = document.toObject(UserObject::class.java)?.copy(uid = uid)
                _currentUser.value = user
            } catch (e: Exception) {
                _currentUser.value = null
            }
        }
    }

    // Обновляет текущего пользователя — перезагружает данные из Firestore по uid текущего Firebase пользователя
    fun refreshUser() {
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            loadUser(uid)
        } else {
            // Если пользователь не авторизован, очищаем состояние
            clearUser()
        }
    }

    fun clearUser() {
        _currentUser.value = null
    }
}
