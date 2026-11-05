package com.simats.echodiary.ui.profile

import android.app.TimePickerDialog
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.echodiary.ui.theme.EchoTheme
import java.util.Calendar

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = remember { NotificationsViewModel() },
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val primaryColor = EchoTheme.primary
    val containerColor = EchoTheme.container
    var showWeeklyDayDialog by remember { mutableStateOf(false) }

    val daysOfWeek = listOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    Scaffold(
        containerColor = EchoTheme.background
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
                    text = "Notifications",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Screen Header Titles
            Text(
                text = "A gentle nudge.",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Choose the reminders that feel right for you.",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Status / Feedback Banner
            if (!state.statusMessage.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = if (state.isSavedSuccess) Color(0xFFECFDF5) else Color(0xFFFEF2F2),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .border(
                            width = 1.dp,
                            color = if (state.isSavedSuccess) Color(0xFF6EE7B7) else Color(0xFFFECACA),
                            shape = RoundedCornerShape(14.dp)
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "✓  ${state.statusMessage}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (state.isSavedSuccess) Color(0xFF047857) else Color(0xFFDC2626),
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "✕",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B),
                            modifier = Modifier
                                .clickable { viewModel.clearStatusMessage() }
                                .padding(start = 8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Reminders Toggles Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, RoundedCornerShape(20.dp))
                    .padding(vertical = 12.dp, horizontal = 16.dp)
            ) {
                // Diary reminders toggle
                NotificationToggleItem(
                    title = "Diary reminders",
                    subtitle = "A moment to reflect on your day",
                    checked = state.diaryRemindersEnabled,
                    activeTrackColor = primaryColor,
                    onCheckedChange = { viewModel.onDiaryRemindersToggled(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Weekly memory reminders toggle
                NotificationToggleItem(
                    title = "Weekly memory reminders",
                    subtitle = "Revisit your week on Sundays",
                    checked = state.weeklyMemoryRemindersEnabled,
                    activeTrackColor = primaryColor,
                    onCheckedChange = { viewModel.onWeeklyMemoryRemindersToggled(it) }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Summary updates toggle
                NotificationToggleItem(
                    title = "Summary updates",
                    subtitle = "When a memory is ready to review",
                    checked = state.summaryUpdatesEnabled,
                    activeTrackColor = primaryColor,
                    onCheckedChange = { viewModel.onSummaryUpdatesToggled(it) }
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Reminder Schedule Section
            Text(
                text = "Reminder schedule",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Daily reminder input with time picker trigger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily reminder",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Pick time 🕒",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryColor,
                    modifier = Modifier
                        .clickable {
                            val cal = Calendar.getInstance()
                            TimePickerDialog(
                                context,
                                { _, hourOfDay, minute ->
                                    val isPm = hourOfDay >= 12
                                    val formattedHour = when {
                                        hourOfDay == 0 -> 12
                                        hourOfDay > 12 -> hourOfDay - 12
                                        else -> hourOfDay
                                    }
                                    val formattedMinute = String.format("%02d", minute)
                                    val amPm = if (isPm) "PM" else "AM"
                                    viewModel.onDailyReminderTimeChanged("$formattedHour:$formattedMinute $amPm")
                                },
                                cal.get(Calendar.HOUR_OF_DAY),
                                cal.get(Calendar.MINUTE),
                                false
                            ).show()
                        }
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = state.dailyReminderTime,
                onValueChange = { viewModel.onDailyReminderTimeChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                trailingIcon = {
                    IconButton(onClick = {
                        val cal = Calendar.getInstance()
                        TimePickerDialog(
                            context,
                            { _, hourOfDay, minute ->
                                val isPm = hourOfDay >= 12
                                val formattedHour = when {
                                    hourOfDay == 0 -> 12
                                    hourOfDay > 12 -> hourOfDay - 12
                                    else -> hourOfDay
                                }
                                val formattedMinute = String.format("%02d", minute)
                                val amPm = if (isPm) "PM" else "AM"
                                viewModel.onDailyReminderTimeChanged("$formattedHour:$formattedMinute $amPm")
                            },
                            cal.get(Calendar.HOUR_OF_DAY),
                            cal.get(Calendar.MINUTE),
                            false
                        ).show()
                    }) {
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val color = Color(0xFF94A3B8)
                            drawCircle(color, radius = size.width * 0.42f, center = Offset(size.width * 0.5f, size.height * 0.5f), style = stroke)
                            val path = Path().apply {
                                moveTo(size.width * 0.5f, size.height * 0.25f)
                                lineTo(size.width * 0.5f, size.height * 0.5f)
                                lineTo(size.width * 0.7f, size.height * 0.5f)
                            }
                            drawPath(path, color, style = stroke)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Weekly reminder input with day & time selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Weekly reminder",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = "Pick day & time 📅",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = primaryColor,
                    modifier = Modifier
                        .clickable { showWeeklyDayDialog = true }
                        .padding(4.dp)
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = state.weeklyReminderTime,
                onValueChange = { viewModel.onWeeklyReminderTimeChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                trailingIcon = {
                    IconButton(onClick = { showWeeklyDayDialog = true }) {
                        Canvas(modifier = Modifier.size(20.dp)) {
                            val stroke = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val color = Color(0xFF94A3B8)
                            drawRect(
                                color = color,
                                topLeft = Offset(0f, size.height * 0.2f),
                                size = Size(size.width, size.height * 0.72f),
                                style = stroke
                            )
                            drawLine(color, Offset(0f, size.height * 0.42f), Offset(size.width, size.height * 0.42f), strokeWidth = stroke.width)
                            drawLine(color, Offset(size.width * 0.3f, size.height * 0.05f), Offset(size.width * 0.3f, size.height * 0.25f), strokeWidth = stroke.width)
                            drawLine(color, Offset(size.width * 0.7f, size.height * 0.05f), Offset(size.width * 0.7f, size.height * 0.25f), strokeWidth = stroke.width)
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Notifications trigger gently according to your chosen schedule.",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Send Test Reminder Button
            Button(
                onClick = {
                    viewModel.sendTestNotification(context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = containerColor,
                    contentColor = primaryColor
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔔 ", fontSize = 16.sp)
                    Text(
                        text = "Send test reminder",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = primaryColor
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Save Preferences Button
            Button(
                onClick = {
                    viewModel.savePreferences()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
            ) {
                Text(
                    text = "Save preferences",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Weekly Day Selection Dialog
    if (showWeeklyDayDialog) {
        AlertDialog(
            onDismissRequest = { showWeeklyDayDialog = false },
            title = {
                Text(text = "Choose weekly reminder day", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            },
            text = {
                Column {
                    daysOfWeek.forEach { day ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    showWeeklyDayDialog = false
                                    // Open time picker for chosen day
                                    val cal = Calendar.getInstance()
                                    TimePickerDialog(
                                        context,
                                        { _, hourOfDay, minute ->
                                            val isPm = hourOfDay >= 12
                                            val formattedHour = when {
                                                hourOfDay == 0 -> 12
                                                hourOfDay > 12 -> hourOfDay - 12
                                                else -> hourOfDay
                                            }
                                            val formattedMinute = String.format("%02d", minute)
                                            val amPm = if (isPm) "PM" else "AM"
                                            viewModel.onWeeklyReminderTimeChanged("$day · $formattedHour:$formattedMinute $amPm")
                                        },
                                        cal.get(Calendar.HOUR_OF_DAY),
                                        cal.get(Calendar.MINUTE),
                                        false
                                    ).show()
                                }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📅  $day",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF1E293B)
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showWeeklyDayDialog = false }) {
                    Text("Cancel", color = Color(0xFF64748B))
                }
            }
        )
    }
}

@Composable
private fun NotificationToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    activeTrackColor: Color,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = activeTrackColor,
                uncheckedThumbColor = Color.White,
                uncheckedTrackColor = Color(0xFFE2E8F0),
                uncheckedBorderColor = Color.Transparent
            )
        )
    }
}
