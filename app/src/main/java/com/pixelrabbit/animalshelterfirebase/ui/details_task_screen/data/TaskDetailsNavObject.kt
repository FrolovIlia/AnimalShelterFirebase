package com.pixelrabbit.animalshelterfirebase.ui.details_task_screen.data

import kotlinx.serialization.Serializable

@Serializable
data class TaskDetailsNavObject(
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