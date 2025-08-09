package com.pixelrabbit.animalshelterfirebase.data.model

import kotlinx.serialization.Serializable

@Serializable
data class MainScreenDataObject(
    val uid: String,
    val email: String,
    val name: String = "",
    val phone: String = "",
    val birthDate: String = ""
)
