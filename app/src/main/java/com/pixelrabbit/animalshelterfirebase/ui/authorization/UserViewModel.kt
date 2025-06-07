package com.pixelrabbit.animalshelterfirebase.ui.authorization

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelrabbit.animalshelterfirebase.data.UserObject
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val _currentUser = MutableStateFlow<UserObject?>(null)
    val currentUser: StateFlow<UserObject?> = _currentUser

    fun loadUser(uid: String) {
        viewModelScope.launch {
            Firebase.firestore.collection("users")
                .document(uid)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val user = document.toObject(UserObject::class.java)?.copy(uid = uid)
                        _currentUser.value = user
                    } else {
                        _currentUser.value = null
                    }
                }
                .addOnFailureListener {
                    _currentUser.value = null
                }
        }
    }

    fun clearUser() {
        _currentUser.value = null
    }
}
