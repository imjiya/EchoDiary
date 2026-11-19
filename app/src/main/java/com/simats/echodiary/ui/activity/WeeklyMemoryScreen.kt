package com.simats.echodiary.ui.activity

import com.simats.echodiary.util.PhotoStorageHelper
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.echodiary.ui.theme.EchoTheme
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeeklyMemoryScreen(
    viewModel: WeeklyMemoryViewModel = remember { WeeklyMemoryViewModel() },
    onNavigateBack: () -> Unit,
    onNavigateToMemories: () -> Unit,
    onNavigateToAllActivities: () -> Unit,
    onNavigateToActivityAndMood: () -> Unit,
    onNavigateToEditYourMemory: () -> Unit = {},
    onNavigateToMemoryDetails: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var previewPhotoUri by remember { mutableStateOf<String?>(null) }

    val photoBitmap = remember(state.photoUri) {
        PhotoStorageHelper.decodeSampledBitmap(context, state.photoUri, 1024, 1024)
    }

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is WeeklyMemoryNavigationEvent.NavigateBack -> onNavigateBack()
                is WeeklyMemoryNavigationEvent.NavigateToMemories -> onNavigateToMemories()
                is WeeklyMemoryNavigationEvent.NavigateToAllActivities -> onNavigateToAllActivities()
                is WeeklyMemoryNavigationEvent.NavigateToActivityAndMood -> onNavigateToActivityAndMood()
                is WeeklyMemoryNavigationEvent.NavigateToEditYourMemory -> onNavigateToEditYourMemory()
                is WeeklyMemoryNavigationEvent.NavigateToMemoryDetails -> onNavigateToMemoryDetails()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Weekly Memory",
                        fontSize = 19.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface
                    )
                },
                navigationIcon = {
                    val backColor = EchoTheme.onSurface
                    IconButton(
                        onClick = { viewModel.onBackClicked() },
                        modifier = Modifier.padding(start = 4.dp)
                    ) {
                        Canvas(modifier = Modifier.size(22.dp)) {
                            val stroke = Stroke(width = 2.2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val path = Path().apply {
                                moveTo(size.width * 0.65f, size.height * 0.2f)
                                lineTo(size.width * 0.35f, size.height * 0.5f)
                                lineTo(size.width * 0.65f, size.height * 0.8f)
                            }
                            drawPath(path, backColor, style = stroke)
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onShareClicked(context) }) {
                        Text(text = "↗️", fontSize = 18.sp)
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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Subtitle Tag: Date Range & AI Draft
            Box(
                modifier = Modifier
                    .background(EchoTheme.container, shape = RoundedCornerShape(10.dp))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = state.dateRangeText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EchoTheme.primary,
                    letterSpacing = 0.5.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Header Title
            Text(
                text = state.title,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = EchoTheme.onSurface,
                lineHeight = 30.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Summary Card (Editorial AI Story Card)
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(EchoTheme.container, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "✨", fontSize = 18.sp)
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Weekly Story",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoTheme.onSurface
                            )
                            Text(
                                text = "Synthesized from your daily reflections",
                                fontSize = 11.sp,
                                color = EchoTheme.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = state.summaryText,
                        fontSize = 15.sp,
                        color = EchoTheme.onSurface,
                        lineHeight = 23.sp
                    )
                }
            }

            // Attached Photo Section
            if (state.hasPhoto && photoBitmap != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(210.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(EchoTheme.container)
                        .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(20.dp))
                        .clickable { previewPhotoUri = state.photoUri }
                ) {
                    Image(
                        bitmap = photoBitmap,
                        contentDescription = "Weekly memory photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(10.dp)
                            .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "📷 Memory Photo · Tap to view",
                            fontSize = 11.sp,
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Highlights section
            if (state.highlights.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🌟", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Highlights",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Highlights Chips
                val chunks = state.highlights.chunked(2)
                chunks.forEach { rowHighlights ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        rowHighlights.forEach { highlight ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(18.dp))
                                    .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(18.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .background(EchoTheme.primary, CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = highlight,
                                        fontSize = 13.sp,
                                        fontWeight = EchoTheme.textMedium,
                                        color = EchoTheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = state.footerText,
                fontSize = 12.sp,
                color = EchoTheme.textSecondary,
                modifier = Modifier.padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Save Memory Button
            Button(
                onClick = { viewModel.onSaveMemoryClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.primary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔖", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Save Weekly Memory",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Edit Summary & Share Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { viewModel.onEditSummaryClicked() },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.container)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "✏️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Edit Summary",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EchoTheme.primary
                        )
                    }
                }

                Button(
                    onClick = { viewModel.onShareClicked(context) },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(25.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.container)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "↗️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Share Card",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EchoTheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // View Details Link
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onViewDetailsClicked() }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "View Diary Activities in this Week",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoTheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "→", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EchoTheme.primary)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // Photo Preview Lightbox Dialog
    previewPhotoUri?.let { path ->
        val previewBitmap = remember(path) {
            PhotoStorageHelper.decodeSampledBitmap(context, path, 1200, 1200)
        }
        AlertDialog(
            onDismissRequest = { previewPhotoUri = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Weekly Memory Photo",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface
                    )
                    IconButton(onClick = { previewPhotoUri = null }) {
                        Text(text = "✕", fontSize = 16.sp, color = EchoTheme.textSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.Black.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (previewBitmap != null) {
                        Image(
                            bitmap = previewBitmap,
                            contentDescription = "Full weekly photo preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } else {
                        Text(text = "🖼️ Preview not available", color = Color.White)
                    }
                }
            },
            confirmButton = {},
            containerColor = EchoTheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}
