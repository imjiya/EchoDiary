package com.simats.echodiary.ui.activity

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

    val photoBitmap = remember(state.photoUri) {
        state.photoUri?.let { pathOrUri ->
            try {
                val file = File(pathOrUri)
                if (file.exists() && file.isFile) {
                    BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
                } else {
                    val uri = Uri.parse(pathOrUri)
                    if (uri.scheme == "file") {
                        BitmapFactory.decodeFile(uri.path)?.asImageBitmap()
                    } else {
                        context.contentResolver.openInputStream(uri)?.use { stream ->
                            BitmapFactory.decodeStream(stream)
                        }?.asImageBitmap()
                    }
                }
            } catch (_: Exception) {
                null
            }
        }
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
                        text = "Weekly memory",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface
                    )
                },
                navigationIcon = {
                    val backColor = EchoTheme.onSurface
                    IconButton(onClick = { viewModel.onBackClicked() }) {
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

            // Subtitle
            Text(
                text = state.dateRangeText,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EchoTheme.primary,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Header
            Text(
                text = state.title,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = EchoTheme.onSurface,
                lineHeight = 32.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Summary Card (Light theme container)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = EchoTheme.container,
                        shape = RoundedCornerShape(24.dp)
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Text(text = "✨", fontSize = 20.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = state.summaryText,
                        fontSize = 15.sp,
                        color = EchoTheme.onSurface,
                        lineHeight = 22.sp
                    )
                }
            }

            if (state.hasPhoto && photoBitmap != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color(0xFFE2E8F0))
                ) {
                    Image(
                        bitmap = photoBitmap,
                        contentDescription = "Weekly memory photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Highlights section
            Text(
                text = "Highlights",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = EchoTheme.onSurface
            )

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
                                .background(color = Color.White, shape = RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = highlight,
                                fontSize = 13.sp,
                                fontWeight = EchoTheme.textMedium,
                                color = EchoTheme.textSecondary
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = state.footerText,
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save Memory Button
            Button(
                onClick = { viewModel.onSaveMemoryClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.primary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🔖 ", fontSize = 16.sp)
                    Text(
                        text = "Save Memory",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Edit Summary & Share Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { viewModel.onEditSummaryClicked() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.container)
                ) {
                    Text(
                        text = "Edit Summary",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoTheme.primary
                    )
                }

                Button(
                    onClick = { viewModel.onShareClicked(context) },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.container)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "↗ ", fontSize = 16.sp, color = EchoTheme.primary)
                        Text(
                            text = "Share",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EchoTheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // View Details Link
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.onViewDetailsClicked() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "View Details",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EchoTheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
