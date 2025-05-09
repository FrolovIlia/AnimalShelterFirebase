package com.example.animalshelterfirebase.ui.details_screen.data

import kotlinx.serialization.Serializable

@Serializable
data class DetailsNavObject(
    val name: String,
    val description: String,
    val imageUrl: String
)