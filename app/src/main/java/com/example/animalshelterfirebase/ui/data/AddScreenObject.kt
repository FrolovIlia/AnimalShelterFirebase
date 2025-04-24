package com.example.animalshelterfirebase.ui.data

import kotlinx.serialization.Serializable

@Serializable
data class AddScreenObject(
    val key: String = "",
    val name: String = "",
    val description: String = "",
    val age: String = "",
    val category: String = "",
    val imageUrl: String = ""
)