package com.pixelrabbit.animalshelterfirebase.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data class AnimalDetailsNavObject(
    val uid: String = "guest",
    val key: String = "",
    val imageUrl: String = "",
    val name: String = "",
    val age: String = "",
    val category: String = "",
    val description: String = "",
    val curatorPhone: String = "уточняется",
    val location: String = "уточняется",
    val feature: String = "",
    val isFavourite: Boolean = false
)