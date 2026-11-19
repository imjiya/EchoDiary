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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.echodiary.ui.theme.EchoTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MonthlyMemoryScreen(
    viewModel: MonthlyMemoryViewModel = remember { MonthlyMemoryViewModel() },
    onNavigateBack: () -> Unit,
    onNavigateToAllActivities: () -> Unit,
    onNavigateToActivityDetails: () -> Unit = {},
    onNavigateToCreatingMemory: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is MonthlyMemoryNavigationEvent.NavigateBack -> onNavigateBack()
                is MonthlyMemoryNavigationEvent.NavigateToAllActivities -> onNavigateToAllActivities()
                is MonthlyMemoryNavigationEvent.NavigateToActivityDetails -> onNavigateToActivityDetails()
                is MonthlyMemoryNavigationEvent.NavigateToCreatingMemory -> onNavigateToCreatingMemory()
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Monthly memory",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface
                    )
                },
                navigationIcon = {
                    val backColor = EchoTheme.onSurface
                    IconButton(onClick = { viewModel.onBackClicked() }) {
                        Canvas(modifier = Modifier.size(24.dp)) {
                            val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val path = Path().apply {
                                moveTo(size.width * 0.7f, size.height * 0.2f)
                                lineTo(size.width * 0.3f, size.height * 0.5f)
                                lineTo(size.width * 0.7f, size.height * 0.8f)
                            }
                            drawPath(path, backColor, style = stroke)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EchoTheme.background)
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
            Spacer(modifier = Modifier.height(12.dp))

            // Month Label
            Text(
                text = "Month",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = EchoTheme.textSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Month Dropdown Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(16.dp))
                    .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(16.dp))
                    .clickable { viewModel.onMonthDropdownClicked() }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.monthTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoTheme.onSurface
                    )
                    Text(
                        text = "⌄",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Sample Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.container, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "ℹ️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = state.infoText,
                        fontSize = 13.sp,
                        color = EchoTheme.onSurface,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Metrics Row (Diary entries & Categories)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = EchoTheme.surface, shape = RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "${state.diaryEntriesCount}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoTheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Diary entries",
                            fontSize = 13.sp,
                            color = EchoTheme.textSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(color = EchoTheme.surface, shape = RoundedCornerShape(20.dp))
                        .padding(20.dp)
                ) {
                    Column {
                        Text(
                            text = "${state.categoriesCount}",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoTheme.primary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Categories",
                            fontSize = 13.sp,
                            color = EchoTheme.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // October, in your words card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(20.dp))
                    .clickable { viewModel.onSummaryCardClicked() }
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = state.summaryTitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.summaryText,
                        fontSize = 14.sp,
                        color = EchoTheme.textSecondary,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category breakdown card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "Category breakdown · ${state.categoriesCount} categories",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (state.categoryBreakdown.isEmpty()) {
                        Text(
                            text = "No category data available yet. Start logging moments to see your category insights.",
                            fontSize = 13.sp,
                            color = EchoTheme.textSecondary
                        )
                    } else {
                        state.categoryBreakdown.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = item.name,
                                    fontSize = 14.sp,
                                    color = EchoTheme.textSecondary,
                                    modifier = Modifier.width(80.dp)
                                )
                                LinearProgressIndicator(
                                    progress = { item.progress },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(8.dp)
                                        .clip(RoundedCornerShape(4.dp)),
                                    color = EchoTheme.primary,
                                    trackColor = EchoTheme.container,
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(
                                    text = "${item.count}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EchoTheme.onSurface
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Dedicated Section: Saved Entries in Monthly Memory
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
                            text = "Saved entries in this monthly memory",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoTheme.onSurface
                        )
                        Text(
                            text = "${state.entries.size} entries",
                            fontSize = 12.sp,
                            color = EchoTheme.textSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (state.entries.isEmpty()) {
                        Text(
                            text = "No diary entries recorded for this month yet.",
                            fontSize = 13.sp,
                            color = EchoTheme.textSecondary
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
                                            color = EchoTheme.onSurface,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = "${entry.category} · ${entry.date}",
                                            fontSize = 12.sp,
                                            color = EchoTheme.textSecondary
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
                                                fontWeight = EchoTheme.textMedium,
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

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Text
            Text(
                text = state.footerText,
                fontSize = 13.sp,
                color = EchoTheme.textSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Generate & Share Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.onGenerateMonthlyMemoryClicked() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.primary)
                ) {
                    Text(
                        text = "✨ Generate",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Button(
                    onClick = { viewModel.onShareClicked(context) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.container)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "↗ ", fontSize = 16.sp, color = EchoTheme.primary)
                        Text(
                            text = "Share as image",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EchoTheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
