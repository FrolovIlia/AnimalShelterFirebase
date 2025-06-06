package com.example.animalshelterfirebase.ui.adoption_screen

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.animalshelterfirebase.data.Animal
import com.example.animalshelterfirebase.data.UserObject

class AdoptionViewModel : ViewModel() {
    private val _animal = mutableStateOf<Animal?>(null)
    val animal: State<Animal?> = _animal

    private val _user = mutableStateOf<UserObject?>(null)
    val user: State<UserObject?> = _user

    fun setData(animal: Animal, user: UserObject) {
        _animal.value = animal
        _user.value = user
    }
}
