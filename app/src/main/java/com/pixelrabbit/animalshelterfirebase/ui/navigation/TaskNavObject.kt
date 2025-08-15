package com.pixelrabbit.animalshelterfirebase.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data class TaskNavObject(
    val uid: String,
    val imageUrl: String = "",
    val shortDescription: String = "",
    val fullDescription: String = "",
    val curatorName: String = "",
    val curatorPhone: String = "",
    val location: String = "",
    val urgency: String = "",
    val category: String = ""

)

