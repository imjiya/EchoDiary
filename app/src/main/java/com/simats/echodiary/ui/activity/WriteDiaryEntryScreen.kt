package com.simats.echodiary.ui.activity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteDiaryEntryScreen(
    viewModel: WriteDiaryEntryViewModel = remember { WriteDiaryEntryViewModel() },
    initialDate: String = "",
    onNavigateBack: () -> Unit,
    onNavigateToActivityDetails: (title: String, description: String, date: String, category: String) -> Unit = { _, _, _, _ -> },
    onNavigateToChooseCategory: () -> Unit,
    onNavigateToCalendar: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(initialDate) {
        if (initialDate.isNotBlank()) {
            viewModel.onDateChanged(initialDate)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is WriteDiaryEntryNavigationEvent.NavigateBack -> onNavigateBack()
                is WriteDiaryEntryNavigationEvent.NavigateToActivityDetails -> {
                    onNavigateToActivityDetails(event.title, event.description, event.date, event.category)
                }
                is WriteDiaryEntryNavigationEvent.NavigateToChooseCategory -> onNavigateToChooseCategory()
                is WriteDiaryEntryNavigationEvent.NavigateToCalendar -> onNavigateToCalendar()
            }
        }
    }

    // Material 3 Date Picker Dialog when Calendar button is clicked
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val selectedMillis = datePickerState.selectedDateMillis
                        if (selectedMillis != null) {
                            val formatter = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH)
                            viewModel.onDateChanged(formatter.format(Date(selectedMillis)))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("Select", fontWeight = FontWeight.Bold, color = Color(0xFF5165F6))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Write diary entry",
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
                actions = {
                    IconButton(onClick = { onNavigateToCalendar() }) {
                        Text(text = "📅", fontSize = 20.sp)
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

            // Draft subtitle
            Text(
                text = "WRITE MANUALLY · Stored directly in your diary",
                fontSize = 12.sp,
                color = Color(0xFF5165F6),
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title Field
            Text(
                text = "Title",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onTitleChanged(it) },
                placeholder = {
                    Text("e.g. Lectures and project", color = Color(0xFF94A3B8), fontSize = 14.sp)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5165F6),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color(0xFF1E293B),
                    unfocusedTextColor = Color(0xFF1E293B)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description Field
            Text(
                text = "Description",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onDescriptionChanged(it) },
                placeholder = {
                    Text("Write about your thoughts, feelings, or activities...", color = Color(0xFF94A3B8), fontSize = 14.sp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5165F6),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color(0xFF1E293B),
                    unfocusedTextColor = Color(0xFF1E293B)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date & Category Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date Column with Calendar Button & Picker
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Date",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = "Calendar 📅",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF5165F6),
                            modifier = Modifier
                                .clickable { showDatePicker = true }
                                .padding(bottom = 6.dp)
                        )
                    }

                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = state.date,
                            onValueChange = { viewModel.onDateChanged(it) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            trailingIcon = {
                                Box(
                                    modifier = Modifier
                                        .clickable { showDatePicker = true }
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = "📅",
                                        fontSize = 18.sp
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF5165F6),
                                unfocusedBorderColor = Color(0xFFE2E8F0),
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = Color(0xFF1E293B),
                                unfocusedTextColor = Color(0xFF1E293B)
                            )
                        )
                    }
                }

                // Category Column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Category",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.onCategoryFieldClicked() }
                    ) {
                        OutlinedTextField(
                            value = state.category,
                            onValueChange = { viewModel.onCategoryChanged(it) },
                            modifier = Modifier.fillMaxWidth(),
                            enabled = false,
                            shape = RoundedCornerShape(16.dp),
                            trailingIcon = {
                                Text(text = "▼", fontSize = 12.sp, color = Color(0xFF64748B), modifier = Modifier.padding(end = 12.dp))
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = Color(0xFFE2E8F0),
                                disabledContainerColor = Color.White,
                                disabledTextColor = Color(0xFF1E293B)
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mood optional Field
            Text(
                text = "Mood · optional",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = state.mood,
                onValueChange = { viewModel.onMoodChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    Text(text = "😊", fontSize = 18.sp, modifier = Modifier.padding(end = 12.dp))
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5165F6),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color(0xFF1E293B),
                    unfocusedTextColor = Color(0xFF1E293B)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Add photo button
            Button(
                onClick = { viewModel.onAddPhotoClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF2FF))
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🖼️ ", fontSize = 16.sp)
                    Text(
                        text = if (state.hasPhoto) "Photo attached ✓" else "Add photo · optional",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5165F6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (state.hasPhoto) "Photo added to this entry." else "No photo attached to this entry.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Error / Status Message
            if (!state.errorMessage.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFEF2F2), shape = RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFFECACA), shape = RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = state.errorMessage ?: "",
                        fontSize = 13.sp,
                        color = Color(0xFFDC2626),
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            } else if (state.saveSuccess) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFECFDF5), shape = RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF6EE7B7), shape = RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "✓ " + (state.statusMessage ?: "Entry stored in diary successfully!"),
                        fontSize = 13.sp,
                        color = Color(0xFF047857),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Save Entry Button
            Button(
                onClick = { viewModel.onSaveEntryClicked() },
                enabled = !state.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5165F6))
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "💾 ", fontSize = 16.sp)
                        Text(
                            text = "Save Entry",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cancel Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { viewModel.onCancelClicked() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
