package com.simats.echodiary.ui.profile

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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

@Composable
fun HelpAndSupportScreen(
    viewModel: HelpAndSupportViewModel = remember { HelpAndSupportViewModel() },
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val primaryColor = EchoTheme.primary

    // Run diagnostics automatically on launch
    LaunchedEffect(Unit) {
        viewModel.runDiagnostics(context)
    }

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
                    val backColor = EchoTheme.onSurface
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val color = backColor
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
                        text = "Help & Support",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface
                    )
                }

                IconButton(
                    onClick = { viewModel.copySupportInfo(context) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Text(text = "📋", fontSize = 18.sp)
                }
            }

            // Feedback Message Banner
            state.feedbackMessage?.let { msg ->
                Spacer(modifier = Modifier.height(12.dp))
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
                text = "A little help, anytime.",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Instant answers for capturing, reviewing memories & device troubleshooting.",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Search Bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EchoTheme.surface, RoundedCornerShape(24.dp)),
                placeholder = {
                    Text(
                        text = "Search questions or topics...",
                        color = EchoTheme.textSecondary,
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Text(text = "🔍", fontSize = 16.sp)
                },
                trailingIcon = {
                    if (state.searchQuery.isNotBlank()) {
                        Text(
                            text = "✕",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoTheme.textSecondary,
                            modifier = Modifier
                                .clickable { viewModel.onSearchQueryChanged("") }
                                .padding(8.dp)
                        )
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = EchoTheme.cardBorder,
                    focusedContainerColor = EchoTheme.surface,
                    unfocusedContainerColor = EchoTheme.surface,
                    focusedTextColor = EchoTheme.onSurface,
                    unfocusedTextColor = EchoTheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Category Chips
            val categories = listOf("All", "Journaling", "AI Memories", "Audio & Mic", "Storage & Privacy")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = state.selectedCategory == cat
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) primaryColor else EchoTheme.surface)
                            .border(1.dp, if (isSelected) primaryColor else EchoTheme.cardBorder, RoundedCornerShape(16.dp))
                            .clickable { viewModel.onCategorySelected(cat) }
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = cat,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else EchoTheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Frequently Asked Questions Section
            Text(
                text = "FREQUENTLY ASKED QUESTIONS (${state.filteredFaqs.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EchoTheme.textMuted,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (state.filteredFaqs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EchoTheme.surface, RoundedCornerShape(20.dp))
                        .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔎", fontSize = 28.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No answers found for \"${state.searchQuery}\"",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EchoTheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try searching for a different keyword or contact support below.",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EchoTheme.surface, RoundedCornerShape(20.dp))
                        .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(20.dp))
                        .padding(vertical = 12.dp, horizontal = 16.dp)
                ) {
                    state.filteredFaqs.forEachIndexed { index, faq ->
                        val isExpanded = state.expandedFaqIds.contains(faq.id)
                        ExpandableFaqCard(
                            faq = faq,
                            isExpanded = isExpanded,
                            onToggle = { viewModel.toggleFaq(faq.id) }
                        )
                        if (index < state.filteredFaqs.lastIndex) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(1.dp)
                                    .background(EchoTheme.cardBorder)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section: System Diagnostics & Troubleshooter
            Text(
                text = "SYSTEM DIAGNOSTICS & HARDWARE STATUS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EchoTheme.textMuted,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EchoTheme.surface, RoundedCornerShape(20.dp))
                    .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Hardware & Engine Health",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoTheme.onSurface
                        )
                        Button(
                            onClick = { viewModel.runDiagnostics(context) },
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EchoTheme.container,
                                contentColor = primaryColor
                            )
                        ) {
                            Text(text = "Re-check", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val report = state.diagnosticReport

                    // Diagnostic Item: Microphone Permission
                    DiagnosticRow(
                        icon = "🎙️",
                        title = "Microphone Permission",
                        statusText = if (report.micPermissionGranted) "Granted · Active" else "Denied · Tap to Grant",
                        isSuccess = report.micPermissionGranted,
                        action = if (!report.micPermissionGranted) {
                            { viewModel.openAppSettings(context) }
                        } else null
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Diagnostic Item: Local Sandbox Storage
                    DiagnosticRow(
                        icon = "💾",
                        title = "On-Device Storage Sandbox",
                        statusText = if (report.storageAvailable) "Accessible (${report.entriesCount} entries stored)" else "Storage Error",
                        isSuccess = report.storageAvailable
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Diagnostic Item: On-Device AI RAG Engine
                    DiagnosticRow(
                        icon = "🧠",
                        title = "On-Device AI RAG Engine",
                        statusText = report.ragEngineStatus,
                        isSuccess = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Diagnostic Item: Device Specs
                    DiagnosticRow(
                        icon = "📱",
                        title = "Device & Android Environment",
                        statusText = "${report.deviceModel} · ${report.androidVersion}",
                        isSuccess = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Guidance Card: How are memories made?
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.container, shape = RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✨", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "How are memories synthesized?",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Weekly, monthly, and yearly memories use on-device SLM synthesis and RAG embeddings to summarize your daily entries. Only explicitly recorded feelings are highlighted. You can freely edit drafts and attach photos anytime.",
                    fontSize = 13.sp,
                    color = EchoTheme.textSecondary,
                    lineHeight = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Contact Support & Actions
            Button(
                onClick = { viewModel.contactSupport(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "✉️ ", fontSize = 16.sp)
                    Text(
                        text = "Contact Support via Email",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.copySupportInfo(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EchoTheme.container,
                    contentColor = primaryColor
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(text = "📋 ", fontSize = 16.sp)
                    Text(
                        text = "Copy Support Email & Diagnostics",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App Version Footer
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "EchoDiary v1.0.0 (Build 2026.11) · 100% Private Local Storage",
                    fontSize = 12.sp,
                    color = EchoTheme.textMuted
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun ExpandableFaqCard(
    faq: FaqData,
    isExpanded: Boolean,
    onToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() }
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Category tag
                Box(
                    modifier = Modifier
                        .background(EchoTheme.container, RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = faq.category.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.primary,
                        letterSpacing = 0.5.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = faq.question,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = EchoTheme.onSurface
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            val arrowColor = if (isExpanded) EchoTheme.primary else EchoTheme.textMuted
            Canvas(modifier = Modifier.size(16.dp)) {
                val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                val color = arrowColor
                val path = Path().apply {
                    if (isExpanded) {
                        moveTo(size.width * 0.2f, size.height * 0.65f)
                        lineTo(size.width * 0.5f, size.height * 0.35f)
                        lineTo(size.width * 0.8f, size.height * 0.65f)
                    } else {
                        moveTo(size.width * 0.2f, size.height * 0.35f)
                        lineTo(size.width * 0.5f, size.height * 0.65f)
                        lineTo(size.width * 0.8f, size.height * 0.35f)
                    }
                }
                drawPath(path, color, style = stroke)
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Text(
                    text = faq.answer,
                    fontSize = 13.sp,
                    color = EchoTheme.textSecondary,
                    lineHeight = 19.sp
                )

                if (faq.tips.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    faq.tips.forEach { tip ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(text = "✓", fontSize = 12.sp, color = Color(0xFF10B981), fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tip,
                                fontSize = 12.sp,
                                color = EchoTheme.textSecondary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DiagnosticRow(
    icon: String,
    title: String,
    statusText: String,
    isSuccess: Boolean,
    action: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(EchoTheme.container, RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(text = icon, fontSize = 16.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EchoTheme.onSurface
                )
                Text(
                    text = statusText,
                    fontSize = 11.sp,
                    color = if (isSuccess) Color(0xFF059669) else Color(0xFFDC2626),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (action != null) {
            Button(
                onClick = action,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFFEE2E2),
                    contentColor = Color(0xFFDC2626)
                ),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Text(text = "Settings", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (isSuccess) Color(0xFF10B981) else Color(0xFFEF4444), CircleShape)
            )
        }
    }
}
