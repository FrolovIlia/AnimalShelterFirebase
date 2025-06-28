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

    private val _userLoadError = MutableStateFlow<String?>(null)
    val userLoadError: StateFlow<String?> = _userLoadError

    fun loadUser(uid: String) {
        viewModelScope.launch {
            try {
                val document = Firebase.firestore
                    .collection("users")
                    .document(uid)
                    .get(Source.SERVER) // Можно заменить на Source.DEFAULT (попробует кэш)
                    .await()

                val user = document.toObject(UserObject::class.java)?.copy(uid = uid)
                if (user != null) {
                    _currentUser.value = user
                    _userLoadError.value = null
                } else {
                    _userLoadError.value = "Пользователь не найден"
                    _currentUser.value = null
                }
            } catch (e: Exception) {
                _currentUser.value = null
                _userLoadError.value = "Не удалось загрузить данные. Проверьте интернет-соединение."
            }
        }
    }

    fun refreshUser() {
        val uid = Firebase.auth.currentUser?.uid
        if (uid != null) {
            loadUser(uid)
        } else {
            clearUser()
        }
    }

    fun clearUser() {
        _currentUser.value = null
        _userLoadError.value = null
    }
}
