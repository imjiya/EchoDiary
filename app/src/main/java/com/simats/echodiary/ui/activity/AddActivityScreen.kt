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
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddActivityScreen(
    viewModel: AddActivityViewModel = remember { AddActivityViewModel() },
    onNavigateBack: () -> Unit,
    onNavigateToVoiceRecording: () -> Unit,
    onNavigateToWriteDiaryEntry: () -> Unit,
    onNavigateToFilterActivities: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is AddActivityNavigationEvent.NavigateBack -> onNavigateBack()
                is AddActivityNavigationEvent.NavigateToVoiceRecording -> onNavigateToVoiceRecording()
                is AddActivityNavigationEvent.NavigateToWriteDiaryEntry -> onNavigateToWriteDiaryEntry()
                is AddActivityNavigationEvent.NavigateToFilterActivities -> onNavigateToFilterActivities()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Add activity",
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

            // Header
            Text(
                text = "How was your day?",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Start with your voice or a few written words.",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Card 1: Record by Voice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF0F4FF),
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable { viewModel.onRecordByVoiceClicked() }
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White, shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val color = Color(0xFF5165F6)
                            drawRoundRect(
                                color = color,
                                topLeft = Offset(size.width * 0.35f, size.height * 0.1f),
                                size = Size(size.width * 0.3f, size.height * 0.45f),
                                cornerRadius = CornerRadius(size.width * 0.15f),
                                style = stroke
                            )
                            drawArc(
                                color = color,
                                startAngle = 0f,
                                sweepAngle = 180f,
                                useCenter = false,
                                topLeft = Offset(size.width * 0.22f, size.height * 0.35f),
                                size = Size(size.width * 0.56f, size.height * 0.4f),
                                style = stroke
                            )
                            drawLine(color, Offset(size.width * 0.5f, size.height * 0.75f), Offset(size.width * 0.5f, size.height * 0.9f), strokeWidth = stroke.width)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Record by Voice",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Say it naturally. Review your transcript before saving.",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Start a voice entry",
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

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: Write Manually
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .clickable { viewModel.onWriteManuallyClicked() }
                    .padding(20.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
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
                                moveTo(size.width * 0.2f, size.height * 0.8f)
                                lineTo(size.width * 0.35f, size.height * 0.85f)
                                lineTo(size.width * 0.85f, size.height * 0.35f)
                                lineTo(size.width * 0.65f, size.height * 0.15f)
                                lineTo(size.width * 0.15f, size.height * 0.65f)
                                close()
                            }
                            drawPath(path, color, style = stroke)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Write Manually",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "A sentence or a page. Write at your own pace.",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Open your diary",
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

            // Start with a category
            Text(
                text = "Start with a category",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(color = Color.White, shape = RoundedCornerShape(20.dp))
                        .clickable { viewModel.onCategoryButtonClicked() }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🎓", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "College", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
                    }
                }

                Box(
                    modifier = Modifier
                        .background(color = Color.White, shape = RoundedCornerShape(20.dp))
                        .clickable { viewModel.onCategoryButtonClicked() }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💼", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Work", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
                    }
                }

                Box(
                    modifier = Modifier
                        .background(color = Color.White, shape = RoundedCornerShape(20.dp))
                        .clickable { viewModel.onCategoryButtonClicked() }
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "➕", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Other", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Color(0xFF1E293B))
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "No perfect words needed. Just yours.",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
