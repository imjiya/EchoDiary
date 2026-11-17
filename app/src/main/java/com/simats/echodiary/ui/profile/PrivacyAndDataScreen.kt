package com.simats.echodiary.ui.profile

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.echodiary.ui.theme.EchoTheme

private enum class PrivacyIconType {
    EXPORT, CLEAR, OPTIONS, POLICY
}

@Composable
fun PrivacyAndDataScreen(
    viewModel: PrivacyAndDataViewModel = remember { PrivacyAndDataViewModel() },
    onNavigateBack: () -> Unit,
    onNavigateToEmptyStates: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val primaryColor = EchoTheme.primary

    Scaffold(
        containerColor = EchoTheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val color = Color(0xFF1E293B)
                            val path = Path().apply {
                                moveTo(size.width * 0.8f, size.height * 0.5f)
                                lineTo(size.width * 0.2f, size.height * 0.5f)
                                moveTo(size.width * 0.45f, size.height * 0.2f)
                                lineTo(size.width * 0.2f, size.height * 0.5f)
                                lineTo(size.width * 0.45f, size.height * 0.8f)
                            }
                            drawPath(path, color, style = stroke)
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Privacy & Data",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🔒 100% Private",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }
            }

            // Feedback Message Banner
            state.feedbackMessage?.let { msg ->
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFECFDF5), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(text = "✅", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = msg,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF065F46)
                        )
                    }
                    Text(
                        text = "✕",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF065F46),
                        modifier = Modifier
                            .clickable { viewModel.clearFeedbackMessage() }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Screen Header Titles
            Text(
                text = "Stay close to your data.",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Review what you save, how summaries use it and your controls for managing your diary.",
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Banner Card: "Your entries are the source"
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFEEF2FF), shape = RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "Your entries are the source",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Voice transcripts are reviewed before saving. Memory drafts strictly use your saved diary entries and can be edited anytime. Moods are not assumed or hallucinated.",
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Options Group Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                // Export diary
                PrivacyMenuItem(
                    title = "Export diary",
                    subtitle = "Request a copy of your entries & memories",
                    iconType = PrivacyIconType.EXPORT,
                    onClick = { viewModel.exportDiaryData(context) },
                    trailingContent = {
                        if (state.isExporting) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = primaryColor
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Clear diary data
                PrivacyMenuItem(
                    title = "Clear diary data",
                    subtitle = "Review entries and memories to remove",
                    iconType = PrivacyIconType.CLEAR,
                    onClick = { viewModel.setShowClearDataDialog(true) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Privacy options
                PrivacyMenuItem(
                    title = "Privacy options",
                    subtitle = "Review permissions and preferences",
                    iconType = PrivacyIconType.OPTIONS,
                    onClick = { viewModel.setShowPrivacyOptionsDialog(true) }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Privacy policy
                PrivacyMenuItem(
                    title = "Privacy policy",
                    subtitle = "Read handling and retention details",
                    iconType = PrivacyIconType.POLICY,
                    onClick = { viewModel.setShowPrivacyPolicyDialog(true) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Verified Privacy Guarantee Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFEEF2FF), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Canvas(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 2.dp)
                ) {
                    val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    val color = Color(0xFF5165F6)
                    drawCircle(color, radius = size.width * 0.42f, center = Offset(size.width * 0.5f, size.height * 0.5f), style = stroke)
                    drawCircle(color, radius = 1.2.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.32f))
                    drawLine(color, Offset(size.width * 0.5f, size.height * 0.48f), Offset(size.width * 0.5f, size.height * 0.72f), strokeWidth = stroke.width)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "🔒 100% On-Device Privacy Guarantee: EchoDiary never uploads your journal entries, speech recordings, or photos to cloud servers. All AI memory synthesis and data storage remain strictly on this device.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 17.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Delete Account Button
            Button(
                onClick = { viewModel.setShowDeleteAccountDialog(true) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEF2F2),
                    contentColor = Color(0xFFEF4444)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Canvas(modifier = Modifier.size(18.dp)) {
                        val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        val color = Color(0xFFEF4444)
                        // Trash bin lid
                        drawLine(color, Offset(size.width * 0.15f, size.height * 0.28f), Offset(size.width * 0.85f, size.height * 0.28f), strokeWidth = stroke.width)
                        drawLine(color, Offset(size.width * 0.35f, size.height * 0.18f), Offset(size.width * 0.65f, size.height * 0.18f), strokeWidth = stroke.width)
                        // Trash bin bucket
                        val path = Path().apply {
                            moveTo(size.width * 0.25f, size.height * 0.28f)
                            lineTo(size.width * 0.28f, size.height * 0.85f)
                            lineTo(size.width * 0.72f, size.height * 0.85f)
                            lineTo(size.width * 0.75f, size.height * 0.28f)
                        }
                        drawPath(path, color, style = stroke)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Delete account",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // ------------------------------------------------------------------------
    // Dialog 1: Clear Diary Data
    // ------------------------------------------------------------------------
    if (state.showClearDataDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowClearDataDialog(false) },
            title = {
                Text(
                    text = "Clear Diary Data",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            },
            text = {
                Column {
                    Text(
                        text = "Choose whether you want to reset synthesized memories or permanently delete all diary entries.",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = { viewModel.clearSynthesizedMemories() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.container)
                    ) {
                        Text(
                            text = "Reset Synthesized Memories Only",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = { viewModel.clearAllDiaryData() },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEE2E2))
                    ) {
                        Text(
                            text = "Erase All Entries & Memories",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { viewModel.setShowClearDataDialog(false) }) {
                    Text(text = "Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // ------------------------------------------------------------------------
    // Dialog 2: Privacy Options & Preferences
    // ------------------------------------------------------------------------
    if (state.showPrivacyOptionsDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowPrivacyOptionsDialog(false) },
            title = {
                Text(
                    text = "Privacy Preferences",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            },
            text = {
                Column {
                    // Option 1: On-Device AI
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "On-Device AI Processing",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Guarantees SLM memory synthesis runs locally without cloud servers.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = state.localAiOnly,
                            onCheckedChange = { viewModel.setLocalAiOnly(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = primaryColor)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Option 2: App Lock
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "App Lock / Biometrics",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "Require device authentication to view sensitive diary entries.",
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Switch(
                            checked = state.appLockEnabled,
                            onCheckedChange = { viewModel.setAppLockEnabled(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = primaryColor)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Option 3: Analytics Disabled
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(12.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🛡️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Third-Party Analytics: Disabled",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF059669)
                            )
                            Text(
                                text = "EchoDiary does not include telemetry or advertising SDKs.",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Permission shortcut
                    Button(
                        onClick = { viewModel.openAppSettings(context) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.container)
                    ) {
                        Text(
                            text = "Manage Microphone & System Permissions",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryColor
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.setShowPrivacyOptionsDialog(false) }) {
                    Text(text = "Done", color = primaryColor, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // ------------------------------------------------------------------------
    // Dialog 3: Privacy Policy Full Text
    // ------------------------------------------------------------------------
    if (state.showPrivacyPolicyDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowPrivacyPolicyDialog(false) },
            title = {
                Column {
                    Text(
                        text = "EchoDiary Privacy Policy",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Published November 2026 · Built for Complete Privacy",
                        fontSize = 11.sp,
                        color = Color(0xFF64748B)
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .height(300.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    PolicySection(
                        title = "1. 100% Local Storage Architecture",
                        text = "All journal entries, mood records, dates, and category tags are saved directly in an encrypted SQLite database within your device's sandboxed storage. No remote servers ever receive this data."
                    )
                    PolicySection(
                        title = "2. On-Device Speech & AI Synthesis",
                        text = "Voice transcription and weekly/monthly/yearly memory summaries use local on-device SLM and RAG algorithms. Voice audio is processed locally and discarded immediately after transcription."
                    )
                    PolicySection(
                        title = "3. Sandboxed Photos & Media",
                        text = "Images attached to diary memories are stored locally in the app's internal files directory. They are never backed up to remote cloud servers."
                    )
                    PolicySection(
                        title = "4. Zero Analytics & Tracking",
                        text = "EchoDiary does not contain tracking pixels, analytics SDKs, advertising frameworks, or behavior identifiers."
                    )
                    PolicySection(
                        title = "5. Right to Data Export & Erasure",
                        text = "You retain absolute ownership of your thoughts. You may export your entire journal in structured JSON format or permanently erase all records with a single tap."
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { viewModel.setShowPrivacyPolicyDialog(false) }) {
                    Text(text = "I Understand", color = primaryColor, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }

    // ------------------------------------------------------------------------
    // Dialog 4: Delete Account Confirmation
    // ------------------------------------------------------------------------
    if (state.showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.setShowDeleteAccountDialog(false) },
            title = {
                Text(
                    text = "Delete Account & All Data?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFDC2626)
                )
            },
            text = {
                Text(
                    text = "Are you sure? This will permanently erase all your diary entries, AI memories, attached photos, and login credentials from this device.\n\nThis action cannot be undone.",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteAccount(context) {
                            onNavigateToLogin()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Delete Everything", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.setShowDeleteAccountDialog(false) }) {
                    Text(text = "Cancel", color = Color(0xFF64748B))
                }
            },
            containerColor = Color.White,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun PolicySection(title: String, text: String) {
    Spacer(modifier = Modifier.height(10.dp))
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1E293B)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = text,
        fontSize = 12.sp,
        color = Color(0xFF64748B),
        lineHeight = 17.sp
    )
}

@Composable
private fun PrivacyMenuItem(
    title: String,
    subtitle: String,
    iconType: PrivacyIconType,
    onClick: () -> Unit,
    trailingContent: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Circle
        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color = Color(0xFFEEF2FF), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(22.dp)) {
                val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                val color = Color(0xFF5165F6)

                when (iconType) {
                    PrivacyIconType.EXPORT -> {
                        // Download tray icon
                        val path = Path().apply {
                            moveTo(size.width * 0.5f, size.height * 0.15f)
                            lineTo(size.width * 0.5f, size.height * 0.62f)
                            moveTo(size.width * 0.3f, size.height * 0.45f)
                            lineTo(size.width * 0.5f, size.height * 0.65f)
                            lineTo(size.width * 0.7f, size.height * 0.45f)
                        }
                        drawPath(path, color, style = stroke)
                        val tray = Path().apply {
                            moveTo(size.width * 0.18f, size.height * 0.65f)
                            lineTo(size.width * 0.18f, size.height * 0.82f)
                            lineTo(size.width * 0.82f, size.height * 0.82f)
                            lineTo(size.width * 0.82f, size.height * 0.65f)
                        }
                        drawPath(tray, color, style = stroke)
                    }
                    PrivacyIconType.CLEAR -> {
                        // Eraser / Clear icon
                        val path = Path().apply {
                            moveTo(size.width * 0.3f, size.height * 0.8f)
                            lineTo(size.width * 0.15f, size.height * 0.62f)
                            lineTo(size.width * 0.55f, size.height * 0.2f)
                            lineTo(size.width * 0.85f, size.height * 0.5f)
                            lineTo(size.width * 0.55f, size.height * 0.8f)
                            close()
                        }
                        drawPath(path, color, style = stroke)
                        drawLine(color, Offset(size.width * 0.35f, size.height * 0.42f), Offset(size.width * 0.65f, size.height * 0.72f), strokeWidth = stroke.width)
                    }
                    PrivacyIconType.OPTIONS -> {
                        // Sliders icon
                        drawLine(color, Offset(size.width * 0.15f, size.height * 0.3f), Offset(size.width * 0.85f, size.height * 0.3f), strokeWidth = stroke.width)
                        drawCircle(color, radius = 2.5.dp.toPx(), center = Offset(size.width * 0.4f, size.height * 0.3f))
                        drawLine(color, Offset(size.width * 0.15f, size.height * 0.5f), Offset(size.width * 0.85f, size.height * 0.5f), strokeWidth = stroke.width)
                        drawCircle(color, radius = 2.5.dp.toPx(), center = Offset(size.width * 0.68f, size.height * 0.5f))
                        drawLine(color, Offset(size.width * 0.15f, size.height * 0.7f), Offset(size.width * 0.85f, size.height * 0.7f), strokeWidth = stroke.width)
                        drawCircle(color, radius = 2.5.dp.toPx(), center = Offset(size.width * 0.32f, size.height * 0.7f))
                    }
                    PrivacyIconType.POLICY -> {
                        // Document / File text icon
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(size.width * 0.2f, size.height * 0.15f),
                            size = Size(size.width * 0.6f, size.height * 0.72f),
                            cornerRadius = CornerRadius(3.dp.toPx()),
                            style = stroke
                        )
                        drawLine(color, Offset(size.width * 0.32f, size.height * 0.35f), Offset(size.width * 0.68f, size.height * 0.35f), strokeWidth = stroke.width)
                        drawLine(color, Offset(size.width * 0.32f, size.height * 0.5f), Offset(size.width * 0.68f, size.height * 0.5f), strokeWidth = stroke.width)
                        drawLine(color, Offset(size.width * 0.32f, size.height * 0.65f), Offset(size.width * 0.52f, size.height * 0.65f), strokeWidth = stroke.width)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Title and Subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        if (trailingContent != null) {
            trailingContent()
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Chevron Right Arrow
        Canvas(modifier = Modifier.size(16.dp)) {
            val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
            val path = Path().apply {
                moveTo(size.width * 0.35f, size.height * 0.2f)
                lineTo(size.width * 0.7f, size.height * 0.5f)
                lineTo(size.width * 0.35f, size.height * 0.8f)
            }
            drawPath(path, Color(0xFF94A3B8), style = stroke)
        }
    }
}
