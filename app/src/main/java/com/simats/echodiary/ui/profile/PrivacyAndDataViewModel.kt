package com.simats.echodiary.ui.profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.data.local.SessionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PrivacyAndDataUiState(
    val exportAvailable: Boolean = true,
    val entriesCount: Int = 0,
    val memoriesCount: Int = 3,
    val isExporting: Boolean = false,
    val showClearDataDialog: Boolean = false,
    val showPrivacyOptionsDialog: Boolean = false,
    val showPrivacyPolicyDialog: Boolean = false,
    val showDeleteAccountDialog: Boolean = false,
    val localAiOnly: Boolean = true,
    val appLockEnabled: Boolean = false,
    val analyticsDisabled: Boolean = true,
    val feedbackMessage: String? = null
)

class PrivacyAndDataViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PrivacyAndDataUiState())
    val uiState: StateFlow<PrivacyAndDataUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DiaryActivityStore.activities.collectLatest { entries ->
                _uiState.update { it.copy(entriesCount = entries.size) }
            }
        }
    }

    fun setShowClearDataDialog(show: Boolean) {
        _uiState.update { it.copy(showClearDataDialog = show) }
    }

    fun setShowPrivacyOptionsDialog(show: Boolean) {
        _uiState.update { it.copy(showPrivacyOptionsDialog = show) }
    }

    fun setShowPrivacyPolicyDialog(show: Boolean) {
        _uiState.update { it.copy(showPrivacyPolicyDialog = show) }
    }

    fun setShowDeleteAccountDialog(show: Boolean) {
        _uiState.update { it.copy(showDeleteAccountDialog = show) }
    }

    fun setLocalAiOnly(enabled: Boolean) {
        _uiState.update { it.copy(localAiOnly = enabled) }
    }

    fun setAppLockEnabled(enabled: Boolean) {
        _uiState.update { it.copy(appLockEnabled = enabled) }
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    fun exportDiaryData(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isExporting = true) }
            val entries = DiaryActivityStore.activities.value

            val exportJson = JSONObject()
            exportJson.put("app", "EchoDiary")
            exportJson.put("export_type", "Privacy & Data Export")
            exportJson.put("timestamp", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()))
            exportJson.put("user_name", DiaryActivityStore.userFullName.value)
            exportJson.put("user_email", DiaryActivityStore.userEmail.value)
            exportJson.put("total_entries", entries.size)

            val entriesArray = JSONArray()
            for (entry in entries) {
                val obj = JSONObject()
                obj.put("id", entry.id)
                obj.put("title", entry.title)
                obj.put("description", entry.description)
                obj.put("category", entry.category)
                obj.put("mood", entry.mood)
                obj.put("date", entry.date)
                entriesArray.put(obj)
            }
            exportJson.put("entries", entriesArray)

            val memoriesObj = JSONObject()
            memoriesObj.put("weekly_memory_title", DiaryActivityStore.weeklyMemoryTitle.value)
            memoriesObj.put("weekly_memory_narrative", DiaryActivityStore.weeklyMemoryNarrative.value)
            memoriesObj.put("monthly_memory_title", DiaryActivityStore.monthlySummaryTitle.value)
            memoriesObj.put("monthly_memory_narrative", DiaryActivityStore.monthlySummaryText.value)
            memoriesObj.put("yearly_memory_title", DiaryActivityStore.yearlyChapterTitle.value)
            memoriesObj.put("yearly_memory_narrative", DiaryActivityStore.yearlyChapterText.value)
            exportJson.put("synthesized_memories", memoriesObj)

            withContext(Dispatchers.Main) {
                _uiState.update {
                    it.copy(
                        isExporting = false,
                        feedbackMessage = "Diary copy generated! Opening share sheet."
                    )
                }
                try {
                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, "EchoDiary Privacy Export - ${SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())}")
                        putExtra(Intent.EXTRA_TEXT, exportJson.toString(2))
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    val chooser = Intent.createChooser(shareIntent, "Export Diary Data")
                    chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(chooser)
                } catch (_: Exception) {}
            }
        }
    }

    fun clearSynthesizedMemories() {
        viewModelScope.launch {
            DiaryActivityStore.resetMemories()
            _uiState.update {
                it.copy(
                    showClearDataDialog = false,
                    feedbackMessage = "Synthesized memories have been reset. Your raw entries are preserved."
                )
            }
        }
    }

    fun clearAllDiaryData() {
        viewModelScope.launch {
            DiaryActivityStore.clearData()
            _uiState.update {
                it.copy(
                    showClearDataDialog = false,
                    feedbackMessage = "All diary entries and memories have been permanently cleared."
                )
            }
        }
    }

    fun deleteAccount(context: Context, onComplete: () -> Unit) {
        viewModelScope.launch {
            DiaryActivityStore.clearData()
            SessionManager.getInstance(context).clearSession()
            _uiState.update {
                it.copy(
                    showDeleteAccountDialog = false,
                    feedbackMessage = "Account and data deleted."
                )
            }
            onComplete()
        }
    }

    fun openAppSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Exception) {}
    }
}
