package com.simats.echodiary.ui.activity

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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MemoryDetailsScreen(
    viewModel: MemoryDetailsViewModel = remember { MemoryDetailsViewModel() },
    onNavigateBack: () -> Unit,
    onNavigateToActivityDetails: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = Color(0xFFFAFBFC)
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

            // Top Bar Header
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
                        text = "Memory details",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }

                // Share Button Icon
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(36.dp)
                ) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        val color = Color(0xFF5165F6)
                        drawCircle(color, radius = 2.5.dp.toPx(), center = Offset(size.width * 0.8f, size.height * 0.25f))
                        drawCircle(color, radius = 2.5.dp.toPx(), center = Offset(size.width * 0.2f, size.height * 0.5f))
                        drawCircle(color, radius = 2.5.dp.toPx(), center = Offset(size.width * 0.8f, size.height * 0.75f))
                        drawLine(color, Offset(size.width * 0.2f, size.height * 0.5f), Offset(size.width * 0.8f, size.height * 0.25f), strokeWidth = stroke.width)
                        drawLine(color, Offset(size.width * 0.2f, size.height * 0.5f), Offset(size.width * 0.8f, size.height * 0.75f), strokeWidth = stroke.width)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Badge / Tag
            Text(
                text = state.tagText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF5165F6),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Memory Title
            Text(
                text = state.title,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                lineHeight = 28.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFEEF2FF), shape = RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Text(
                    text = state.summaryText,
                    fontSize = 13.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 19.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Highlight Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.highlights.forEach { highlight ->
                    Box(
                        modifier = Modifier
                            .background(Color.White, shape = CircleShape)
                            .border(width = 1.dp, color = Color(0xFFE2E8F0), shape = CircleShape)
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = highlight,
                            fontSize = 13.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Source entries header
            Text(
                text = "${state.entries.size} source entries",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Source entries group card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                state.entries.forEachIndexed { index, entry ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToActivityDetails() }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Icon Circle
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(color = Color(0xFFEEF2FF), shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = entry.icon, fontSize = 16.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        // Title & Subtitle
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = entry.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = entry.subtitle,
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        // Right Chevron Arrow
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

                    if (index < state.entries.size - 1) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer Text
            Text(
                text = "Highlights reflect the records, not inferred moods.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
