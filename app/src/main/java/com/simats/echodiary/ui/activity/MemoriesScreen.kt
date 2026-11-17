package com.simats.echodiary.ui.activity

import com.simats.echodiary.ui.common.EchoBottomBar
import com.simats.echodiary.ui.theme.EchoTheme

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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

@Composable
fun MemoriesScreen(
    viewModel: MemoriesViewModel = remember { MemoriesViewModel() },
    onNavigateToHome: () -> Unit,
    onNavigateToCalendar: () -> Unit,
    onNavigateToAllActivities: () -> Unit,
    onNavigateToYourWeeklySummary: () -> Unit,
    onNavigateToMonthlyMemory: () -> Unit,
    onNavigateToYearlyMemory: () -> Unit,
    onNavigateToPhotoMemories: () -> Unit,
    onNavigateToProfile: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            EchoBottomBar(
                selectedIndex = state.selectedTab,
                onNavigateToHome = {
                    viewModel.onBottomTabSelected(0)
                    onNavigateToHome()
                },
                onNavigateToCalendar = {
                    viewModel.onBottomTabSelected(1)
                    onNavigateToCalendar()
                },
                onNavigateToMemories = { viewModel.onBottomTabSelected(2) },
                onNavigateToProfile = {
                    viewModel.onBottomTabSelected(3)
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
                text = "Memories",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle
            Text(
                text = "A shelf for your story.",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "1 saved memory, ready to revisit.",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Filter Tabs (Weekly, Monthly, Yearly)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("Weekly", "Monthly", "Yearly").forEach { tab ->
                    val isSelected = state.activeFilterTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(
                                color = if (isSelected) EchoTheme.container else EchoTheme.surface,
                                shape = RoundedCornerShape(20.dp)
                            )
                            .clickable {
                                viewModel.onFilterTabSelected(tab)
                            }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab,
                            fontSize = 14.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) EchoTheme.primary else Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Saved Memory Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = EchoTheme.container,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = state.memoryCardHeader,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.primary,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = state.memoryTitle,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        lineHeight = 26.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = state.memorySubtitle,
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )

                    if (state.memoryNarrative.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.memoryNarrative,
                            fontSize = 14.sp,
                            color = Color(0xFF334155),
                            lineHeight = 20.sp
                        )
                    }

                    if (state.highlights.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            state.highlights.take(3).forEach { highlight ->
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = Color.White.copy(alpha = 0.85f),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = highlight,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = EchoTheme.primary
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            when (state.activeFilterTab) {
                                "Monthly" -> onNavigateToMonthlyMemory()
                                "Yearly" -> onNavigateToYearlyMemory()
                                else -> onNavigateToYourWeeklySummary()
                            }
                        }
                    ) {
                        Text(
                            text = "View full ${state.activeFilterTab.lowercase()} summary",
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
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Dedicated Section: Saved Entries in this Memory
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Saved entries in this ${state.activeFilterTab.lowercase()} memory",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "${state.entries.size} entries",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (state.entries.isEmpty()) {
                        Text(
                            text = "No diary entries saved for this ${state.activeFilterTab.lowercase()} memory yet.",
                            fontSize = 13.sp,
                            color = Color(0xFF94A3B8)
                        )
                    } else {
                        state.entries.forEach { entry ->
                            val moodEmoji = com.simats.echodiary.data.local.DiaryActivityStore.getMoodEmoji(entry.mood)
                            val hasExplicitMood = entry.mood.isNotBlank() &&
                                !entry.mood.equals("not specified", ignoreCase = true) &&
                                !entry.mood.equals("unspecified", ignoreCase = true)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable {
                                        com.simats.echodiary.data.local.DiaryActivityStore.selectActivity(entry)
                                        onNavigateToAllActivities()
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(EchoTheme.container, shape = RoundedCornerShape(12.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = entry.icon.ifBlank { "📝" }, fontSize = 18.sp)
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = entry.title,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = Color(0xFF1E293B),
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${entry.category} · ${entry.date}",
                                            fontSize = 12.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Mood status pill
                                Box(
                                    modifier = Modifier
                                        .background(
                                            color = if (hasExplicitMood) EchoTheme.container else Color(0xFFF1F5F9),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = moodEmoji, fontSize = 13.sp)
                                        if (hasExplicitMood) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = entry.mood,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = EchoTheme.primary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Explore your diary section
            Text(
                text = "Explore your diary",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Explore Card 1: Monthly Memory
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(16.dp))
                    .clickable { onNavigateToMonthlyMemory() }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(EchoTheme.container, shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📅", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Monthly Memory",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = state.monthlyCardSubtitle,
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    Text(text = "›", fontSize = 18.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Explore Card 2: Yearly Memory
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(16.dp))
                    .clickable { onNavigateToYearlyMemory() }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(EchoTheme.container, shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "📅", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Yearly Memory",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = state.yearlyCardSubtitle,
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    Text(text = "›", fontSize = 18.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Explore Card 3: Photos
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(16.dp))
                    .clickable { onNavigateToPhotoMemories() }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(EchoTheme.container, shape = RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🖼️", fontSize = 16.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Photos",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = state.photosSubtitle,
                                fontSize = 13.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                    Text(text = "›", fontSize = 18.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
