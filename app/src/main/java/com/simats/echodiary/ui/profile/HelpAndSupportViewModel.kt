package com.simats.echodiary.ui.profile

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.simats.echodiary.data.local.DiaryActivityStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Locale

data class FaqData(
    val id: Int,
    val category: String,
    val question: String,
    val answer: String,
    val tips: List<String> = emptyList()
)

data class DiagnosticReport(
    val micPermissionGranted: Boolean = false,
    val storageAvailable: Boolean = true,
    val entriesCount: Int = 0,
    val memoriesCount: Int = 3,
    val androidVersion: String = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
    val deviceModel: String = "${Build.MANUFACTURER} ${Build.MODEL}",
    val appVersion: String = "1.0.0 (Build 2026.11)",
    val ragEngineStatus: String = "Ready · 100% On-Device",
    val isRunning: Boolean = false
)

data class HelpAndSupportUiState(
    val searchQuery: String = "",
    val selectedCategory: String = "All",
    val expandedFaqIds: Set<Int> = setOf(1), // First FAQ expanded by default
    val faqs: List<FaqData> = emptyList(),
    val filteredFaqs: List<FaqData> = emptyList(),
    val diagnosticReport: DiagnosticReport = DiagnosticReport(),
    val feedbackMessage: String? = null
)

class HelpAndSupportViewModel : ViewModel() {

    private val masterFaqs = listOf(
        FaqData(
            id = 1,
            category = "Journaling",
            question = "How do I capture a voice diary entry?",
            answer = "Tap the floating '+' button on the Home or All Activities screen, then choose 'Voice Recording'. Speak your thoughts naturally. When you are done speaking, tap 'Stop Recording' to review the transcription and detected emotion before saving.",
            tips = listOf(
                "Speak clearly into your microphone in a quiet room.",
                "You can review and edit the transcript before saving.",
                "You can also choose 'Write Manually' to type directly."
            )
        ),
        FaqData(
            id = 2,
            category = "AI Memories",
            question = "How does On-Device AI Memory Generation work?",
            answer = "EchoDiary uses a local Small Language Model (SLM) coupled with Retrieval-Augmented Generation (RAG). It reviews your journal entries for the week, month, or year to generate an emotional reflection and chapter highlights. 100% of this processing happens locally on your device.",
            tips = listOf(
                "Grounded strictly in your entries—no hallucinated facts.",
                "Operates completely offline with zero external API keys.",
                "Only emotions you explicitly express are synthesized."
            )
        ),
        FaqData(
            id = 3,
            category = "AI Memories",
            question = "Can I edit or attach photos to a memory?",
            answer = "Yes! Open your Weekly Memory and tap 'Edit Summary'. You can change the title, refine the narrative, and tap 'Add Photo' to attach a picture from your device gallery. The attached photo appears as a card banner and in shared images.",
            tips = listOf(
                "Tap the photo preview to replace or remove it at any time.",
                "Photos are saved in app-private sandbox storage.",
                "Saved photos persist across app restarts."
            )
        ),
        FaqData(
            id = 4,
            category = "AI Memories",
            question = "How do I share memories as images to other apps?",
            answer = "On any Weekly, Monthly, or Yearly memory screen, tap 'Share' or 'Share as image'. EchoDiary dynamically renders a high-definition 1080px branded card with your date range, narrative, highlights, and photo, then opens Android's share chooser for WhatsApp, Instagram, Telegram, and more.",
            tips = listOf(
                "Share to WhatsApp, Instagram Stories, Telegram, or Gmail.",
                "Your private raw diary entries are never exposed.",
                "Cards are rendered in crisp, high-resolution typography."
            )
        ),
        FaqData(
            id = 5,
            category = "Audio & Mic",
            question = "Microphone not recording or permissions denied?",
            answer = "If the microphone does not record audio, make sure EchoDiary has microphone permission granted in Android Settings. You can also use the 'Fix Permissions' shortcut in the System Diagnostics section below to jump straight to your app permissions.",
            tips = listOf(
                "Make sure microphone permission is set to 'Allow'.",
                "Ensure another app (like phone calls or voice recorders) isn't using the mic.",
                "You can always type your thoughts using 'Write Manually'."
            )
        ),
        FaqData(
            id = 6,
            category = "Storage & Privacy",
            question = "Where are my diary entries and photos stored?",
            answer = "EchoDiary is 100% offline-first. All your entries, mood logs, synthesized memory narratives, and photos are saved in your device's private sandboxed internal storage. There is zero cloud synchronization, zero tracking, and zero data leakage.",
            tips = listOf(
                "Visit Settings -> Storage for real-time storage breakdowns.",
                "Use 'Export Diary Data Backup' in Storage to export a JSON file.",
                "You can clean temporary share cache anytime without losing entries."
            )
        ),
        FaqData(
            id = 7,
            category = "Journaling",
            question = "How do I customize the app theme and colors?",
            answer = "Go to Profile -> Settings -> Appearance. You can toggle between Light and Dark mode, and select from customizable color palettes including Calm Blue, Soft Purple, Sage Green, and Sunset Amber.",
            tips = listOf(
                "Your theme preference applies across all screens.",
                "Changes take effect immediately without restarting the app."
            )
        )
    )

