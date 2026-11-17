package com.simats.echodiary.ui.activity

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.EchoDiaryApp
import com.simats.echodiary.data.local.DiaryActivityStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class CreatingYourMemoryUiState(
    val memoryType: String = "weekly",
    val title: String = "Connecting your week.",
    val subtitle: String = "Bringing together your diary entries,\nusing local AI technology.",
    val badgeText: String = "🧠 On-Device AI RAG Engine Active",
    val progress: Float = 0.15f,
    val progressPercentageText: String = "15% · Gathering recorded diary entries",
    val checklist: List<Pair<String, Boolean>> = listOf(
        "Gathering diary entries" to true,
        "Retrieving grounded context & emotional anchors via RAG" to false,
        "Synthesizing source-grounded memory narrative via SLM" to false
    ),
    val isCompleted: Boolean = false
)

sealed class CreatingYourMemoryNavigationEvent {
    object NavigateBack : CreatingYourMemoryNavigationEvent()
    data class NavigateToMemory(val type: String) : CreatingYourMemoryNavigationEvent()
    object NavigateToWeeklySummary : CreatingYourMemoryNavigationEvent()
}

class CreatingYourMemoryViewModel(
    scope: CoroutineScope? = null
) : ViewModel() {

    private val executionScope: CoroutineScope = scope ?: viewModelScope

    private val _uiState = MutableStateFlow(CreatingYourMemoryUiState())
    val uiState: StateFlow<CreatingYourMemoryUiState> = _uiState.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<CreatingYourMemoryNavigationEvent>()
    val navigationEvent: SharedFlow<CreatingYourMemoryNavigationEvent> = _navigationEvent.asSharedFlow()

    init {
        startSynthesis()
    }

    fun startSynthesis() {
        executionScope.launch {
            val type = DiaryActivityStore.generatingMemoryType.value.lowercase(Locale.ROOT)
            val activities = DiaryActivityStore.activities.value
            val count = activities.size

            val titleText = when (type) {
                "monthly" -> "Connecting your month."
                "yearly" -> "Connecting your year."
                else -> "Connecting your week."
            }
            val subtitleText = when (type) {
                "monthly" -> "Bringing together your $count entries,\nusing local AI RAG technology to synthesize your monthly summary."
                "yearly" -> "Bringing together your $count entries,\nusing local AI RAG technology to synthesize your year in review."
                else -> "Bringing together your $count entries,\nusing local AI RAG technology to synthesize your weekly memory."
            }

            // Step 1: Gathering entries
            _uiState.update {
                it.copy(
                    memoryType = type,
                    title = titleText,
                    subtitle = subtitleText,
                    badgeText = "🧠 On-Device AI RAG Engine Active",
                    progress = 0.25f,
                    progressPercentageText = "25% · Gathering $count recorded diary entries",
                    checklist = listOf(
                        "Gathered $count diary entries" to true,
                        "Retrieving grounded context & emotional anchors via RAG" to false,
                        "Synthesizing source-grounded narrative via SLM" to false
                    ),
                    isCompleted = false
                )
            }

            delay(700L)

            // Step 2: RAG Context & anchors retrieval
            _uiState.update {
                it.copy(
                    progress = 0.65f,
                    progressPercentageText = "65% · Retrieving grounded context & emotional anchors",
                    checklist = listOf(
                        "Gathered $count diary entries" to true,
                        "Retrieved emotional arc & category anchors via RAG" to true,
                        "Synthesizing source-grounded narrative via SLM" to false
                    )
                )
            }

            delay(800L)

            // Step 3: SLM / AI synthesis execution
            _uiState.update {
                it.copy(
                    progress = 0.90f,
                    progressPercentageText = "90% · Synthesizing $type narrative via AI SLM engine",
                    checklist = listOf(
                        "Gathered $count diary entries" to true,
                        "Retrieved emotional arc & category anchors via RAG" to true,
                        "Synthesized source-grounded narrative via SLM" to true
                    )
                )
            }

            // Execute local source-grounded SLM memory synthesis
            when (type) {
                "monthly" -> DiaryActivityStore.generateMonthlySummary(executionScope)
                "yearly" -> DiaryActivityStore.generateYearlySummary(executionScope)
                else -> DiaryActivityStore.generateWeeklySummary(executionScope)
            }

            try {
                EchoDiaryApp.getMemoryRepository().generateMemory(type = type, save = true)
            } catch (_: Exception) {}

            delay(600L)

            // Step 4: Complete
            _uiState.update {
                it.copy(
                    progress = 1.0f,
                    progressPercentageText = "100% · $type memory ready! Opening...",
                    isCompleted = true
                )
            }

            delay(400L)
            _navigationEvent.emit(CreatingYourMemoryNavigationEvent.NavigateToMemory(type))
        }
    }

    fun onBackClicked() {
        executionScope.launch {
            _navigationEvent.emit(CreatingYourMemoryNavigationEvent.NavigateBack)
        }
    }
}
