package com.simats.echodiary.ui.activity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EditYourMemoryScreen(
    viewModel: EditYourMemoryViewModel = remember { EditYourMemoryViewModel() },
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
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
            Spacer(modifier = Modifier.height(16.dp))

            // Header Top Bar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.size(36.dp)
                ) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                        val color = Color(0xFF1E293B)
                        val path = Path().apply {
                            moveTo(size.width * 0.8f, size.height * 0.5f)
                            lineTo(size.width * 0.2f, size.height * 0.5f)
                            moveTo(size.width * 0.45f, size.height * 0.2f)
                            lineTo(size.width * 0.2f, size.height * 0.5f)
                            lineTo(size.width * 0.45f, size.height * 0.8f)
                        }
                        drawPath(path, color, style = stroke)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Edit your memory",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subheader
            Text(
                text = state.dateRangeText,
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Field 1: Memory Title
            Text(
                text = "Memory title",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = state.memoryTitle,
                onValueChange = { viewModel.onTitleChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5165F6),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Field 2: Weekly Narrative
            Text(
                text = "Weekly narrative",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = state.weeklyNarrative,
                onValueChange = { viewModel.onNarrativeChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 8,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5165F6),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Editable summary · make it sound like you.",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Info Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color(0xFFEEF2FF), shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Canvas(modifier = Modifier.size(20.dp).padding(top = 2.dp)) {
                    val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    val color = Color(0xFF5165F6)
                    drawCircle(color, radius = size.width * 0.42f, center = Offset(size.width * 0.5f, size.height * 0.5f), style = stroke)
                    drawCircle(color, radius = 1.2.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.32f))
                    drawLine(color, Offset(size.width * 0.5f, size.height * 0.48f), Offset(size.width * 0.5f, size.height * 0.72f), strokeWidth = stroke.width)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Your changes apply to this summary, not to the original diary entries.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 17.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Source Material Text
            Text(
                text = state.sourceMaterialText,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Action Buttons
            Button(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5165F6))
            ) {
                Text(
                    text = "Save",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF5165F6)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
