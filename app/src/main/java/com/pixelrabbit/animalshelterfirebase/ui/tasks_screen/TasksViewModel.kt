package com.pixelrabbit.animalshelterfirebase.ui.tasks_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pixelrabbit.animalshelterfirebase.utils.isAdmin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TasksViewModel : ViewModel() {

    private val _isAdmin = MutableStateFlow(false)
    val isAdmin: StateFlow<Boolean> = _isAdmin

    fun checkAdminStatus(uid: String) {
        viewModelScope.launch {
            isAdmin(uid) { result ->
                _isAdmin.value = result
            }
        }
    }
}
