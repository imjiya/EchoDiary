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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
fun FourthOnboardingScreen(
    viewModel: FourthOnboardingViewModel = remember { FourthOnboardingViewModel() },
    onNavigateToCreateAccount: () -> Unit
) {
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is FourthOnboardingNavigationEvent.NavigateToCreateAccount -> onNavigateToCreateAccount()
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
                    text = "03 / 03 - YOUR DIARY, YOUR CHOICE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF5165F6),
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Hero Card with Shield Icon
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
                Canvas(modifier = Modifier.size(64.dp)) {
                    val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    val color = Color(0xFF5165F6)

                    val shieldPath = Path().apply {
                        moveTo(size.width * 0.5f, size.height * 0.1f)
                        lineTo(size.width * 0.85f, size.height * 0.25f)
                        lineTo(size.width * 0.85f, size.height * 0.55f)
                        quadraticTo(size.width * 0.85f, size.height * 0.85f, size.width * 0.5f, size.height * 0.95f)
                        quadraticTo(size.width * 0.15f, size.height * 0.85f, size.width * 0.15f, size.height * 0.55f)
                        lineTo(size.width * 0.15f, size.height * 0.25f)
                        close()
                    }
                    drawPath(shieldPath, color, style = stroke)

                    val checkPath = Path().apply {
                        moveTo(size.width * 0.35f, size.height * 0.52f)
                        lineTo(size.width * 0.45f, size.height * 0.62f)
                        lineTo(size.width * 0.68f, size.height * 0.38f)
                    }
                    drawPath(checkPath, color, style = stroke)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Heading
            Text(
                text = "A personal space. With you in control.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                lineHeight = 36.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Description
            Text(
                text = "Review what you save and choose what stays. Explore privacy and data options whenever you need to.",
                fontSize = 15.sp,
                color = Color(0xFF64748B),
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Feature Card 1
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
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
                            drawRect(color, topLeft = Offset(size.width * 0.2f, size.height * 0.1f), size = Size(size.width * 0.6f, size.height * 0.8f), style = stroke)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Review before saving",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Your words, with your final say.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Text(
                        text = "›",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Feature Card 2
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFF0F4FF), shape = RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                            val color = Color(0xFF5165F6)
                            drawLine(color, Offset(2f, size.height * 0.3f), Offset(size.width - 2f, size.height * 0.3f), strokeWidth = stroke.width)
                            drawLine(color, Offset(6f, size.height * 0.5f), Offset(size.width - 6f, size.height * 0.5f), strokeWidth = stroke.width)
                            drawLine(color, Offset(10f, size.height * 0.7f), Offset(size.width - 10f, size.height * 0.7f), strokeWidth = stroke.width)
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Manage your diary",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        Text(
                            text = "Explore export and deletion options.",
                            fontSize = 13.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                    Text(
                        text = "›",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
            Spacer(modifier = Modifier.height(24.dp))

            // Page Indicator Dots (third dot active)
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
                        .size(6.dp)
                        .background(Color(0xFFCBD5E1), shape = CircleShape)
                )
                Box(
                    modifier = Modifier
                        .width(20.dp)
                        .height(6.dp)
                        .background(Color(0xFF5165F6), shape = RoundedCornerShape(3.dp))
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
