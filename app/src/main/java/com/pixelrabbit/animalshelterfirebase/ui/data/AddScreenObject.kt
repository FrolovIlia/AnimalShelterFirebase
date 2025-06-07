package com.pixelrabbit.animalshelterfirebase.ui.data

import kotlinx.serialization.Serializable

@Serializable
data class AddScreenObject(
    val key: String = "",
    val name: String = "",
    val descriptionShort: String = "",
    val descriptionFull: String = "",
    val age: String = "",
    val category: String = "",
    val imageUrl: String = "",
    val feature: String = "",
    val location: String = "",
    val curatorPhone: String = ""
)