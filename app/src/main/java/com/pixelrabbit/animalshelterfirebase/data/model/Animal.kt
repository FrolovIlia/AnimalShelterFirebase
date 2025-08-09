package com.pixelrabbit.animalshelterfirebase.data.model


data class Animal(
    val key: String = "",
    val name: String = "",
    val description: String = "",
    val age: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val isFavourite: Boolean = false,
    val curatorPhone: String = "",
    val location: String = "",
    val feature: String = ""
)
