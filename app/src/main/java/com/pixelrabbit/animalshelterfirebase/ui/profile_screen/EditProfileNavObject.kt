package com.pixelrabbit.animalshelterfirebase.ui.profile_screen

import kotlinx.serialization.Serializable

@Serializable
data class EditProfileNavObject(
    val uid: String,
    val name: String,
    val email: String,
    val phone: String
)