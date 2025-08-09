package com.pixelrabbit.animalshelterfirebase.ui.donation_screen.shelter_data

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ShelterViewModel : ViewModel() {

    private val _shelterData = mutableStateOf<ShelterData?>(null)
    val shelterData: State<ShelterData?> = _shelterData

    init {
        loadShelterData()
    }

    private fun loadShelterData() {
        val db = Firebase.firestore
        db.collection("shelter_data").document("ePmKtUf6TMKKi4V8OTOz")
            .get()
            .addOnSuccessListener { document ->
                document?.toObject(ShelterData::class.java)?.let {
                    _shelterData.value = it
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ShelterViewModel", "Error loading shelter data", exception)
            }
    }
}
