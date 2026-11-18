package com.simats.echodiary.ui.activity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.echodiary.ui.theme.EchoTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllActivitiesScreen(
    viewModel: AllActivitiesViewModel = remember { AllActivitiesViewModel() },
    onNavigateBack: () -> Unit,
    onNavigateToSearchDiary: () -> Unit,
    onNavigateToActivityDetails: (DiaryEntryItem) -> Unit = {},
    onNavigateToFilterActivities: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is AllActivitiesNavigationEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "All activities",
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
                actions = {
                    val filterColor = EchoTheme.onSurface
                    IconButton(onClick = onNavigateToFilterActivities) {
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val color = filterColor
                            drawLine(color, Offset(size.width * 0.15f, size.height * 0.25f), Offset(size.width * 0.85f, size.height * 0.25f), strokeWidth = stroke.width)
                            drawCircle(color, radius = 2.5.dp.toPx(), center = Offset(size.width * 0.35f, size.height * 0.25f))

                            drawLine(color, Offset(size.width * 0.15f, size.height * 0.5f), Offset(size.width * 0.85f, size.height * 0.5f), strokeWidth = stroke.width)
                            drawCircle(color, radius = 2.5.dp.toPx(), center = Offset(size.width * 0.65f, size.height * 0.5f))

                            drawLine(color, Offset(size.width * 0.15f, size.height * 0.75f), Offset(size.width * 0.85f, size.height * 0.75f), strokeWidth = stroke.width)
                            drawCircle(color, radius = 2.5.dp.toPx(), center = Offset(size.width * 0.45f, size.height * 0.75f))
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

            // Search diary label
            Text(
                text = "Search diary",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = EchoTheme.onSurface,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigateToSearchDiary() }
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    placeholder = {
                        Text(text = "Search entries", color = EchoTheme.textSecondary, fontSize = 14.sp)
                    },
                    trailingIcon = {
                        Text(text = "🔍", fontSize = 16.sp, modifier = Modifier.padding(end = 12.dp))
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EchoTheme.primary,
                        unfocusedBorderColor = EchoTheme.cardBorder,
                        focusedContainerColor = EchoTheme.surface,
                        unfocusedContainerColor = EchoTheme.surface,
                        focusedTextColor = EchoTheme.onSurface,
                        unfocusedTextColor = EchoTheme.onSurface
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subtitle info
            Text(
                text = "${state.entries.size} entries · Stored in memory of activities",
                fontSize = 12.sp,
                color = EchoTheme.textSecondary,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            // List of entry cards
            state.entries.forEach { entry ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = EchoTheme.surface, shape = RoundedCornerShape(20.dp))
                        .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(20.dp))
                        .clickable { onNavigateToActivityDetails(entry) }
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = entry.icon, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = entry.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EchoTheme.onSurface
                                )
                                Text(
                                    text = "${entry.category} · ${entry.date}",
                                    fontSize = 12.sp,
                                    color = EchoTheme.textSecondary
                                )
                            }
                            val moodEmoji = com.simats.echodiary.data.local.DiaryActivityStore.getMoodEmoji(entry.mood)
                            val hasExplicitMood = entry.mood.isNotBlank() &&
                                !entry.mood.equals("not specified", ignoreCase = true) &&
                                !entry.mood.equals("unspecified", ignoreCase = true)
                            if (hasExplicitMood) {
                                Box(
                                    modifier = Modifier
                                        .background(EchoTheme.container, shape = RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "$moodEmoji ${entry.mood}",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = EchoTheme.primary
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                            }
                            Text(
                                text = "✏️",
                                fontSize = 14.sp,
                                modifier = Modifier.clickable { onNavigateToActivityDetails(entry) }
                            )
                        }

                        if (entry.description.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = entry.description,
                                fontSize = 14.sp,
                                color = EchoTheme.textSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "All ${state.entries.size} entries in your diary",
                fontSize = 13.sp,
                color = EchoTheme.textMuted,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
