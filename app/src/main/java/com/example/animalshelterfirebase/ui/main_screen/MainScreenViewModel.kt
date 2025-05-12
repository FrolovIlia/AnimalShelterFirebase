package com.example.animalshelterfirebase.ui.main_screen

import androidx.lifecycle.ViewModel
import com.example.animalshelterfirebase.ui.main_screen.bottom_menu.BottomMenuItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class MainScreenViewModel : ViewModel() {

    // Храним текущую категорию (по умолчанию "Все")
    private val _selectedCategory = MutableStateFlow("Все")
    val selectedCategory: StateFlow<String> = _selectedCategory

    // Храним текущую вкладку (по умолчанию Home)
    private val _selectedTab = MutableStateFlow<BottomMenuItem>(BottomMenuItem.Home)
    val selectedTab: StateFlow<BottomMenuItem> = _selectedTab

    fun selectCategory(category: String) {
        _selectedCategory.value = category
    }

    fun selectTab(tab: BottomMenuItem) {
        _selectedTab.value = tab
    }
}
