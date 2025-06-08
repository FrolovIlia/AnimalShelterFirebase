package com.pixelrabbit.animalshelterfirebase.data

import com.google.firebase.firestore.PropertyName

data class UserObject(
    val uid: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    @get:PropertyName("isAdmin")
    val isAdmin: Boolean = false
)