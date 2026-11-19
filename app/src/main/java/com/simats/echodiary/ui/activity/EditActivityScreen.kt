package com.simats.echodiary.ui.activity

import com.simats.echodiary.util.PhotoStorageHelper
import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import com.simats.echodiary.ui.theme.EchoTheme
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
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.collectLatest
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityScreen(
    viewModel: EditActivityViewModel = remember { EditActivityViewModel() },
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
    onNavigateToAllActivities: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val photoBitmap = remember(state.photoUri) {
        PhotoStorageHelper.decodeSampledBitmap(context, state.photoUri, 1024, 1024)
    }

    // Modern Photo Picker Launcher (PickMultipleVisualMedia - zero permission barrier)
    val modernPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val persistentPhotoPaths = PhotoStorageHelper.savePhotosToInternalStorage(context, uris)
            viewModel.onPhotosSelected(persistentPhotoPaths)
        }
    }

    // Fallback Photo Picker Launcher (GetMultipleContents)
    val fallbackPhotoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val persistentPhotoPaths = PhotoStorageHelper.savePhotosToInternalStorage(context, uris)
            viewModel.onPhotosSelected(persistentPhotoPaths)
        }
    }

    fun onPhotoButtonClicked() {
        try {
            modernPhotoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        } catch (_: Throwable) {
            try {
                fallbackPhotoPickerLauncher.launch("image/*")
            } catch (e: Throwable) {
                viewModel.onPermissionDenied("Unable to open photo picker: ${e.message}")
            }
        }
    }

    LaunchedEffect(initialTitle, initialDescription, initialDate, initialCategory, initialMood, entryId, initialPhotoUri, initialHasPhoto, initialPhotoUris) {
        if (initialTitle.isNotBlank() || initialDescription.isNotBlank() || initialDate.isNotBlank()) {
            viewModel.setInitialDetails(
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
                is EditActivityNavigationEvent.NavigateBack -> onNavigateBack()
                is EditActivityNavigationEvent.NavigateToAllActivities -> onNavigateToAllActivities()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Edit memory",
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
                text = "Update the details to save in your memory of activities.",
                fontSize = 12.sp,
                color = EchoTheme.textSecondary,
                fontWeight = EchoTheme.textMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title Field
            Text(
                text = "Title",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = EchoTheme.onSurface,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onTitleChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EchoTheme.primary,
                    unfocusedBorderColor = EchoTheme.cardBorder,
                    focusedContainerColor = EchoTheme.surface,
                    unfocusedContainerColor = EchoTheme.surface,
                    focusedTextColor = EchoTheme.onSurface,
                    unfocusedTextColor = EchoTheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Description Field
            Text(
                text = "Description",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = EchoTheme.onSurface,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onDescriptionChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EchoTheme.primary,
                    unfocusedBorderColor = EchoTheme.cardBorder,
                    focusedContainerColor = EchoTheme.surface,
                    unfocusedContainerColor = EchoTheme.surface,
                    focusedTextColor = EchoTheme.onSurface,
                    unfocusedTextColor = EchoTheme.onSurface
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Date & Category Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Date",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = state.date,
                        onValueChange = { viewModel.onDateChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        trailingIcon = {
                            Text(text = "📅", fontSize = 16.sp, modifier = Modifier.padding(end = 12.dp))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoTheme.primary,
                            unfocusedBorderColor = EchoTheme.cardBorder,
                            focusedContainerColor = EchoTheme.surface,
                            unfocusedContainerColor = EchoTheme.surface,
                            focusedTextColor = EchoTheme.onSurface,
                            unfocusedTextColor = EchoTheme.onSurface
                        )
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Category",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = state.category,
                        onValueChange = { viewModel.onCategoryChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        trailingIcon = {
                            Text(text = "▼", fontSize = 12.sp, color = EchoTheme.textSecondary, modifier = Modifier.padding(end = 12.dp))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EchoTheme.primary,
                            unfocusedBorderColor = EchoTheme.cardBorder,
                            focusedContainerColor = EchoTheme.surface,
                            unfocusedContainerColor = EchoTheme.surface,
                            focusedTextColor = EchoTheme.onSurface,
                            unfocusedTextColor = EchoTheme.onSurface
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Mood optional Field
            Text(
                text = "Mood · optional",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = EchoTheme.onSurface,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            OutlinedTextField(
                value = state.mood,
                onValueChange = { viewModel.onMoodChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                trailingIcon = {
                    Text(
                        text = com.simats.echodiary.data.local.DiaryActivityStore.getMoodEmoji(state.mood),
                        fontSize = 18.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EchoTheme.primary,
                    unfocusedBorderColor = EchoTheme.cardBorder,
                    focusedContainerColor = EchoTheme.surface,
                    unfocusedContainerColor = EchoTheme.surface,
                    focusedTextColor = EchoTheme.onSurface,
                    unfocusedTextColor = EchoTheme.onSurface
                )
            )

            // Quick mood suggestion chips
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "😊 Happy" to "Happy",
                    "😌 Calm" to "Calm",
                    "🎯 Focused" to "Focused",
                    "🤔 Reflective" to "Reflective",
                    "😢 Sad" to "Sad"
                ).forEach { (label, moodVal) ->
                    val isSelected = state.mood.trim().equals(moodVal, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .background(
                                color = if (isSelected) EchoTheme.primary else EchoTheme.surface,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .border(1.dp, if (isSelected) EchoTheme.primary else EchoTheme.cardBorder, RoundedCornerShape(12.dp))
                            .clickable { viewModel.onMoodChanged(moodVal) }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected || EchoTheme.isDark) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else EchoTheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Attached Photo Preview
            val currentPhotos = if (state.photoUris.isNotEmpty()) state.photoUris else if (!state.photoUri.isNullOrBlank()) listOf(state.photoUri!!) else emptyList()

            // Attached Photos Preview / Multi-Photo Strip
            if (currentPhotos.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${currentPhotos.size} photo(s) attached",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoTheme.onSurface
                    )
                    Text(
                        text = "Clear all",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFEF4444),
                        modifier = Modifier
                            .clickable { viewModel.onRemovePhoto() }
                            .padding(4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    currentPhotos.forEachIndexed { index, pathOrUri ->
                        val itemBitmap = remember(pathOrUri) {
                            PhotoStorageHelper.decodeSampledBitmap(context, pathOrUri, 300, 300)
                        }

                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFE2E8F0))
                        ) {
                            if (itemBitmap != null) {
                                Image(
                                    bitmap = itemBitmap,
                                    contentDescription = "Attached photo $index",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Box(
                                    modifier = Modifier.fillMaxSize(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "🖼️", fontSize = 24.sp)
                                }
                            }

                            // Individual Delete Button
                            IconButton(
                                onClick = { viewModel.onRemovePhotoAt(index) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(4.dp)
                                    .size(24.dp)
                                    .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                            ) {
                                Text(text = "✕", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Add photo button
            Button(
                onClick = { onPhotoButtonClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = EchoTheme.container
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🖼️ ", fontSize = 16.sp)
                    Text(
                        text = if (currentPhotos.isNotEmpty()) "Add more photos (${currentPhotos.size} selected)" else "Add photo(s) · multiple allowed",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoTheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (currentPhotos.isNotEmpty()) "Photos attached to this entry will appear in your memory collages." else "Click above to select and attach multiple photos from your gallery.",
                fontSize = 12.sp,
                color = EchoTheme.textSecondary,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Save Changes Button
            Button(
                onClick = { viewModel.onSaveClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.primary)
            ) {
                Text(
                    text = "Save Changes",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delete Entry Button
            Button(
                onClick = { viewModel.onDeleteClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(27.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (EchoTheme.isDark) Color(0xFF451A1A) else Color(0xFFFFF1F2)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🗑️ ", fontSize = 16.sp)
                    Text(
                        text = "Delete Entry",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFEF4444)
                    )
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
                    color = EchoTheme.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
