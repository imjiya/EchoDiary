package com.simats.echodiary.ui.home

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

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = remember { HomeViewModel() },
    onNavigateToAddActivity: () -> Unit,
    onNavigateToAllActivities: () -> Unit
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
                        onClick = { viewModel.onTabSelected(index) },
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

            // Top Header: Date, Greeting, Avatar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = state.dateText,
                        fontSize = 13.sp,
                        color = Color(0xFF94A3B8)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hello, ${state.userName}.",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(color = Color(0xFFEEF2FF), shape = CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "AM",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5165F6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = state.subtitleText,
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onNavigateToAddActivity,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5165F6))
                ) {
                    Text(
                        text = "Add Voice Entry",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = onNavigateToAddActivity,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF2FF))
                ) {
                    Text(
                        text = "Write a Diary",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5165F6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Weekly Memory Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF0F4FF),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Canvas(modifier = Modifier.size(16.dp)) {
                            val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val color = Color(0xFF5165F6)
                            val path = Path().apply {
                                moveTo(size.width * 0.5f, 0f)
                                quadraticTo(size.width * 0.5f, size.height * 0.5f, size.width, size.height * 0.5f)
                                quadraticTo(size.width * 0.5f, size.height * 0.5f, size.width * 0.5f, size.height)
                                quadraticTo(size.width * 0.5f, size.height * 0.5f, 0f, size.height * 0.5f)
                                quadraticTo(size.width * 0.5f, size.height * 0.5f, size.width * 0.5f, 0f)
                                close()
                            }
                            drawPath(path, color, style = stroke)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "WEEKLY MEMORY · OCT 5–11",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5165F6),
                            letterSpacing = 0.5.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "A week of progress and connection.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "5 entries · 5 categories · 1 saved memory",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { onNavigateToAllActivities() }
                    ) {
                        Text(
                            text = "Revisit your week",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF5165F6)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "→",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF5165F6)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Recent Activities Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent activities",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "See All",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5165F6),
                    modifier = Modifier.clickable { onNavigateToAllActivities() }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Activity Item 1
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFF0F4FF), shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val color = Color(0xFF5165F6)
                            val path = Path().apply {
                                moveTo(size.width * 0.1f, size.height * 0.5f)
                                lineTo(size.width * 0.5f, size.height * 0.15f)
                                lineTo(size.width * 0.9f, size.height * 0.5f)
                                moveTo(size.width * 0.25f, size.height * 0.45f)
                                lineTo(size.width * 0.25f, size.height * 0.85f)
                                lineTo(size.width * 0.75f, size.height * 0.85f)
                                lineTo(size.width * 0.75f, size.height * 0.45f)
                            }
                            drawPath(path, color, style = stroke)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Weekend with family",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Family · Oct 10, 2026",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Activity Item 2
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFF0F4FF), shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val color = Color(0xFF5165F6)
                            val path = Path().apply {
                                moveTo(size.width * 0.1f, size.height * 0.4f)
                                lineTo(size.width * 0.5f, size.height * 0.2f)
                                lineTo(size.width * 0.9f, size.height * 0.4f)
                                lineTo(size.width * 0.5f, size.height * 0.6f)
                                close()
                            }
                            drawPath(path, color, style = stroke)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Lectures and project",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "College · Oct 5, 2026",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Photos & Insights Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Canvas(modifier = Modifier.size(18.dp)) {
                            val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                            val color = Color(0xFF5165F6)
                            drawRect(color, topLeft = Offset(0f, size.height * 0.2f), size = Size(size.width, size.height * 0.7f), style = stroke)
                            drawCircle(color, radius = size.width * 0.2f, center = center)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Photos",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF5165F6)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = Color.White, shape = RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Canvas(modifier = Modifier.size(18.dp)) {
                            val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val color = Color(0xFF5165F6)
                            drawLine(color, Offset(2f, size.height * 0.8f), Offset(size.width * 0.3f, size.height * 0.4f), strokeWidth = stroke.width)
                            drawLine(color, Offset(size.width * 0.3f, size.height * 0.4f), Offset(size.width * 0.6f, size.height * 0.6f), strokeWidth = stroke.width)
                            drawLine(color, Offset(size.width * 0.6f, size.height * 0.6f), Offset(size.width - 2f, size.height * 0.2f), strokeWidth = stroke.width)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Insights",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF5165F6)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
