package com.simats.echodiary.ui.calendar

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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.echodiary.ui.common.EchoBottomBar
import com.simats.echodiary.ui.theme.EchoTheme

@Composable
fun CalendarScreen(
    viewModel: CalendarViewModel = remember { CalendarViewModel() },
    onNavigateToHome: () -> Unit,
    onNavigateToSelectedDate: () -> Unit,
    onNavigateToMemories: () -> Unit,
    onNavigateToProfile: () -> Unit = {},
    onNavigateToActivityDetails: () -> Unit = {},
    onNavigateToWriteDiary: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            EchoBottomBar(
                selectedIndex = state.selectedTab,
                onNavigateToHome = {
                    viewModel.onTabSelected(0)
                    onNavigateToHome()
                },
                onNavigateToCalendar = { viewModel.onTabSelected(1) },
                onNavigateToMemories = {
                    viewModel.onTabSelected(2)
                    onNavigateToMemories()
                },
                onNavigateToProfile = {
                    viewModel.onTabSelected(3)
                    onNavigateToProfile()
                }
            )
        },
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

            // Title
            Text(
                text = "Calendar",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Calendar Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Month Navigation Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "〈", fontSize = 18.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                        Text(
                            text = "October 2026",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(text = "〉", fontSize = 18.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Weekdays
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        listOf("M", "T", "W", "T", "F", "S", "S").forEach { day ->
                            Text(
                                text = day,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Days Grid
                    val weeks = listOf(
                        listOf(null, null, null, 1, 2, 3, 4),
                        listOf(5, 6, 7, 8, 9, 10, 11),
                        listOf(12, 13, 14, 15, 16, 17, 18),
                        listOf(19, 20, 21, 22, 23, 24, 25),
                        listOf(26, 27, 28, 29, 30, 31, null)
                    )

                    weeks.forEach { week ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            week.forEach { dayNum ->
                                if (dayNum != null) {
                                    val isSelected = state.selectedDay == dayNum
                                    val isToday = dayNum == 11
                                    val hasEntry = dayNum in listOf(5, 6, 7, 8, 10, 11)

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                color = if (isSelected) EchoTheme.primary else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .clickable {
                                                viewModel.selectDay(dayNum)
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Text(
                                                text = "$dayNum",
                                                fontSize = 14.sp,
                                                fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                                                color = when {
                                                    isSelected -> Color.White
                                                    isToday -> EchoTheme.primary
                                                    else -> Color(0xFF1E293B)
                                                }
                                            )
                                            if (hasEntry && !isSelected) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .size(4.dp)
                                                        .background(EchoTheme.primary, shape = CircleShape)
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Spacer(modifier = Modifier.size(36.dp))
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Legend Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "● Has entries  ·  Select date above",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Selected: ${state.selectedActivityDate}",
                    fontSize = 12.sp,
                    color = EchoTheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Selected Date Activities Header
            Text(
                text = state.selectedDateText,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Selected Activity Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(20.dp))
                    .clickable { onNavigateToActivityDetails() }
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "📅", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = state.selectedActivityTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "${state.selectedActivityCategory} · ${state.selectedActivityDate}",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = state.selectedActivityDescription,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action: Write Diary for this Date
            Button(
                onClick = { onNavigateToWriteDiary(state.selectedActivityDate) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.container)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✏️ ", fontSize = 15.sp)
                    Text(
                        text = "Write Diary for ${state.selectedActivityDate}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoTheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // View selected date activities link
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clickable { onNavigateToSelectedDate() }
                    .padding(vertical = 4.dp)
            ) {
                Text(
                    text = "View activities for ${state.selectedActivityDate}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EchoTheme.primary
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "→",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EchoTheme.primary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
