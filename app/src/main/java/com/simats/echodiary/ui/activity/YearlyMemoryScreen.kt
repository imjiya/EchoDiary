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
fun YearlyMemoryScreen(
    viewModel: YearlyMemoryViewModel = remember { YearlyMemoryViewModel() },
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
                is YearlyMemoryNavigationEvent.NavigateBack -> onNavigateBack()
                is YearlyMemoryNavigationEvent.NavigateToAllActivities -> onNavigateToAllActivities()
                is YearlyMemoryNavigationEvent.NavigateToActivityDetails -> onNavigateToActivityDetails()
                is YearlyMemoryNavigationEvent.NavigateToCreatingMemory -> onNavigateToCreatingMemory()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Yearly memory",
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

            // Year Label
            Text(
                text = "Year",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = EchoTheme.textSecondary
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Year Dropdown Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(16.dp))
                    .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(16.dp))
                    .clickable { viewModel.onYearDropdownClicked() }
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = state.yearTitle,
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

            // One small chapter card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(20.dp))
                    .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(20.dp))
                    .clickable { viewModel.onChapterCardClicked() }
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = state.chapterTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.chapterText,
                        fontSize = 14.sp,
                        color = EchoTheme.textSecondary,
                        lineHeight = 20.sp
                    )
                }
            }

            if (state.categories.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))

                // Category Chips Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.categories.forEach { category ->
                        Box(
                            modifier = Modifier
                                .background(color = EchoTheme.surface, shape = RoundedCornerShape(20.dp))
                                .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = category,
                                fontSize = 13.sp,
                                fontWeight = EchoTheme.textMedium,
                                color = EchoTheme.textSecondary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Supporting records & saved entries header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Saved entries in yearly memory",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = EchoTheme.onSurface
                )
                Text(
                    text = "${if (state.entries.isNotEmpty()) state.entries.size else state.supportingRecords.size} entries",
                    fontSize = 12.sp,
                    color = EchoTheme.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Saved Entries in Yearly Memory Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(20.dp))
                    .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    if (state.entries.isNotEmpty()) {
                        state.entries.forEach { entry ->
                            val moodEmoji = com.simats.echodiary.data.local.DiaryActivityStore.getMoodEmoji(entry.mood)
                            val hasExplicitMood = entry.mood.isNotBlank() &&
                                !entry.mood.equals("not specified", ignoreCase = true) &&
                                !entry.mood.equals("unspecified", ignoreCase = true)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .clickable { viewModel.onEntryClicked(entry) },
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

                                if (hasExplicitMood) {
                                    Box(
                                        modifier = Modifier
                                            .background(
                                                color = EchoTheme.container,
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "$moodEmoji ${entry.mood}",
                                            fontSize = 12.sp,
                                            fontWeight = EchoTheme.textMedium,
                                            color = EchoTheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    } else if (state.supportingRecords.isNotEmpty()) {
                        state.supportingRecords.forEach { record ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onRecordClicked() }
                                    .padding(vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = record.title,
                                    fontSize = 14.sp,
                                    color = EchoTheme.onSurface
                                )
                                Text(
                                    text = record.date,
                                    fontSize = 14.sp,
                                    fontWeight = EchoTheme.textMedium,
                                    color = EchoTheme.primary
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No diary entries recorded for this year yet.",
                            fontSize = 13.sp,
                            color = EchoTheme.textSecondary
                        )
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
                    onClick = { viewModel.onGenerateYearlyMemoryClicked() },
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
