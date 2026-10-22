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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private enum class SettingsIconType {
    ACCOUNT, APPEARANCE, PRIVACY, NOTIFICATIONS, STORAGE, HELP
}

@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = remember { SettingsViewModel() },
    onNavigateBack: () -> Unit,
    onNavigateToAccount: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
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
                    text = "Settings",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Subheader
            Text(
                text = "YOUR SPACE, YOUR PREFERENCES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Group 1 Card: Account & Appearance
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                SettingsMenuItem(
                    title = "Account",
                    subtitle = state.userName,
                    iconType = SettingsIconType.ACCOUNT,
                    onClick = onNavigateToAccount
                )

                Spacer(modifier = Modifier.height(8.dp))

                SettingsMenuItem(
                    title = "Appearance",
                    subtitle = state.appearanceSummary,
                    iconType = SettingsIconType.APPEARANCE,
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Group 2 Card: Privacy, Notifications, Storage
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                SettingsMenuItem(
                    title = "Privacy & data",
                    subtitle = "Review and manage your content",
                    iconType = SettingsIconType.PRIVACY,
                    onClick = { }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SettingsMenuItem(
                    title = "Notifications",
                    subtitle = "Reminder schedules",
                    iconType = SettingsIconType.NOTIFICATIONS,
                    onClick = onNavigateToNotifications
                )

                Spacer(modifier = Modifier.height(8.dp))

                SettingsMenuItem(
                    title = "Storage",
                    subtitle = state.storageSummary,
                    iconType = SettingsIconType.STORAGE,
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Group 3 Card: Help & support
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                SettingsMenuItem(
                    title = "Help & support",
                    subtitle = "FAQs and troubleshooting",
                    iconType = SettingsIconType.HELP,
                    onClick = { }
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Log Out Button
            Button(
                onClick = onNavigateToLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFEEF2FF),
                    contentColor = Color(0xFF5165F6)
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        val stroke = Stroke(width = 1.8.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        val color = Color(0xFF5165F6)
                        // Door / Exit outline
                        val path = Path().apply {
                            moveTo(size.width * 0.5f, size.height * 0.2f)
                            lineTo(size.width * 0.2f, size.height * 0.2f)
                            lineTo(size.width * 0.2f, size.height * 0.8f)
                            lineTo(size.width * 0.5f, size.height * 0.8f)
                        }
                        drawPath(path, color, style = stroke)
                        // Exit arrow
                        val arrowPath = Path().apply {
                            moveTo(size.width * 0.45f, size.height * 0.5f)
                            lineTo(size.width * 0.85f, size.height * 0.5f)
                            moveTo(size.width * 0.68f, size.height * 0.32f)
                            lineTo(size.width * 0.85f, size.height * 0.5f)
                            lineTo(size.width * 0.68f, size.height * 0.68f)
                        }
                        drawPath(arrowPath, color, style = stroke)
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Log out",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5165F6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Footer Text
            Text(
                text = "EchoDiary AI · Design preview",
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
private fun SettingsMenuItem(
    title: String,
    subtitle: String,
    iconType: SettingsIconType,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular Icon Container
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
                    SettingsIconType.ACCOUNT -> {
                        // Person icon
                        drawCircle(color, radius = size.width * 0.22f, center = Offset(size.width * 0.5f, size.height * 0.35f), style = stroke)
                        drawArc(color, 0f, 180f, false, topLeft = Offset(size.width * 0.15f, size.height * 0.55f), size = Size(size.width * 0.7f, size.height * 0.4f), style = stroke)
                    }
                    SettingsIconType.APPEARANCE -> {
                        // Palette icon
                        val path = Path().apply {
                            moveTo(size.width * 0.5f, size.height * 0.15f)
                            cubicTo(size.width * 0.85f, size.height * 0.15f, size.width * 0.9f, size.height * 0.45f, size.width * 0.75f, size.height * 0.65f)
                            cubicTo(size.width * 0.65f, size.height * 0.78f, size.width * 0.55f, size.height * 0.85f, size.width * 0.45f, size.height * 0.85f)
                            cubicTo(size.width * 0.15f, size.height * 0.85f, size.width * 0.15f, size.height * 0.45f, size.width * 0.5f, size.height * 0.15f)
                        }
                        drawPath(path, color, style = stroke)
                        drawCircle(color, radius = 1.5.dp.toPx(), center = Offset(size.width * 0.35f, size.height * 0.4f))
                        drawCircle(color, radius = 1.5.dp.toPx(), center = Offset(size.width * 0.55f, size.height * 0.35f))
                        drawCircle(color, radius = 1.5.dp.toPx(), center = Offset(size.width * 0.7f, size.height * 0.45f))
                    }
                    SettingsIconType.PRIVACY -> {
                        // Shield icon with checkmark
                        val path = Path().apply {
                            moveTo(size.width * 0.5f, size.height * 0.15f)
                            lineTo(size.width * 0.85f, size.height * 0.28f)
                            lineTo(size.width * 0.85f, size.height * 0.55f)
                            cubicTo(size.width * 0.85f, size.height * 0.78f, size.width * 0.5f, size.height * 0.9f, size.width * 0.5f, size.height * 0.9f)
                            cubicTo(size.width * 0.5f, size.height * 0.9f, size.width * 0.15f, size.height * 0.78f, size.width * 0.15f, size.height * 0.55f)
                            lineTo(size.width * 0.15f, size.height * 0.28f)
                            close()
                        }
                        drawPath(path, color, style = stroke)
                        val check = Path().apply {
                            moveTo(size.width * 0.38f, size.height * 0.52f)
                            lineTo(size.width * 0.48f, size.height * 0.62f)
                            lineTo(size.width * 0.65f, size.height * 0.42f)
                        }
                        drawPath(check, color, style = stroke)
                    }
                    SettingsIconType.NOTIFICATIONS -> {
                        // Bell icon
                        val path = Path().apply {
                            moveTo(size.width * 0.5f, size.height * 0.18f)
                            cubicTo(size.width * 0.3f, size.height * 0.18f, size.width * 0.25f, size.height * 0.45f, size.width * 0.25f, size.height * 0.62f)
                            lineTo(size.width * 0.18f, size.height * 0.72f)
                            lineTo(size.width * 0.82f, size.height * 0.72f)
                            lineTo(size.width * 0.75f, size.height * 0.62f)
                            cubicTo(size.width * 0.75f, size.height * 0.45f, size.width * 0.7f, size.height * 0.18f, size.width * 0.5f, size.height * 0.18f)
                        }
                        drawPath(path, color, style = stroke)
                        val clapper = Path().apply {
                            moveTo(size.width * 0.42f, size.height * 0.78f)
                            cubicTo(size.width * 0.42f, size.height * 0.88f, size.width * 0.58f, size.height * 0.88f, size.width * 0.58f, size.height * 0.78f)
                        }
                        drawPath(clapper, color, style = stroke)
                    }
                    SettingsIconType.STORAGE -> {
                        // Storage / Hard drive icon
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(size.width * 0.15f, size.height * 0.25f),
                            size = Size(size.width * 0.7f, size.height * 0.52f),
                            cornerRadius = CornerRadius(4.dp.toPx()),
                            style = stroke
                        )
                        drawLine(color, Offset(size.width * 0.15f, size.height * 0.52f), Offset(size.width * 0.85f, size.height * 0.52f), strokeWidth = stroke.width)
                        drawCircle(color, radius = 1.5.dp.toPx(), center = Offset(size.width * 0.72f, size.height * 0.38f))
                        drawCircle(color, radius = 1.5.dp.toPx(), center = Offset(size.width * 0.72f, size.height * 0.65f))
                    }
                    SettingsIconType.HELP -> {
                        // Question mark in circle
                        drawCircle(color, radius = size.width * 0.4f, center = Offset(size.width * 0.5f, size.height * 0.5f), style = stroke)
                        val question = Path().apply {
                            moveTo(size.width * 0.38f, size.height * 0.4f)
                            cubicTo(size.width * 0.38f, size.height * 0.3f, size.width * 0.62f, size.height * 0.3f, size.width * 0.62f, size.height * 0.43f)
                            cubicTo(size.width * 0.62f, size.height * 0.55f, size.width * 0.5f, size.height * 0.55f, size.width * 0.5f, size.height * 0.63f)
                        }
                        drawPath(question, color, style = stroke)
                        drawCircle(color, radius = 1.2.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.72f))
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
