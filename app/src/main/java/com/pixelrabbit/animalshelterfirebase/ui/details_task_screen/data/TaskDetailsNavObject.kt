package com.pixelrabbit.animalshelterfirebase.ui.details_task_screen.data

import kotlinx.serialization.Serializable

@Serializable
data class TaskDetailsNavObject(
    val uid: String = "guest",
    val imageUrl: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val location: String = "уточняется"
)
