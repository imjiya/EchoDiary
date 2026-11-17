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
    val userName: String = "",
    val userInitials: String = "",
    val dateText: String = "",
    val subtitleText: String = "A little reflection goes a long way.",
    val selectedTab: Int = 0,
    val recentActivities: List<DiaryEntryItem> = emptyList(),
    val weeklyMemoryDateRange: String = "",
    val weeklyMemoryTitle: String = "",
    val weeklyMemorySubtitle: String = "",
    val entriesCount: Int = 0,
    val categoriesCount: Int = 0
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
        val todayDate = SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()).format(Date())

        executionScope.launch {
            DiaryActivityStore.userFullName.collectLatest { name ->
                val sessionName = try { EchoDiaryApp.getSessionManager().getUserName() } catch (_: Exception) { null }
                val resolvedName = when {
                    !sessionName.isNullOrBlank() -> sessionName
                    name.isNotBlank() -> name
                    else -> "Friend"
                }
                val firstWord = resolvedName.trim().split("\\s+".toRegex()).firstOrNull() ?: resolvedName
                _uiState.update { it.copy(userName = firstWord) }
            }
        }

        executionScope.launch {
            DiaryActivityStore.userInitials.collectLatest { initials ->
                val resolved = if (initials.isNotBlank()) initials else {
                    val sName = try { EchoDiaryApp.getSessionManager().getUserName() } catch (_: Exception) { null }
                    if (!sName.isNullOrBlank()) DiaryActivityStore.computeInitials(sName) else "U"
                }
                _uiState.update { it.copy(userInitials = resolved) }
            }
        }

        executionScope.launch {
            DiaryActivityStore.activities.collectLatest { activities ->
                val count = activities.size
                val cats = activities.map { it.category }.distinct().size
                val sub = if (count > 0) {
                    "$count entries · $cats categories · 1 saved memory"
                } else {
                    "No entries this week yet · Start your reflection"
                }
                _uiState.update {
                    it.copy(
                        recentActivities = activities.take(4),
                        entriesCount = count,
                        categoriesCount = cats,
                        weeklyMemorySubtitle = sub
                    )
                }
            }
        }

        executionScope.launch {
            DiaryActivityStore.weeklyMemoryTitle.collectLatest { title ->
                _uiState.update { it.copy(weeklyMemoryTitle = title) }
            }
        }

        executionScope.launch {
            DiaryActivityStore.weeklyMemoryDateRange.collectLatest { range ->
                _uiState.update { it.copy(weeklyMemoryDateRange = range) }
            }
        }

        _uiState.update { it.copy(dateText = todayDate) }

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
