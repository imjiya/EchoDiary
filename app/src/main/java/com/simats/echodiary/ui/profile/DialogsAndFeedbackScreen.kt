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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class DialogType {
    DELETE_ENTRY,
    DELETE_ACCOUNT,
    LOGOUT,
    MIC_PERMISSION,
    SPEECH_FAILURE,
    NO_INTERNET,
    AI_FAILURE,
    INVALID_FIELDS,
    ENTRY_SAVED,
    MEMORY_SAVED
}

@Composable
fun DialogsAndFeedbackScreen(
    viewModel: DialogsAndFeedbackViewModel = remember { DialogsAndFeedbackViewModel() },
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
                    text = state.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Subheader
            Text(
                text = "Clear choices, helpful errors and quiet confirmations.",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Item 1: Delete entry
            DialogItemCard(
                title = "Delete entry",
                subtitle = "Delete entry / Cancel",
                dialogType = DialogType.DELETE_ENTRY
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Item 2: Delete account
            DialogItemCard(
                title = "Delete account",
                subtitle = "Delete account / Keep account",
                dialogType = DialogType.DELETE_ACCOUNT
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Item 3: Logout
            DialogItemCard(
                title = "Logout",
                subtitle = "Log out / Stay here",
                dialogType = DialogType.LOGOUT
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Item 4: Mic permission
            DialogItemCard(
                title = "Mic permission",
                subtitle = "Allow access / Write manually",
                dialogType = DialogType.MIC_PERMISSION
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Item 5: Speech failure
            DialogItemCard(
                title = "Speech failure",
                subtitle = "Try again / Write manually",
                dialogType = DialogType.SPEECH_FAILURE
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Item 6: No internet
            DialogItemCard(
                title = "No internet",
                subtitle = "Try again / Close",
                dialogType = DialogType.NO_INTERNET
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Item 7: AI failure
            DialogItemCard(
                title = "AI failure",
                subtitle = "Retry / Not now",
                dialogType = DialogType.AI_FAILURE
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Item 8: Invalid fields
            DialogItemCard(
                title = "Invalid fields",
                subtitle = "Review fields",
                dialogType = DialogType.INVALID_FIELDS
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Item 9: Entry saved
            DialogItemCard(
                title = "Entry saved",
                subtitle = "View entry / Done",
                dialogType = DialogType.ENTRY_SAVED
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Item 10: Memory saved
            DialogItemCard(
                title = "Memory saved",
                subtitle = "View memory / Done",
                dialogType = DialogType.MEMORY_SAVED
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Footer Text
            Text(
                text = "Full readable dialogs are in the component library.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DialogItemCard(
    title: String,
    subtitle: String,
    dialogType: DialogType
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(20.dp))
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val (bgColor, iconColor) = when (dialogType) {
            DialogType.DELETE_ENTRY, DialogType.DELETE_ACCOUNT -> Pair(Color(0xFFFEF2F2), Color(0xFFEF4444))
            DialogType.ENTRY_SAVED, DialogType.MEMORY_SAVED -> Pair(Color(0xFFF0FDF4), Color(0xFF22C55E))
            else -> Pair(Color(0xFFEEF2FF), Color(0xFF5165F6))
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .background(color = bgColor, shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(22.dp)) {
                val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

                when (dialogType) {
                    DialogType.DELETE_ENTRY -> {
                        // Trash icon
                        drawLine(iconColor, Offset(size.width * 0.15f, size.height * 0.28f), Offset(size.width * 0.85f, size.height * 0.28f), strokeWidth = stroke.width)
                        drawLine(iconColor, Offset(size.width * 0.35f, size.height * 0.18f), Offset(size.width * 0.65f, size.height * 0.18f), strokeWidth = stroke.width)
                        val path = Path().apply {
                            moveTo(size.width * 0.25f, size.height * 0.28f)
                            lineTo(size.width * 0.28f, size.height * 0.85f)
                            lineTo(size.width * 0.72f, size.height * 0.85f)
                            lineTo(size.width * 0.75f, size.height * 0.28f)
                        }
                        drawPath(path, iconColor, style = stroke)
                    }
                    DialogType.DELETE_ACCOUNT -> {
                        // Person with X icon
                        drawCircle(iconColor, radius = size.width * 0.2f, center = Offset(size.width * 0.4f, size.height * 0.32f), style = stroke)
                        drawArc(iconColor, 0f, 180f, false, topLeft = Offset(size.width * 0.1f, size.height * 0.55f), size = Size(size.width * 0.6f, size.height * 0.38f), style = stroke)
                        // X mark on top right
                        drawLine(iconColor, Offset(size.width * 0.7f, size.height * 0.25f), Offset(size.width * 0.9f, size.height * 0.45f), strokeWidth = stroke.width)
                        drawLine(iconColor, Offset(size.width * 0.9f, size.height * 0.25f), Offset(size.width * 0.7f, size.height * 0.45f), strokeWidth = stroke.width)
                    }
                    DialogType.LOGOUT -> {
                        // Exit door icon
                        val path = Path().apply {
                            moveTo(size.width * 0.5f, size.height * 0.2f)
                            lineTo(size.width * 0.2f, size.height * 0.2f)
                            lineTo(size.width * 0.2f, size.height * 0.8f)
                            lineTo(size.width * 0.5f, size.height * 0.8f)
                        }
                        drawPath(path, iconColor, style = stroke)
                        val arrow = Path().apply {
                            moveTo(size.width * 0.45f, size.height * 0.5f)
                            lineTo(size.width * 0.85f, size.height * 0.5f)
                            moveTo(size.width * 0.68f, size.height * 0.32f)
                            lineTo(size.width * 0.85f, size.height * 0.5f)
                            lineTo(size.width * 0.68f, size.height * 0.68f)
                        }
                        drawPath(arrow, iconColor, style = stroke)
                    }
                    DialogType.MIC_PERMISSION -> {
                        // Mic icon
                        drawRoundRect(iconColor, topLeft = Offset(size.width * 0.38f, size.height * 0.15f), size = Size(size.width * 0.24f, size.height * 0.45f), cornerRadius = CornerRadius(6.dp.toPx()), style = stroke)
                        drawArc(iconColor, 0f, 180f, false, topLeft = Offset(size.width * 0.25f, size.height * 0.35f), size = Size(size.width * 0.5f, size.height * 0.38f), style = stroke)
                        drawLine(iconColor, Offset(size.width * 0.5f, size.height * 0.73f), Offset(size.width * 0.5f, size.height * 0.88f), strokeWidth = stroke.width)
                    }
                    DialogType.SPEECH_FAILURE -> {
                        // Sound waves icon
                        drawLine(iconColor, Offset(size.width * 0.2f, size.height * 0.4f), Offset(size.width * 0.2f, size.height * 0.6f), strokeWidth = stroke.width)
                        drawLine(iconColor, Offset(size.width * 0.4f, size.height * 0.2f), Offset(size.width * 0.4f, size.height * 0.8f), strokeWidth = stroke.width)
                        drawLine(iconColor, Offset(size.width * 0.6f, size.height * 0.3f), Offset(size.width * 0.6f, size.height * 0.7f), strokeWidth = stroke.width)
                        drawLine(iconColor, Offset(size.width * 0.8f, size.height * 0.4f), Offset(size.width * 0.8f, size.height * 0.6f), strokeWidth = stroke.width)
                    }
                    DialogType.NO_INTERNET -> {
                        // Wifi off icon
                        val arc1 = Path().apply {
                            moveTo(size.width * 0.15f, size.height * 0.35f)
                            cubicTo(size.width * 0.35f, size.height * 0.2f, size.width * 0.65f, size.height * 0.2f, size.width * 0.85f, size.height * 0.35f)
                        }
                        drawPath(arc1, iconColor, style = stroke)
                        val arc2 = Path().apply {
                            moveTo(size.width * 0.3f, size.height * 0.52f)
                            cubicTo(size.width * 0.4f, size.height * 0.42f, size.width * 0.6f, size.height * 0.42f, size.width * 0.7f, size.height * 0.52f)
                        }
                        drawPath(arc2, iconColor, style = stroke)
                        drawCircle(iconColor, radius = 1.8.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.72f))
                        // Slash
                        drawLine(iconColor, Offset(size.width * 0.15f, size.height * 0.85f), Offset(size.width * 0.85f, size.height * 0.15f), strokeWidth = stroke.width)
                    }
                    DialogType.AI_FAILURE -> {
                        // Sparkle star icon
                        val star = Path().apply {
                            moveTo(size.width * 0.5f, size.height * 0.15f)
                            lineTo(size.width * 0.6f, size.height * 0.4f)
                            lineTo(size.width * 0.85f, size.height * 0.5f)
                            lineTo(size.width * 0.6f, size.height * 0.6f)
                            lineTo(size.width * 0.5f, size.height * 0.85f)
                            lineTo(size.width * 0.4f, size.height * 0.6f)
                            lineTo(size.width * 0.15f, size.height * 0.5f)
                            lineTo(size.width * 0.4f, size.height * 0.4f)
                            close()
                        }
                        drawPath(star, iconColor, style = stroke)
                    }
                    DialogType.INVALID_FIELDS -> {
                        // Exclamation / Info circle
                        drawCircle(iconColor, radius = size.width * 0.42f, center = Offset(size.width * 0.5f, size.height * 0.5f), style = stroke)
                        drawLine(iconColor, Offset(size.width * 0.5f, size.height * 0.28f), Offset(size.width * 0.5f, size.height * 0.55f), strokeWidth = stroke.width)
                        drawCircle(iconColor, radius = 1.2.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.7f))
                    }
                    DialogType.ENTRY_SAVED -> {
                        // Checkmark circle
                        drawCircle(iconColor, radius = size.width * 0.42f, center = Offset(size.width * 0.5f, size.height * 0.5f), style = stroke)
                        val check = Path().apply {
                            moveTo(size.width * 0.32f, size.height * 0.5f)
                            lineTo(size.width * 0.45f, size.height * 0.65f)
                            lineTo(size.width * 0.68f, size.height * 0.38f)
                        }
                        drawPath(check, iconColor, style = stroke)
                    }
                    DialogType.MEMORY_SAVED -> {
                        // Ribbon / Bookmark with check
                        drawRoundRect(iconColor, topLeft = Offset(size.width * 0.22f, size.height * 0.18f), size = Size(size.width * 0.56f, size.height * 0.68f), cornerRadius = CornerRadius(4.dp.toPx()), style = stroke)
                        val check = Path().apply {
                            moveTo(size.width * 0.35f, size.height * 0.48f)
                            lineTo(size.width * 0.46f, size.height * 0.58f)
                            lineTo(size.width * 0.62f, size.height * 0.38f)
                        }
                        drawPath(check, iconColor, style = stroke)
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
