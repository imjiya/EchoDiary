package com.simats.echodiary.ui.register

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
fun CreateAccountScreen(
    viewModel: CreateAccountViewModel = remember { CreateAccountViewModel() },
    onNavigateBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is CreateAccountNavigationEvent.NavigateBack -> onNavigateBack()
                is CreateAccountNavigationEvent.NavigateToLogin -> onNavigateToLogin()
                is CreateAccountNavigationEvent.NavigateToHome -> onNavigateToHome()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create account",
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
            Spacer(modifier = Modifier.height(12.dp))

            // Header
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Your story starts here.",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "A few details to make this space yours.",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Full Name Field
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Full name",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = state.fullName,
                    onValueChange = { viewModel.onFullNameChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF5165F6),
                        unfocusedBorderColor = Color(0xFFE2E8F0),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

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

            Spacer(modifier = Modifier.height(16.dp))

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
                                drawArc(color, 0f, 180f, false, Offset(0f, size.height * 0.25f), Size(size.width, size.height * 0.5f), style = stroke)
                                drawArc(color, 180f, 180f, false, Offset(0f, size.height * 0.25f), Size(size.width, size.height * 0.5f), style = stroke)
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
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Use at least 8 characters.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Confirm Password Field
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Confirm password",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                val hasError = state.errorMessage != null
                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    singleLine = true,
                    isError = hasError,
                    visualTransformation = if (state.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.onToggleConfirmPasswordVisibility() }) {
                            Canvas(modifier = Modifier.size(20.dp)) {
                                val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
                                val color = Color(0xFF94A3B8)
                                drawArc(color, 0f, 180f, false, Offset(0f, size.height * 0.25f), Size(size.width, size.height * 0.5f), style = stroke)
                                drawArc(color, 180f, 180f, false, Offset(0f, size.height * 0.25f), Size(size.width, size.height * 0.5f), style = stroke)
                                drawCircle(color, radius = size.width * 0.15f, center = center)
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = if (hasError) Color(0xFFEF4444) else Color(0xFF5165F6),
                        unfocusedBorderColor = if (hasError) Color(0xFFEF4444) else Color(0xFFE2E8F0),
                        errorBorderColor = Color(0xFFEF4444),
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    )
                )
                if (hasError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.errorMessage!!,
                        fontSize = 12.sp,
                        color = Color(0xFFEF4444)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "By creating an account, you agree to the Terms and Privacy Policy.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Create Account Button
            Button(
                onClick = { viewModel.onCreateAccountClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5165F6))
            ) {
                Text(
                    text = "Create Account",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Login Link
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    modifier = Modifier
                        .clickable { viewModel.onLoginLinkClicked() }
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Already have an account? ",
                        fontSize = 14.sp,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "Login",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF5165F6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
