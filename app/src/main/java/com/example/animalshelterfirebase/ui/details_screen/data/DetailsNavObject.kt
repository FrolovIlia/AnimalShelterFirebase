package com.example.animalshelterfirebase.ui.details_screen.data

import kotlinx.serialization.Serializable

@Serializable
data class DetailsNavObject(
    val imageUrl: String = "",
    val name: String = "",
    val age: String = "",
    val category: String = "",
    val description: String = "",
    val volunteerPhone: String = "",
    val location: String = ""

)