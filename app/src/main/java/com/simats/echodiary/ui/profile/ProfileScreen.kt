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
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
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
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = remember { ProfileViewModel() },
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToMemories: () -> Unit,
    onNavigateToEditProfile: () -> Unit = {},
    onNavigateToInsights: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                val tabs = listOf("Home", "Calendar", "Memories", "Profile")
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        selected = state.selectedTab == index,
                        onClick = {
                            viewModel.onTabSelected(index)
                            when (index) {
                                0 -> onNavigateToHome()
                                1 -> onNavigateToCalendar()
                                2 -> onNavigateToMemories()
                            }
                        },
                        icon = {
                            Canvas(modifier = Modifier.size(24.dp)) {
                                val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                                val color = if (state.selectedTab == index) Color(0xFF5165F6) else Color(0xFF94A3B8)
                                when (index) {
                                    0 -> { // Home
                                        val path = Path().apply {
                                            moveTo(size.width * 0.1f, size.height * 0.5f)
                                            lineTo(size.width * 0.5f, size.height * 0.15f)
                                            lineTo(size.width * 0.9f, size.height * 0.5f)
                                            moveTo(size.width * 0.2f, size.height * 0.45f)
                                            lineTo(size.width * 0.2f, size.height * 0.85f)
                                            lineTo(size.width * 0.8f, size.height * 0.85f)
                                            lineTo(size.width * 0.8f, size.height * 0.45f)
                                        }
                                        drawPath(path, color, style = stroke)
                                    }
                                    1 -> { // Calendar
                                        drawRect(color, topLeft = Offset(2f, size.height * 0.2f), size = Size(size.width - 4f, size.height * 0.7f), style = stroke)
                                        drawLine(color, Offset(2f, size.height * 0.4f), Offset(size.width - 2f, size.height * 0.4f), strokeWidth = stroke.width)
                                    }
                                    2 -> { // Memories
                                        drawRect(color, topLeft = Offset(size.width * 0.2f, size.height * 0.2f), size = Size(size.width * 0.6f, size.height * 0.65f), style = stroke)
                                        drawLine(color, Offset(size.width * 0.5f, size.height * 0.2f), Offset(size.width * 0.5f, size.height * 0.85f), strokeWidth = stroke.width)
                                    }
                                    3 -> { // Profile
                                        drawCircle(color, radius = size.width * 0.2f, center = Offset(size.width * 0.5f, size.height * 0.35f), style = stroke)
                                        drawArc(color, 0f, 180f, false, topLeft = Offset(size.width * 0.15f, size.height * 0.55f), size = Size(size.width * 0.7f, size.height * 0.4f), style = stroke)
                                    }
                                }
                            }
                        },
                        label = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (state.selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (state.selectedTab == index) Color(0xFF5165F6) else Color(0xFF94A3B8)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color(0xFFEEF2FF)
                        )
                    )
                }
            }
        },
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

            // Screen Title
            Text(
                text = "Profile",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Profile Header Section (Centered)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .background(color = Color(0xFFEEF2FF), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = state.userInitials,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5165F6)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = state.userName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = state.userEmail,
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Stats Cards Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Diary Entries Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = state.diaryEntriesCount.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5165F6)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Diary entries",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                // Saved Memory Card
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White, RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = state.savedMemoriesCount.toString(),
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5165F6)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Saved memory",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Menu Options Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(vertical = 8.dp, horizontal = 16.dp)
            ) {
                // Edit Profile Option
                ProfileMenuItem(
                    title = "Edit Profile",
                    subtitle = "Your name, email and avatar",
                    iconType = ProfileIconType.EDIT_PROFILE,
                    onClick = onNavigateToEditProfile
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Settings Option
                ProfileMenuItem(
                    title = "Settings",
                    subtitle = "Make this space yours",
                    iconType = ProfileIconType.SETTINGS,
                    onClick = { }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Insights Option
                ProfileMenuItem(
                    title = "Insights",
                    subtitle = "Activity and recorded moods",
                    iconType = ProfileIconType.INSIGHTS,
                    onClick = onNavigateToInsights
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Footer Text
            Text(
                text = "A little space for your everyday.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private enum class ProfileIconType {
    EDIT_PROFILE, SETTINGS, INSIGHTS
}

@Composable
private fun ProfileMenuItem(
    title: String,
    subtitle: String,
    iconType: ProfileIconType,
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
                    ProfileIconType.EDIT_PROFILE -> {
                        // Pencil icon
                        val path = Path().apply {
                            moveTo(size.width * 0.2f, size.height * 0.8f)
                            lineTo(size.width * 0.4f, size.height * 0.8f)
                            lineTo(size.width * 0.85f, size.height * 0.35f)
                            lineTo(size.width * 0.65f, size.height * 0.15f)
                            lineTo(size.width * 0.2f, size.height * 0.6f)
                            close()
                        }
                        drawPath(path, color, style = stroke)
                    }
                    ProfileIconType.SETTINGS -> {
                        // Settings / Gear icon
                        drawCircle(color, radius = size.width * 0.25f, center = Offset(size.width * 0.5f, size.height * 0.5f), style = stroke)
                        val numTeeth = 6
                        for (i in 0 until numTeeth) {
                            val angle = Math.toRadians((i * 60).toDouble())
                            val startX = (size.width * 0.5f + cos(angle) * size.width * 0.25f).toFloat()
                            val startY = (size.height * 0.5f + sin(angle) * size.height * 0.25f).toFloat()
                            val endX = (size.width * 0.5f + cos(angle) * size.width * 0.42f).toFloat()
                            val endY = (size.height * 0.5f + sin(angle) * size.height * 0.42f).toFloat()
                            drawLine(color, Offset(startX, startY), Offset(endX, endY), strokeWidth = stroke.width)
                        }
                    }
                    ProfileIconType.INSIGHTS -> {
                        // Insights / Bar chart icon
                        val barWidth = size.width * 0.18f
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(size.width * 0.12f, size.height * 0.5f),
                            size = Size(barWidth, size.height * 0.4f),
                            cornerRadius = CornerRadius(2.dp.toPx()),
                            style = stroke
                        )
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(size.width * 0.41f, size.height * 0.25f),
                            size = Size(barWidth, size.height * 0.65f),
                            cornerRadius = CornerRadius(2.dp.toPx()),
                            style = stroke
                        )
                        drawRoundRect(
                            color = color,
                            topLeft = Offset(size.width * 0.7f, size.height * 0.4f),
                            size = Size(barWidth, size.height * 0.5f),
                            cornerRadius = CornerRadius(2.dp.toPx()),
                            style = stroke
                        )
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
}
