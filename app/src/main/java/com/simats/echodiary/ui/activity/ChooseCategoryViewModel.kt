package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class CategoryItem(
    val name: String,
    val icon: String
)

data class ChooseCategoryUiState(
    val selectedCategory: String = "College",
    val categories: List<CategoryItem> = listOf(
        CategoryItem("College", "🎓"),
        CategoryItem("Work", "💼"),
        CategoryItem("Friends", "👥"),
        CategoryItem("Family", "🏠"),
        CategoryItem("Health", "❤️"),
        CategoryItem("Travel", "✈️"),
        CategoryItem("Exercise", "🧩"),
        CategoryItem("Learning", "📖"),
        CategoryItem("Hobbies", "🎨"),
        CategoryItem("Other", "⋯")
    )
)

sealed class ChooseCategoryNavigationEvent {
    object NavigateBack : ChooseCategoryNavigationEvent()
    data class CategorySelected(val category: String) : ChooseCategoryNavigationEvent()
}

class ChooseCategoryViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ChooseCategoryUiState())
    val uiState: StateFlow<ChooseCategoryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ChooseCategoryNavigationEvent>()
    val navigationEvent: SharedFlow<ChooseCategoryNavigationEvent> = _navigationEvent.asSharedFlow()

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onUseCategoryClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ChooseCategoryNavigationEvent.CategorySelected(_uiState.value.selectedCategory))
        }
    }

    fun onBackClicked() {
        viewModelScope.launch {
            _navigationEvent.emit(ChooseCategoryNavigationEvent.NavigateBack)
        }
    }
}
