package com.simats.echodiary.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.EchoDiaryApp
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.data.repository.EntryRepository
import com.simats.echodiary.ui.activity.DiaryEntryItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HomeUiState(
    val userName: String = "Alex",
    val dateText: String = "Sunday, Oct 11, 2026",
    val subtitleText: String = "A little reflection goes a long way.",
    val selectedTab: Int = 0,
    val recentActivities: List<DiaryEntryItem> = emptyList()
)

class HomeViewModel(
    private val entryRepository: EntryRepository? = null,
    scope: CoroutineScope? = null
) : ViewModel() {

    private val executionScope: CoroutineScope = scope ?: viewModelScope

    private fun getRepository(): EntryRepository? {
        return entryRepository ?: try {
            EchoDiaryApp.getEntryRepository()
        } catch (_: Exception) {
            null
        }
    }

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        val sessionName = try { EchoDiaryApp.getSessionManager().getUserName() } catch (_: Exception) { null }
        val displayName = if (!sessionName.isNullOrBlank()) {
            sessionName.trim().split("\\s+".toRegex()).firstOrNull() ?: sessionName
        } else {
            DiaryActivityStore.userFullName.value.split(" ").firstOrNull() ?: "Alex"
        }

        val todayDate = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()).format(Date())

        _uiState.value = _uiState.value.copy(
            userName = displayName,
            dateText = todayDate
        )

        executionScope.launch {
            DiaryActivityStore.activities.collectLatest { activities ->
                _uiState.update { it.copy(recentActivities = activities.take(4)) }
            }
        }

        // MVVM Cache: Only fetch from backend if not already loaded, avoiding repeated API hits on every visit
        loadEntries(forceRefresh = false)
    }

    fun loadEntries(forceRefresh: Boolean = false) {
        if (!forceRefresh && DiaryActivityStore.isLoadedFromBackend.value) {
            // Already cached in memory; do not hit API on repeated navigation
            return
        }
        val repo = getRepository() ?: return
        executionScope.launch {
            repo.getEntries(forceRefresh = forceRefresh)
        }
    }

    fun onTabSelected(index: Int) {
        _uiState.value = _uiState.value.copy(selectedTab = index)
    }
}
