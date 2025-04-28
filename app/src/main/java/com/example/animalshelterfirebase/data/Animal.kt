package com.example.animalshelterfirebase.data


data class Animal(
    val key: String = "",
    val name: String = "",
    val description: String = "",
    val age: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val isFavourite: Boolean = false
)
