package com.simats.echodiary.ui.profile

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ThemePaletteItem(
    val id: String,
    val name: String,
    val description: String,
    val primaryColor: Color,
    val containerColor: Color,
    val iconEmoji: String = "✨"
)

data class AppearanceUiState(
    val selectedMode: String = "Light",
    val selectedPalette: String = "Calm Blue",
    val availableModes: List<String> = listOf("Light", "Dark", "System"),
    val palettes: List<ThemePaletteItem> = listOf(
        ThemePaletteItem(
            id = "Calm Blue",
            name = "Calm Blue",
            description = "Balanced, clear and serene (Default)",
            primaryColor = Color(0xFF5165F6),
            containerColor = Color(0xFFEEF2FF),
            iconEmoji = "🌊"
        ),
        ThemePaletteItem(
            id = "Serene Lavender",
            name = "Serene Lavender",
            description = "Creative, reflective and mindful",
            primaryColor = Color(0xFF8B5CF6),
            containerColor = Color(0xFFF5F3FF),
            iconEmoji = "🪻"
        ),
        ThemePaletteItem(
            id = "Emerald Forest",
            name = "Emerald Forest",
            description = "Natural, peaceful and grounded",
            primaryColor = Color(0xFF10B981),
            containerColor = Color(0xFFECFDF5),
            iconEmoji = "🍃"
        ),
        ThemePaletteItem(
            id = "Sunset Amber",
            name = "Sunset Amber",
            description = "Warm, vibrant and energetic",
            primaryColor = Color(0xFFF97316),
            containerColor = Color(0xFFFFF7ED),
            iconEmoji = "🌅"
        ),
        ThemePaletteItem(
            id = "Rose Blossom",
            name = "Rose Blossom",
            description = "Gentle, heartfelt and soothing",
            primaryColor = Color(0xFFEC4899),
            containerColor = Color(0xFFFDF2F8),
            iconEmoji = "🌸"
        ),
        ThemePaletteItem(
            id = "Midnight Slate",
            name = "Midnight Slate",
            description = "Sleek, focused and distraction-free",
            primaryColor = Color(0xFF475569),
            containerColor = Color(0xFFF1F5F9),
            iconEmoji = "🌌"
        )
    ),
    val previewSavedNotice: String? = null
)

class AppearanceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppearanceUiState())
    val uiState: StateFlow<AppearanceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DiaryActivityStore.themeMode.collectLatest { mode ->
                _uiState.update { it.copy(selectedMode = mode) }
            }
        }
        viewModelScope.launch {
            DiaryActivityStore.themePalette.collectLatest { palette ->
                _uiState.update { it.copy(selectedPalette = palette) }
            }
        }
    }

    fun onThemeModeSelected(mode: String) {
        val currentPalette = _uiState.value.selectedPalette
        DiaryActivityStore.setTheme(mode, currentPalette)
        _uiState.update {
            it.copy(
                selectedMode = mode,
                previewSavedNotice = "Theme set to $mode mode."
            )
        }
    }

    fun onThemePaletteSelected(palette: ThemePaletteItem) {
        val currentMode = _uiState.value.selectedMode
        DiaryActivityStore.setTheme(currentMode, palette.name)
        _uiState.update {
            it.copy(
                selectedPalette = palette.name,
                previewSavedNotice = "Palette updated to ${palette.name}."
            )
        }
    }
}