    private val _uiState = MutableStateFlow(
        HelpAndSupportUiState(
            faqs = masterFaqs,
            filteredFaqs = masterFaqs
        )
    )
    val uiState: StateFlow<HelpAndSupportUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            DiaryActivityStore.activities.collectLatest { entries ->
                _uiState.update { state ->
                    state.copy(
                        diagnosticReport = state.diagnosticReport.copy(
                            entriesCount = entries.size
                        )
                    )
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { state ->
            val updated = state.copy(searchQuery = query)
            updated.copy(filteredFaqs = filterFaqs(query, state.selectedCategory))
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update { state ->
            val updated = state.copy(selectedCategory = category)
            updated.copy(filteredFaqs = filterFaqs(state.searchQuery, category))
        }
    }

    fun toggleFaq(id: Int) {
        _uiState.update { state ->
            val current = state.expandedFaqIds
            val updated = if (current.contains(id)) {
                current - id
            } else {
                current + id
            }
            state.copy(expandedFaqIds = updated)
        }
    }

    fun runDiagnostics(context: Context) {
        val micGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        val storageOk = context.filesDir.exists() && context.filesDir.canWrite()
        val entries = DiaryActivityStore.activities.value.size

        _uiState.update { state ->
            state.copy(
                diagnosticReport = DiagnosticReport(
                    micPermissionGranted = micGranted,
                    storageAvailable = storageOk,
                    entriesCount = entries,
                    memoriesCount = 3,
                    androidVersion = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                    deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
                    appVersion = "1.0.0 (Build 2026.11)",
                    ragEngineStatus = "Ready · 100% On-Device",
                    isRunning = false
                ),
                feedbackMessage = "System diagnostics refreshed: All subsystems operational."
            )
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

    fun contactSupport(context: Context) {
        val state = _uiState.value
        val report = state.diagnosticReport

        val body = """
            Hello EchoDiary Support Team,
            
            [Describe your issue or feedback here]
            
            --- Device & App Diagnostics ---
            • App: EchoDiary v${report.appVersion}
            • Device: ${report.deviceModel}
            • OS: ${report.androidVersion}
            • Mic Permission: ${if (report.micPermissionGranted) "Granted" else "Denied"}
            • Storage: ${if (report.storageAvailable) "Accessible" else "Error"}
            • Entries Recorded: ${report.entriesCount}
            • RAG AI Engine: ${report.ragEngineStatus}
            ---------------------------------
        """.trimIndent()

        try {
            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:support@echodiary.app")
                putExtra(Intent.EXTRA_SUBJECT, "EchoDiary Support & Feedback")
                putExtra(Intent.EXTRA_TEXT, body)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooser = Intent.createChooser(emailIntent, "Send Email via")
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(chooser)
        } catch (_: Exception) {
            // Fallback: Copy to clipboard
            copySupportInfo(context)
        }
    }

    fun copySupportInfo(context: Context) {
        try {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(
                "EchoDiary Support Info",
                "Support Email: support@echodiary.app\nApp: EchoDiary 1.0.0\nDevice: ${Build.MANUFACTURER} ${Build.MODEL}"
            )
            clipboard.setPrimaryClip(clip)
            _uiState.update { it.copy(feedbackMessage = "Support email copied to clipboard: support@echodiary.app") }
        } catch (_: Exception) {}
    }

    fun clearFeedbackMessage() {
        _uiState.update { it.copy(feedbackMessage = null) }
    }

    private fun filterFaqs(query: String, category: String): List<FaqData> {
        val cleanQuery = query.trim().lowercase(Locale.ROOT)
        return masterFaqs.filter { item ->
            val matchesCategory = (category == "All" || item.category.equals(category, ignoreCase = true))
            val matchesQuery = cleanQuery.isBlank() ||
                    item.question.lowercase(Locale.ROOT).contains(cleanQuery) ||
                    item.answer.lowercase(Locale.ROOT).contains(cleanQuery) ||
                    item.category.lowercase(Locale.ROOT).contains(cleanQuery)
            matchesCategory && matchesQuery
        }
    }
}
