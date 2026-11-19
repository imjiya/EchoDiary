package com.simats.echodiary.ui.profile

import android.content.Context
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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

@Composable
fun StorageScreen(
    viewModel: StorageViewModel = remember { StorageViewModel() },
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val primaryColor = EchoTheme.primary

    // Refresh storage stats on screen launch
    LaunchedEffect(Unit) {
        viewModel.refreshStorageStats(context)
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
                        text = "Storage & Memory",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface
                    )
                }

                IconButton(
                    onClick = { viewModel.refreshStorageStats(context) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Text(text = "🔄", fontSize = 18.sp)
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
                            fontWeight = EchoTheme.textMedium,
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

            // Hero Card: Storage Usage Summary
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EchoTheme.surface, RoundedCornerShape(24.dp))
                    .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(24.dp))
                    .padding(22.dp)
            ) {
                Column {
                    Text(
                        text = "ON-DEVICE STORAGE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = state.totalAppStorageText,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoTheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "used by EchoDiary",
                            fontSize = 14.sp,
                            color = EchoTheme.textSecondary,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "100% on-device private data · zero cloud synchronization",
                        fontSize = 12.sp,
                        color = EchoTheme.textSecondary
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Multi-Segment Visual Storage Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp)
                            .clip(RoundedCornerShape(7.dp))
                    ) {
                        // Entries segment
                        Box(
                            modifier = Modifier
                                .weight(state.entriesBreakdown.percentage.coerceAtLeast(0.05f))
                                .fillMaxSize()
                                .background(Color(state.entriesBreakdown.colorHex))
                        )
                        // Memories segment
                        Box(
                            modifier = Modifier
                                .weight(state.memoriesBreakdown.percentage.coerceAtLeast(0.05f))
                                .fillMaxSize()
                                .background(Color(state.memoriesBreakdown.colorHex))
                        )
                        // Media segment
                        Box(
                            modifier = Modifier
                                .weight(state.mediaBreakdown.percentage.coerceAtLeast(0.05f))
                                .fillMaxSize()
                                .background(Color(state.mediaBreakdown.colorHex))
                        )
                        // Cache segment
                        Box(
                            modifier = Modifier
                                .weight(state.cacheBreakdown.percentage.coerceAtLeast(0.05f))
                                .fillMaxSize()
                                .background(Color(state.cacheBreakdown.colorHex))
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Storage Legend (2x2 Grid)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StorageLegendItem(
                            color = Color(state.entriesBreakdown.colorHex),
                            label = "Entries",
                            sizeText = state.entriesBreakdown.sizeText,
                            modifier = Modifier.weight(1f)
                        )
                        StorageLegendItem(
                            color = Color(state.memoriesBreakdown.colorHex),
                            label = "AI Memories",
                            sizeText = state.memoriesBreakdown.sizeText,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StorageLegendItem(
                            color = Color(state.mediaBreakdown.colorHex),
                            label = "Photos",
                            sizeText = state.mediaBreakdown.sizeText,
                            modifier = Modifier.weight(1f)
                        )
                        StorageLegendItem(
                            color = Color(state.cacheBreakdown.colorHex),
                            label = "Cache",
                            sizeText = state.cacheBreakdown.sizeText,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Section Header: Detailed Breakdown
            Text(
                text = "STORAGE & MEMORY DETAILS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EchoTheme.textMuted,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Card 1: Diary Entries Details
            StorageDetailCard(
                icon = "📝",
                title = "Diary Entries",
                subtitle = state.entriesBreakdown.countText,
                sizeText = state.entriesBreakdown.sizeText,
                accentColor = Color(state.entriesBreakdown.colorHex),
                content = {
                    if (state.categoryBreakdown.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "Category Distribution",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EchoTheme.textSecondary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        state.categoryBreakdown.take(4).forEach { cat ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color(cat.colorHex), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = cat.name,
                                        fontSize = 13.sp,
                                        color = EchoTheme.onSurface
                                    )
                                }
                                Text(
                                    text = "${cat.count} (${(cat.percentage * 100).toInt()}%)",
                                    fontSize = 12.sp,
                                    fontWeight = EchoTheme.textMedium,
                                    color = EchoTheme.textSecondary
                                )
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Card 2: AI Synthesized Memories Details
            StorageDetailCard(
                icon = "🧠",
                title = "AI Memories & Summaries",
                subtitle = "On-device SLM weekly, monthly & yearly narratives",
                sizeText = state.memoriesBreakdown.sizeText,
                accentColor = Color(state.memoriesBreakdown.colorHex),
                content = {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MemoryTagChip(label = "Weekly Memory", active = state.weeklyMemoryTitle.isNotBlank(), modifier = Modifier.weight(1f))
                        MemoryTagChip(label = "Monthly Memory", active = state.monthlyMemoryTitle.isNotBlank(), modifier = Modifier.weight(1f))
                        MemoryTagChip(label = "Yearly Memory", active = state.yearlyMemoryTitle.isNotBlank(), modifier = Modifier.weight(1f))
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "🧠 100% On-device RAG embedding vectors & SLM synthesis index.",
                        fontSize = 12.sp,
                        color = EchoTheme.textSecondary,
                        lineHeight = 16.sp
                    )
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Card 3: Attached Photos & Media
            StorageDetailCard(
                icon = "🖼️",
                title = "Attached Photos & Media",
                subtitle = state.mediaBreakdown.countText,
                sizeText = state.mediaBreakdown.sizeText,
                accentColor = Color(state.mediaBreakdown.colorHex),
                content = {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Photos saved with your weekly, monthly and yearly memories are preserved in app-private sandboxed storage.",
                        fontSize = 12.sp,
                        color = EchoTheme.textSecondary,
                        lineHeight = 16.sp
                    )
                }
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Card 4: Temporary Cache & Buffers
            StorageDetailCard(
                icon = "⚡",
                title = "Temporary Cache",
                subtitle = "Shared memory images & cache files",
                sizeText = state.cacheBreakdown.sizeText,
                accentColor = Color(state.cacheBreakdown.colorHex),
                content = {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cleaning cache frees space without removing diary entries or memories.",
                            fontSize = 12.sp,
                            color = EchoTheme.textSecondary,
                            modifier = Modifier.weight(1f),
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = { viewModel.clearTemporaryCache(context) },
                            enabled = !state.isClearingCache,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EchoTheme.container,
                                contentColor = primaryColor
                            )
                        ) {
                            if (state.isClearingCache) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = primaryColor
                                )
                            } else {
                                Text(
                                    text = "Clean",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Device Overall Storage Section
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
                            text = "Device Storage",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoTheme.onSurface
                        )
                        Text(
                            text = "${state.deviceFreeStorageSummary} free",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryColor
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${state.deviceStorageSummary} used of ${state.deviceTotalStorageSummary}",
                        fontSize = 13.sp,
                        color = EchoTheme.textSecondary
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LinearProgressIndicator(
                        progress = { state.deviceUsedPercentage },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = primaryColor,
                        trackColor = if (EchoTheme.isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Privacy Guarantee Notice Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.container, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Canvas(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 2.dp)
                ) {
                    val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    val color = primaryColor
                    drawCircle(color, radius = size.width * 0.42f, center = Offset(size.width * 0.5f, size.height * 0.5f), style = stroke)
                    drawCircle(color, radius = 1.2.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.32f))
                    drawLine(color, Offset(size.width * 0.5f, size.height * 0.48f), Offset(size.width * 0.5f, size.height * 0.72f), strokeWidth = stroke.width)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "EchoDiary stores all entries, SLM memory narratives, and photos locally on your device. Your personal life stories never leave this device.",
                    fontSize = 12.sp,
                    color = EchoTheme.textSecondary,
                    lineHeight = 17.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Storage Management Action Buttons
            Button(
                onClick = { viewModel.clearTemporaryCache(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor),
                enabled = !state.isClearingCache
            ) {
                if (state.isClearingCache) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🧹 ", fontSize = 16.sp)
                        Text(
                            text = "Clean Temporary Cache",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.exportDiaryData(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.container),
                enabled = !state.isExporting
            ) {
                if (state.isExporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = primaryColor
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📤 ", fontSize = 16.sp)
                        Text(
                            text = "Export Diary Data Backup",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}

@Composable
private fun StorageLegendItem(
    color: Color,
    label: String,
    sizeText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = EchoTheme.textSecondary
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = sizeText,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = EchoTheme.onSurface
        )
    }
}

@Composable
private fun StorageDetailCard(
    icon: String,
    title: String,
    subtitle: String,
    sizeText: String,
    accentColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(EchoTheme.surface, RoundedCornerShape(20.dp))
            .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(20.dp))
            .padding(18.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(accentColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = icon, fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoTheme.onSurface
                        )
                        Text(
                            text = subtitle,
                            fontSize = 12.sp,
                            color = EchoTheme.textSecondary
                        )
                    }
                }

                Text(
                    text = sizeText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
            }

            content()
        }
    }
}

@Composable
private fun MemoryTagChip(
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = if (active) EchoTheme.container else EchoTheme.surface,
                shape = RoundedCornerShape(10.dp)
            )
            .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (active || EchoTheme.isDark) FontWeight.Bold else FontWeight.Normal,
            color = if (active) EchoTheme.primary else EchoTheme.textSecondary
        )
    }
}

