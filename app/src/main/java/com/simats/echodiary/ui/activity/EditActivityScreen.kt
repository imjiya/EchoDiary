package com.simats.echodiary.ui.activity

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
    onNavigateBack: () -> Unit,
    onNavigateToAllActivities: () -> Unit
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

    // Function to persist the imported gallery photo into internal storage
    fun savePhotoToMemory(uri: Uri): String {
        return try {
            val photosDir = java.io.File(context.filesDir, "diary_photos")
            if (!photosDir.exists()) {
                photosDir.mkdirs()
            }
            val destinationFile = java.io.File(photosDir, "photo_${System.currentTimeMillis()}.jpg")
            context.contentResolver.openInputStream(uri)?.use { input ->
                java.io.FileOutputStream(destinationFile).use { output ->
                    input.copyTo(output)
                }
            }
            destinationFile.absolutePath
        } catch (_: Exception) {
            uri.toString()
        }
    }

    // Launcher to select photo from gallery
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val persistentPhotoPath = savePhotoToMemory(uri)
            viewModel.onPhotoSelected(persistentPhotoPath)
        }
    }

    // Appropriate permissions based on Android API level
    val permissionsToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
    } else {
        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

    // Launcher to request gallery access permission
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsMap ->
        val isGranted = permissionsMap.values.any { it }
        if (isGranted) {
            photoPickerLauncher.launch("image/*")
        } else {
            viewModel.onPermissionDenied("Gallery permission is required to import photos.")
        }
    }

    fun onPhotoButtonClicked() {
        val hasPermission = permissionsToRequest.any { perm ->
            ContextCompat.checkSelfPermission(context, perm) == PackageManager.PERMISSION_GRANTED
        }
        if (hasPermission) {
            photoPickerLauncher.launch("image/*")
        } else {
            permissionLauncher.launch(permissionsToRequest)
        }
    }

    LaunchedEffect(initialTitle, initialDescription, initialDate, initialCategory, initialMood, entryId, initialPhotoUri, initialHasPhoto) {
        if (initialTitle.isNotBlank() || initialDescription.isNotBlank() || initialDate.isNotBlank()) {
            viewModel.setInitialDetails(
                title = initialTitle,
                description = initialDescription,
                date = initialDate,
                category = initialCategory,
                mood = initialMood,
                id = entryId,
                photoUri = initialPhotoUri,
                hasPhoto = initialHasPhoto
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
            horizontalAlignment = Alignment.Start
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Subtitle
            Text(
                text = "Update the details to save in your memory of activities.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                fontWeight = FontWeight.Medium
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5165F6),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
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
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                shape = RoundedCornerShape(16.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF5165F6),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
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
                        color = Color(0xFF1E293B),
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
                            focusedBorderColor = Color(0xFF5165F6),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
                        )
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Category",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B),
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    OutlinedTextField(
                        value = state.category,
                        onValueChange = { viewModel.onCategoryChanged(it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        trailingIcon = {
                            Text(text = "▼", fontSize = 12.sp, color = Color(0xFF64748B), modifier = Modifier.padding(end = 12.dp))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF5165F6),
                            unfocusedBorderColor = Color(0xFFE2E8F0),
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White
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
                color = Color(0xFF1E293B),
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
                    focusedBorderColor = Color(0xFF5165F6),
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
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
                                color = if (isSelected) Color(0xFFE0E7FF) else Color(0xFFF1F5F9),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.onMoodChanged(moodVal) }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = label,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFF4338CA) else Color(0xFF475569)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Attached Photo Preview
            if (state.hasPhoto || !state.photoUri.isNullOrBlank()) {
                if (photoBitmap != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFFE2E8F0))
                    ) {
                        Image(
                            bitmap = photoBitmap,
                            contentDescription = "Attached photo preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { viewModel.onRemovePhoto() },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(32.dp)
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        ) {
                            Text(text = "✕", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFEEF2FF), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🖼️", fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Photo attached ✓",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "Saved in memory for this entry",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                        TextButton(onClick = { viewModel.onRemovePhoto() }) {
                            Text("Remove", color = Color(0xFFEF4444), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // Add photo button
            Button(
                onClick = { onPhotoButtonClicked() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (state.hasPhoto || !state.photoUri.isNullOrBlank()) Color(0xFFE0E7FF) else Color(0xFFEEF2FF)
                )
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🖼️ ", fontSize = 16.sp)
                    Text(
                        text = if (state.hasPhoto || !state.photoUri.isNullOrBlank()) "Change photo from gallery" else "Add photo · optional",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5165F6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (state.hasPhoto || !state.photoUri.isNullOrBlank()) "Photo attached to this entry." else "Click above to select and attach a photo from your gallery.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5165F6))
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF1F2))
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
                    color = Color(0xFF5165F6)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
