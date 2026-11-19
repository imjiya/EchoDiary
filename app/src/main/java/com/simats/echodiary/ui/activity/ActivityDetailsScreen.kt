package com.simats.echodiary.ui.activity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.ui.theme.EchoTheme
import com.simats.echodiary.util.PhotoStorageHelper
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailsScreen(
    viewModel: ActivityDetailsViewModel = remember { ActivityDetailsViewModel() },
    initialTitle: String = "",
    initialDescription: String = "",
    initialDate: String = "",
    initialCategory: String = "",
    initialMood: String = "",
    entryId: Int = 0,
    initialPhotoUri: String? = null,
    initialHasPhoto: Boolean = false,
    initialPhotoUris: List<String> = emptyList(),
    onNavigateBack: () -> Unit,
    onNavigateToEdit: () -> Unit,
    onNavigateToHome: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var previewPhotoUri by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val attachedPhotos = if (state.photoUris.isNotEmpty()) state.photoUris else listOfNotNull(state.photoUri)
    val photoBitmap = remember(state.photoUri, attachedPhotos) {
        val targetUri = state.photoUri ?: attachedPhotos.firstOrNull()
        PhotoStorageHelper.decodeSampledBitmap(context, targetUri, 1024, 1024)
    }

    LaunchedEffect(initialTitle, initialDescription, initialDate, initialCategory, initialMood, entryId, initialPhotoUri, initialHasPhoto, initialPhotoUris) {
        if (initialTitle.isNotBlank() || initialDescription.isNotBlank() || initialDate.isNotBlank()) {
            viewModel.setEntryDetails(
                title = initialTitle,
                description = initialDescription,
                date = initialDate,
                category = initialCategory,
                mood = initialMood,
                id = entryId,
                photoUri = initialPhotoUri,
                hasPhoto = initialHasPhoto,
                photoUris = initialPhotoUris
            )
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is ActivityDetailsNavigationEvent.NavigateBack -> onNavigateBack()
                is ActivityDetailsNavigationEvent.NavigateToEdit -> onNavigateToEdit()
                is ActivityDetailsNavigationEvent.NavigateToDelete -> onNavigateToHome()
            }
        }
    }

    val catIcon = DiaryActivityStore.getCategoryIcon(state.category)
    val moodEmoji = DiaryActivityStore.getMoodEmoji(state.mood)
    val hasExplicitMood = state.mood.isNotBlank() &&
            !state.mood.equals("not specified", ignoreCase = true) &&
            !state.mood.equals("unspecified", ignoreCase = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Activity Details",
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
                    IconButton(onClick = { viewModel.onEditClicked() }) {
                        Text(text = "✏️", fontSize = 18.sp)
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

            // Hero Header Card: Icon, Title, and Meta Chips
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(24.dp))
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
                                .size(50.dp)
                                .background(EchoTheme.container, shape = CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = catIcon.ifBlank { "📝" }, fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = state.category.ifBlank { "Diary" },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoTheme.primary,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = state.date.ifBlank { "Today" },
                                fontSize = 13.sp,
                                color = EchoTheme.textSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = state.title.ifBlank { "Untitled Memory" },
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface,
                        lineHeight = 28.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Meta Chips Row: Category badge & Mood badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .background(EchoTheme.container, shape = RoundedCornerShape(10.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🏷️", fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = state.category,
                                    fontSize = 12.sp,
                                    fontWeight = EchoTheme.textMedium,
                                    color = EchoTheme.primary
                                )
                            }
                        }

                        if (hasExplicitMood) {
                            Box(
                                modifier = Modifier
                                    .background(EchoTheme.container, shape = RoundedCornerShape(10.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = moodEmoji, fontSize = 13.sp)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = state.mood,
                                        fontSize = 12.sp,
                                        fontWeight = EchoTheme.textMedium,
                                        color = EchoTheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Reflection / Description Narrative Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.surface, shape = RoundedCornerShape(24.dp))
                    .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(text = "✍️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Personal Reflection",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoTheme.onSurface
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (state.description.isNotBlank()) state.description else "No written notes for this entry. Tap Edit to add your reflections.",
                        fontSize = 15.sp,
                        color = if (state.description.isNotBlank()) EchoTheme.onSurface else EchoTheme.textSecondary,
                        lineHeight = 22.sp
                    )
                }
            }

            // Photo Card (only when photos are attached)
            if (state.hasPhoto || attachedPhotos.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = EchoTheme.surface, shape = RoundedCornerShape(24.dp))
                        .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(24.dp))
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📷", fontSize = 16.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (attachedPhotos.size > 1) "Attached Photos (${attachedPhotos.size})" else "Attached Photo",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EchoTheme.onSurface
                                )
                            }
                            Text(
                                text = "Stored in memory ✓",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EchoTheme.primary
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        if (attachedPhotos.size > 1) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                attachedPhotos.forEachIndexed { index, path ->
                                    val thumb = remember(path) {
                                        PhotoStorageHelper.decodeSampledBitmap(context, path, 400, 400)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(120.dp)
                                            .clip(RoundedCornerShape(16.dp))
                                            .background(EchoTheme.container)
                                            .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(16.dp))
                                            .clickable { previewPhotoUri = path }
                                    ) {
                                        if (thumb != null) {
                                            Image(
                                                bitmap = thumb,
                                                contentDescription = "Attached photo ${index + 1}",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                Text(text = "🖼️", fontSize = 26.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        } else if (photoBitmap != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(220.dp)
                                    .clip(RoundedCornerShape(18.dp))
                                    .background(EchoTheme.container)
                                    .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(18.dp))
                                    .clickable { previewPhotoUri = state.photoUri ?: attachedPhotos.firstOrNull() }
                            ) {
                                Image(
                                    bitmap = photoBitmap,
                                    contentDescription = "Memory photo",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(10.dp)
                                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "🔍 Tap to view",
                                        fontSize = 11.sp,
                                        color = Color.White,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(EchoTheme.container),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(text = "🖼️", fontSize = 28.sp)
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = "Photo stored in memory",
                                        fontSize = 13.sp,
                                        fontWeight = EchoTheme.textMedium,
                                        color = EchoTheme.primary
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Entry ID / Sandboxed Storage Status
            Text(
                text = state.entryIdText,
                fontSize = 12.sp,
                color = EchoTheme.textMuted,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Action Button 1: Edit Memory
            Button(
                onClick = { viewModel.onEditClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.primary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "✏️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Edit Memory",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Button 2: Delete Entry with confirmation
            Button(
                onClick = { showDeleteDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (EchoTheme.isDark) Color(0xFF381515) else Color(0xFFFFF1F2)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🗑️", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Delete Activity",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFEF4444)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = "Delete Activity?",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EchoTheme.onSurface
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to permanently delete \"${state.title}\"? This action cannot be undone.",
                    fontSize = 14.sp,
                    color = EchoTheme.textSecondary,
                    lineHeight = 20.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.onDeleteClicked()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text(text = "Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = "Cancel", color = EchoTheme.textSecondary)
                }
            },
            containerColor = EchoTheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
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
                        text = "Attached Photo",
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
                            contentDescription = "Full photo preview",
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
