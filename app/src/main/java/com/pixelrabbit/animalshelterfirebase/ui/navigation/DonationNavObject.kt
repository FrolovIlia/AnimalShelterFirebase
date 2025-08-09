package com.pixelrabbit.animalshelterfirebase.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data class DonationNavObject(
    val donation: String = ""
) {
    fun toRoute(): String = "donation?donation=$donation"

    companion object {
        const val route = "donation"
        const val routeWithArgs = "donation?donation={donation}"
    }
}