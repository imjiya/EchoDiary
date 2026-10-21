package com.simats.echodiary.ui.onboarding

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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SecondOnboardingScreen(
    viewModel: SecondOnboardingViewModel = remember { SecondOnboardingViewModel() },
    onNavigateToThirdOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is SecondOnboardingNavigationEvent.NavigateToThirdOnboarding -> onNavigateToThirdOnboarding()
                is SecondOnboardingNavigationEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "EchoDiary AI",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Step Label
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = "01 / 03 - VOICE JOURNALING",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5165F6),
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF0F4FF),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(vertical = 40.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Microphone Icon Circle
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                color = Color.White,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(36.dp)) {
                            val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val color = Color(0xFF5165F6)

                            // Mic body
                            drawRoundRect(
                                color = color,
                                topLeft = Offset(size.width * 0.35f, size.height * 0.1f),
                                size = Size(size.width * 0.3f, size.height * 0.45f),
                                cornerRadius = CornerRadius(size.width * 0.15f),
                                style = stroke
                            )

                            // Mic stand arc
                            drawArc(
                                color = color,
                                startAngle = 0f,
                                sweepAngle = 180f,
                                useCenter = false,
                                topLeft = Offset(size.width * 0.22f, size.height * 0.35f),
                                size = Size(size.width * 0.56f, size.height * 0.4f),
                                style = stroke
                            )

                            // Mic vertical stem
                            drawLine(
                                color = color,
                                start = Offset(size.width * 0.5f, size.height * 0.75f),
                                end = Offset(size.width * 0.5f, size.height * 0.9f),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )

                            // Mic base line
                            drawLine(
                                color = color,
                                start = Offset(size.width * 0.35f, size.height * 0.9f),
                                end = Offset(size.width * 0.65f, size.height * 0.9f),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Audio Waveform
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val heights = listOf(8, 12, 18, 10, 24, 14, 30, 16, 36, 22, 40, 26, 32, 18, 28, 12, 22, 10, 16, 8)
                        heights.forEach { h ->
                            Box(
                                modifier = Modifier
                                    .width(2.5.dp)
                                    .height(h.dp)
                                    .background(Color(0xFF5165F6), shape = RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Heading
            Text(
                text = "Some days are easier said than written.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = "Capture your day in your own words. Review the transcript and shape it into a diary entry before saving.",
                fontSize = 15.sp,
                color = Color(0xFF64748B),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            // Page Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(6.dp)
                        .background(Color(0xFF5165F6), shape = RoundedCornerShape(3.dp))
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFFCBD5E1), shape = CircleShape)
                )
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFFCBD5E1), shape = CircleShape)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Continue Button
            Button(
                onClick = { viewModel.onContinueClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5165F6))
            ) {
                Text(
                    text = "Continue",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Skip link
            Text(
                text = "Skip",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF5165F6),
                modifier = Modifier
                    .clickable { viewModel.onSkipClicked() }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
