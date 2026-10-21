package com.simats.echodiary.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OnboardingScreen(
    viewModel: OnboardingViewModel = remember { OnboardingViewModel() },
    onNavigateToSecondOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is OnboardingNavigationEvent.NavigateToSecondOnboarding -> onNavigateToSecondOnboarding()
                is OnboardingNavigationEvent.NavigateToLogin -> onNavigateToLogin()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFBFC))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF0F4FF),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .padding(vertical = 36.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                color = Color(0xFF5165F6),
                                shape = RoundedCornerShape(20.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(38.dp)) {
                            val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
                            val white = Color.White

                            val leftPath = Path().apply {
                                moveTo(size.width * 0.5f, size.height * 0.15f)
                                cubicTo(
                                    size.width * 0.35f, size.height * 0.12f,
                                    size.width * 0.15f, size.height * 0.18f,
                                    size.width * 0.1f, size.height * 0.25f
                                )
                                lineTo(size.width * 0.1f, size.height * 0.85f)
                                cubicTo(
                                    size.width * 0.15f, size.height * 0.78f,
                                    size.width * 0.35f, size.height * 0.75f,
                                    size.width * 0.5f, size.height * 0.82f
                                )
                                close()
                            }
                            drawPath(leftPath, white, style = stroke)

                            val rightPath = Path().apply {
                                moveTo(size.width * 0.5f, size.height * 0.15f)
                                cubicTo(
                                    size.width * 0.65f, size.height * 0.12f,
                                    size.width * 0.85f, size.height * 0.18f,
                                    size.width * 0.9f, size.height * 0.25f
                                )
                                lineTo(size.width * 0.9f, size.height * 0.85f)
                                cubicTo(
                                    size.width * 0.85f, size.height * 0.78f,
                                    size.width * 0.65f, size.height * 0.75f,
                                    size.width * 0.5f, size.height * 0.82f
                                )
                                close()
                            }
                            drawPath(rightPath, white, style = stroke)

                            drawLine(
                                color = white,
                                start = Offset(size.width * 0.5f, size.height * 0.15f),
                                end = Offset(size.width * 0.5f, size.height * 0.82f),
                                strokeWidth = 2.dp.toPx(),
                                cap = StrokeCap.Round
                            )

                            drawLine(white, Offset(size.width * 0.25f, size.height * 0.35f), Offset(size.width * 0.25f, size.height * 0.60f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
                            drawLine(white, Offset(size.width * 0.37f, size.height * 0.30f), Offset(size.width * 0.37f, size.height * 0.65f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
                            drawLine(white, Offset(size.width * 0.63f, size.height * 0.30f), Offset(size.width * 0.63f, size.height * 0.65f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
                            drawLine(white, Offset(size.width * 0.75f, size.height * 0.35f), Offset(size.width * 0.75f, size.height * 0.60f), strokeWidth = 1.5.dp.toPx(), cap = StrokeCap.Round)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val heights = listOf(8, 12, 18, 10, 22, 14, 28, 16, 32, 20, 36, 24, 30, 18, 26, 12, 22, 10, 16, 8)
                        heights.forEach { h ->
                            Box(
                                modifier = Modifier
                                    .width(2.5.dp)
                                    .height(h.dp)
                                    .background(Color(0xFF5165F6), shape = RoundedCornerShape(2.dp))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "EchoDiary AI",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF5165F6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Your everyday,\nbeautifully remembered.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Speak a thought or write a few lines. Make room for your day, then revisit the moments that matter.",
                fontSize = 15.sp,
                color = Color(0xFF64748B),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Your Voice. Your Day. Your Memories.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF5165F6)
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = { viewModel.onGetStartedClicked() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5165F6))
                ) {
                    Text(
                        text = "Get Started",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.onLogInClicked() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp),
                    shape = RoundedCornerShape(27.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF2FF))
                ) {
                    Text(
                        text = "Log In",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5165F6)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "Your diary begins with you.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
