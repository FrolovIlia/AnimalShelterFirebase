package com.example.animalshelterfirebase.data

import kotlinx.serialization.Serializable

@Serializable
data class MainScreenDataObject(
    val uid: String = "",
    val email: String = ""

)
