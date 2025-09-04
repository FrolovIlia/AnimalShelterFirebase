package com.pixelrabbit.animalshelterfirebase.data.repository

import com.google.firebase.auth.ktx.auth
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

    private val _allUsers = MutableStateFlow<List<UserObject>>(emptyList())
    val allUsers: StateFlow<List<UserObject>> = _allUsers

    private val _userLoadError = MutableStateFlow<String?>(null)

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

    fun loadAllUsers() {
        val currentUid = Firebase.auth.currentUser?.uid ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val snapshot = Firebase.firestore.collection("users").get().await()
                val users = snapshot.documents.mapNotNull { it.toObject(UserObject::class.java)?.copy(uid = it.id) }
                // Owner видит всех
                val currentUserDoc = users.find { it.uid == currentUid }
                if (currentUserDoc?.isOwner == true) {
                    _allUsers.value = users
                } else {
                    // Обычный пользователь видит только себя
                    _allUsers.value = listOfNotNull(currentUserDoc)
                }
            } catch (e: Exception) {
                _allUsers.value = emptyList()
            }
        }
    }

    fun updateUserRole(userUid: String, isAdmin: Boolean) {
        val currentUid = Firebase.auth.currentUser?.uid ?: return
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val currentDoc = Firebase.firestore.collection("users").document(currentUid).get().await()
                val isOwner = currentDoc.getBoolean("isOwner") == true
                if (!isOwner) return@launch

                Firebase.firestore.collection("users").document(userUid)
                    .update("isAdmin", isAdmin)
                    .await()

                // После успешного обновления перезагружаем список пользователей
                loadAllUsers()
            } catch (e: Exception) {
                // Ошибка игнорируем или логируем
            }
        }
    }
}
