package com.pixelrabbit.animalshelterfirebase.data.repository

import com.pixelrabbit.animalshelterfirebase.data.model.UserObject
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object UserRepository {
    private val _currentUser = MutableStateFlow<UserObject?>(null)
    val currentUser: StateFlow<UserObject?> = _currentUser

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    private val _userLoadError = MutableStateFlow<String?>(null)
    val userLoadError: StateFlow<String?> = _userLoadError

    fun loadUser(uid: String) {
        if (uid.isBlank() || uid == "guest") {
            _currentUser.value = null
            _isAdmin.value = false
            _userLoadError.value = null
            return
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val document = Firebase.firestore.collection("users")
                    .document(uid)
                    .get()
                    .await()
                val user = document.toObject(UserObject::class.java)?.copy(uid = uid)
                _currentUser.value = user
                _isAdmin.value = document.getBoolean("isAdmin") == true
                _userLoadError.value = null
            } catch (e: Exception) {
                _currentUser.value = null
                _isAdmin.value = false
                _userLoadError.value = "Не удалось загрузить пользователя"
            }
        }
    }

    fun clearUser() {
        _currentUser.value = null
        _isAdmin.value = false
        _userLoadError.value = null
    }
}