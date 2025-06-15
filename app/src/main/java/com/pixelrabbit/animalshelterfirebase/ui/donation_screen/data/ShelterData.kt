package com.pixelrabbit.animalshelterfirebase.ui.donation_screen.data

import kotlinx.serialization.Serializable

@Serializable
data class ShelterData(
    val general: String = "",
    val name: String = "",
    val donation: String = "",
    val needs: String = ""
)