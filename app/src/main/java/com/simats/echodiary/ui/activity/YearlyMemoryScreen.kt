package com.simats.echodiary.ui.activity

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
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YearlyMemoryScreen(
    viewModel: YearlyMemoryViewModel = remember { YearlyMemoryViewModel() },
    onNavigateBack: () -> Unit,
    onNavigateToAllActivities: () -> Unit,
    onNavigateToActivityDetails: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is YearlyMemoryNavigationEvent.NavigateBack -> onNavigateBack()
                is YearlyMemoryNavigationEvent.NavigateToAllActivities -> onNavigateToAllActivities()
                is YearlyMemoryNavigationEvent.NavigateToActivityDetails -> onNavigateToActivityDetails()
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
                        color = Color(0xFF1E293B)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onBackClicked() }) {
                        Canvas(modifier = Modifier.size(24.dp)) {
                            val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val path = Path().apply {
                                moveTo(size.width * 0.7f, size.height * 0.2f)
                                lineTo(size.width * 0.3f, size.height * 0.5f)
                                lineTo(size.width * 0.7f, size.height * 0.8f)
                            }
                            drawPath(path, Color(0xFF1E293B), style = stroke)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFFFAFBFC))
            )
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
            Spacer(modifier = Modifier.height(12.dp))

            // Year Label
            Text(
                text = "Year",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Year Dropdown Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(16.dp))
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
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "⌄",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Info Sample Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFF0F4FF), shape = RoundedCornerShape(16.dp))
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
                        color = Color(0xFF334155),
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // One small chapter card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(20.dp))
                    .clickable { viewModel.onChapterCardClicked() }
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = state.chapterTitle,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.chapterText,
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Category Chips Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.categories.forEach { category ->
                    Box(
                        modifier = Modifier
                            .background(color = Color.White, shape = RoundedCornerShape(20.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = category,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Supporting records header
            Text(
                text = "Supporting records · October",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Supporting Records Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.White, shape = RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
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
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = record.date,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF5165F6)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Footer Text
            Text(
                text = state.footerText,
                fontSize = 13.sp,
                color = Color(0xFF64748B),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
