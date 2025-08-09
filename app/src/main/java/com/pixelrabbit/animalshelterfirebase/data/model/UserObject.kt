package com.pixelrabbit.animalshelterfirebase.data.model

import com.google.firebase.firestore.PropertyName

data class UserObject(
    val uid: String = "",
    val name: String = "",
    val birthDate: String = "",
    val phone: String = "",
    val email: String = "",
    @get:PropertyName("isAdmin")
    val isAdmin: Boolean = false,
    @get:PropertyName("isOwner")
    val isOwner: Boolean = false
)