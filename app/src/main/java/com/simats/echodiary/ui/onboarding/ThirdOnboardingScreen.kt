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
fun ThirdOnboardingScreen(
    viewModel: ThirdOnboardingViewModel = remember { ThirdOnboardingViewModel() },
    onNavigateToFourthOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is ThirdOnboardingNavigationEvent.NavigateToFourthOnboarding -> onNavigateToFourthOnboarding()
                is ThirdOnboardingNavigationEvent.NavigateToLogin -> onNavigateToLogin()
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
                    text = "02 / 03 - AI MEMORIES",
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
                    .padding(24.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start
                ) {
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
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

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "YOUR WEEKLY MEMORY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5165F6),
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "A week of progress and connection.",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Oct 5–11, 2026 · 5 diary entries",
                        fontSize = 13.sp,
                        color = Color(0xFF64748B)
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "View memory",
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

            Spacer(modifier = Modifier.height(28.dp))

            // Heading
            Text(
                text = "See the story in your everyday.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = "Bring your saved entries together in a source-based summary. Review and edit the words before keeping a memory.",
                fontSize = 15.sp,
                color = Color(0xFF64748B),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Illustrative preview · Oct 5–11, 2026",
                fontSize = 13.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // Page Indicator Dots (middle dot active)
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .background(Color(0xFFCBD5E1), shape = CircleShape)
                )
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
