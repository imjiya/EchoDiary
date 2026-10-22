package com.simats.echodiary.ui.profile

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmptyStatesScreen(
    viewModel: EmptyStatesViewModel = remember { EmptyStatesViewModel() },
    onNavigateBack: () -> Unit,
    onNavigateToAddEntry: () -> Unit = {},
    onNavigateToAllActivities: () -> Unit = {}
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
                    text = state.title,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Screen Subheader
            Text(
                text = "Gentle starting points for a diary still taking shape.",
                fontSize = 14.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Card 1: NO ENTRIES
            EmptyStateCard(
                tag = "NO ENTRIES",
                title = "Your diary starts here.",
                body = "Capture a thought with your voice or write a few lines.",
                actionText = "Add an entry →",
                onActionClick = onNavigateToAddEntry
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 2: NO MEMORIES
            EmptyStateCard(
                tag = "NO MEMORIES",
                title = "Your stories will gather here.",
                body = "Add diary entries, then review a memory built from them.",
                actionText = "Write a diary →",
                onActionClick = onNavigateToAddEntry
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 3: NO PHOTOS
            EmptyStateCard(
                tag = "NO PHOTOS",
                title = "Make room for a picture.",
                body = "Attach a photo to an entry to see it in photo memories.",
                actionText = "Browse entries →",
                onActionClick = onNavigateToAllActivities
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 4: NO RESULTS
            EmptyStateCard(
                tag = "NO RESULTS",
                title = "No matching entries.",
                body = "Try another word or clear your filters.",
                actionText = "Clear search →",
                onActionClick = onNavigateToAllActivities
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Card 5: INSUFFICIENT SUMMARY DATA
            EmptyStateCard(
                tag = "INSUFFICIENT SUMMARY DATA",
                title = "A little more to remember.",
                body = "There are not enough entries in this period for a useful summary.",
                actionText = "Add an entry →",
                onActionClick = onNavigateToAddEntry
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Footer Text
            Text(
                text = "Full-size panels are in the component library.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EmptyStateCard(
    tag: String,
    title: String,
    body: String,
    actionText: String,
    onActionClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(20.dp))
            .padding(20.dp)
    ) {
        Text(
            text = tag,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF5165F6),
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E293B)
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = body,
            fontSize = 13.sp,
            color = Color(0xFF64748B),
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = actionText,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF5165F6),
            modifier = Modifier.clickable { onActionClick() }
        )
    }
}
