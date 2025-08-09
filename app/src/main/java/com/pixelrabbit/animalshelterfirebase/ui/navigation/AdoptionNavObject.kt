package com.pixelrabbit.animalshelterfirebase.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data class AdoptionNavObject(
    val name: String,
    val age: String,
    val curatorPhone: String,
    val location: String,
    val description: String,

    val userUid: String,
    val userName: String,
    val userPhone: String,
    val userEmail: String
)
