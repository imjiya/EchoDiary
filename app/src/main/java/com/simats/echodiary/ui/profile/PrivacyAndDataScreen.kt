package com.simats.echodiary.ui.profile

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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class PrivacyIconType {
    EXPORT, CLEAR, OPTIONS, POLICY
}

@Composable
fun PrivacyAndDataScreen(
    viewModel: PrivacyAndDataViewModel = remember { PrivacyAndDataViewModel() },
    onNavigateBack: () -> Unit
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

            // Header Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
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
                    text = "Privacy & data",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Screen Header Titles
            Text(
                text = "Stay close to your data.",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Review what you save, how summaries use it and the options for managing your diary.",
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
                    text = "Voice transcripts are reviewed before saving. Memory drafts use saved diary entries and can be edited. Moods are not assumed.",
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
                    subtitle = "Request a copy of your entries",
                    iconType = PrivacyIconType.EXPORT,
                    onClick = { }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Clear diary data
                PrivacyMenuItem(
                    title = "Clear diary data",
                    subtitle = "Review entries and memories to remove",
                    iconType = PrivacyIconType.CLEAR,
                    onClick = { }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Privacy options
                PrivacyMenuItem(
                    title = "Privacy options",
                    subtitle = "Review permissions and preferences",
                    iconType = PrivacyIconType.OPTIONS,
                    onClick = { }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Privacy policy
                PrivacyMenuItem(
                    title = "Privacy policy",
                    subtitle = "Read handling and retention details",
                    iconType = PrivacyIconType.POLICY,
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Info Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFEEF2FF), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Canvas(modifier = Modifier.size(20.dp).padding(top = 2.dp)) {
                    val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    val color = Color(0xFF5165F6)
                    drawCircle(color, radius = size.width * 0.42f, center = Offset(size.width * 0.5f, size.height * 0.5f), style = stroke)
                    drawCircle(color, radius = 1.2.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.32f))
                    drawLine(color, Offset(size.width * 0.5f, size.height * 0.48f), Offset(size.width * 0.5f, size.height * 0.72f), strokeWidth = stroke.width)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Conceptual controls only. Storage, export, retention and deletion details require a published policy.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 17.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Delete Account Button
            Button(
                onClick = { },
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

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PrivacyMenuItem(
    title: String,
    subtitle: String,
    iconType: PrivacyIconType,
    onClick: () -> Unit
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
                        // Top slider
                        drawLine(color, Offset(size.width * 0.15f, size.height * 0.3f), Offset(size.width * 0.85f, size.height * 0.3f), strokeWidth = stroke.width)
                        drawCircle(color, radius = 2.5.dp.toPx(), center = Offset(size.width * 0.4f, size.height * 0.3f))
                        // Middle slider
                        drawLine(color, Offset(size.width * 0.15f, size.height * 0.5f), Offset(size.width * 0.85f, size.height * 0.5f), strokeWidth = stroke.width)
                        drawCircle(color, radius = 2.5.dp.toPx(), center = Offset(size.width * 0.68f, size.height * 0.5f))
                        // Bottom slider
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
