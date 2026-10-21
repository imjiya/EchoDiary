package com.simats.echodiary.ui.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel = remember { LoginViewModel() },
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is LoginNavigationEvent.NavigateBack -> onNavigateBack()
                is LoginNavigationEvent.NavigateToHome -> onNavigateToHome()
                else -> {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Log in",
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // App Icon
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = Color(0xFF5165F6),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(34.dp)) {
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

            Spacer(modifier = Modifier.height(24.dp))

            // Heading
            Text(
                text = "Welcome back, Alex.",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Your diary is right where you left it.",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email Field
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Email",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    trailingIcon = {
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val color = Color(0xFF94A3B8)
                            drawRect(color, topLeft = Offset(0f, size.height * 0.25f), size = Size(size.width, size.height * 0.6f), style = stroke)
                            val path = Path().apply {
                                moveTo(0f, size.height * 0.25f)
                                lineTo(size.width * 0.5f, size.height * 0.6f)
                                lineTo(size.width, size.height * 0.25f)
                            }
                            drawPath(path, color, style = stroke)
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5165F6),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Password Field
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Password",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = state.password,
                    onValueChange = { viewModel.onPasswordChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.onTogglePasswordVisibility() }) {
                            Canvas(modifier = Modifier.size(20.dp)) {
                                val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                                val color = Color(0xFF94A3B8)
                                drawArc(
                                    color = color,
                                    startAngle = 0f,
                                    sweepAngle = 180f,
                                    useCenter = false,
                                    topLeft = Offset(0f, size.height * 0.25f),
                                    size = Size(size.width, size.height * 0.5f),
                                    style = stroke
                                )
                                drawArc(
                                    color = color,
                                    startAngle = 180f,
                                    sweepAngle = 180f,
                                    useCenter = false,
                                    topLeft = Offset(0f, size.height * 0.25f),
                                    size = Size(size.width, size.height * 0.5f),
                                    style = stroke
                                )
                                drawCircle(color, radius = size.width * 0.15f, center = center)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5165F6),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Forgot Password
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Forgot Password",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5165F6),
                    modifier = Modifier
                        .clickable { viewModel.onForgotPasswordClicked() }
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Buttons
            Button(
                onClick = { viewModel.onLoginClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5165F6))
            ) {
                Text(
                    text = "Login",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = { viewModel.onCreateAccountClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF2FF))
            ) {
                Text(
                    text = "Create Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5165F6)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
