package com.simats.echodiary.ui.activity

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.echodiary.ui.theme.EchoTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectedDateScreen(
    viewModel: SelectedDateViewModel = remember { SelectedDateViewModel() },
    onNavigateBack: () -> Unit,
    onNavigateToActivityDetails: () -> Unit,
    onNavigateToAddEntry: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is SelectedDateNavigationEvent.NavigateBack -> onNavigateBack()
                is SelectedDateNavigationEvent.NavigateToActivityDetails -> onNavigateToActivityDetails()
                is SelectedDateNavigationEvent.NavigateToAddEntry -> onNavigateToAddEntry()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Selected date",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onBackClicked() }) {
                        val backColor = EchoTheme.onSurface
                        Canvas(modifier = Modifier.size(24.dp)) {
                            val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val path = Path().apply {
                                moveTo(size.width * 0.7f, size.height * 0.2f)
                                lineTo(size.width * 0.3f, size.height * 0.5f)
                                lineTo(size.width * 0.7f, size.height * 0.8f)
                            }
                            drawPath(path, backColor, style = stroke)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = EchoTheme.background)
            )
        },
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
            Spacer(modifier = Modifier.height(12.dp))

            // Date Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = EchoTheme.surface,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = state.dayOfWeek,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.primary,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.fullDate,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = state.entrySummary,
                        fontSize = 13.sp,
                        color = EchoTheme.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Your day, in words header
            Text(
                text = "Your day, in words",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = EchoTheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Activity Item Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(20.dp))
                    .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = state.entryItem?.icon ?: "📅", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = state.entryItem?.title ?: "No entry recorded",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoTheme.onSurface
                            )
                            Text(
                                text = "${state.entryItem?.category ?: "Diary"} · ${state.fullDate}",
                                fontSize = 12.sp,
                                color = EchoTheme.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = state.entryItem?.description ?: "No diary entry recorded for this date. Tap Add an entry below to create one.",
                        fontSize = 14.sp,
                        color = EchoTheme.textSecondary
                    )
                }
            }

            if (state.hasEntry) {
                Spacer(modifier = Modifier.height(12.dp))

                // View activity details link
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { viewModel.onViewActivityDetailsClicked() }
                ) {
                    Text(
                        text = "View activity details",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoTheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "→",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Add an entry Button
            Button(
                onClick = { viewModel.onAddEntryClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.primary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "+ ", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = "Add an entry",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Back to Calendar Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clickable { viewModel.onBackToCalendarClicked() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Back to Calendar",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EchoTheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
