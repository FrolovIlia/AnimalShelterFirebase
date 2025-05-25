package com.example.animalshelterfirebase.ui.adoption_screen

import kotlinx.serialization.Serializable

@Serializable
data class AdoptionNavObject(
    val name: String,
    val age: String,
    val curatorPhone: String,
    val location: String,
    val description: String
)
