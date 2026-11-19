package com.simats.echodiary.ui.activity

import com.simats.echodiary.ui.theme.EchoTheme

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
    val context = LocalContext.current
    var showDatePicker by remember { mutableStateOf(false) }

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

    // Launcher to select photo(s) from gallery
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            val persistentPhotoPaths = uris.map { savePhotoToMemory(it) }
            viewModel.onPhotosSelected(persistentPhotoPaths)
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
            // Permission granted: open gallery to import photo
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

    val photoBitmap = remember(state.photoUri) {
        state.photoUri?.let { pathOrUri ->
            try {
                val file = java.io.File(pathOrUri)
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
                    Text("Cancel", color = EchoTheme.textSecondary)
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
                actions = {
                    IconButton(onClick = { onNavigateToCalendar() }) {
                        Text(text = "📅", fontSize = 20.sp)
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

            // Draft subtitle
            Text(
                text = "WRITE MANUALLY · Stored directly in your diary",
                fontSize = 12.sp,
                color = EchoTheme.primary,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
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
                placeholder = {
                    Text("e.g. Lectures and project", color = EchoTheme.textSecondary, fontSize = 14.sp)
                },
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
                placeholder = {
                    Text("Write about your thoughts, feelings, or activities...", color = EchoTheme.textSecondary, fontSize = 14.sp)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
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
                            color = EchoTheme.onSurface,
                            modifier = Modifier.padding(bottom = 6.dp)
                        )
                        Text(
                            text = "Calendar 📅",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EchoTheme.primary,
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

                // Category Column
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Category",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = EchoTheme.onSurface,
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
                                Text(text = "▼", fontSize = 12.sp, color = EchoTheme.textSecondary, modifier = Modifier.padding(end = 12.dp))
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledBorderColor = EchoTheme.cardBorder,
                                disabledContainerColor = EchoTheme.surface,
                                disabledTextColor = EchoTheme.onSurface
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

            // Add photo button - asks for gallery permission and imports photo(s)
            val currentPhotos = if (state.photoUris.isNotEmpty()) state.photoUris else if (!state.photoUri.isNullOrBlank()) listOf(state.photoUri!!) else emptyList()
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
                        text = if (currentPhotos.isNotEmpty()) "Add more photos (${currentPhotos.size} selected)" else "Import photos from gallery · multiple allowed",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = EchoTheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Photo Preview / Multi-Photo Thumbnails Container
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
                            try {
                                val file = java.io.File(pathOrUri)
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

                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFFE2E8F0))
                        ) {
                            if (itemBitmap != null) {
                                Image(
                                    bitmap = itemBitmap,
                                    contentDescription = "Selected photo $index",
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
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = if (state.hasPhoto) "Photos will be displayed in your memory collages." else "Click above to select single or multiple photos from your gallery.",
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
                        fontWeight = EchoTheme.textMedium
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
            } else if (!state.statusMessage.isNullOrBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFEFF6FF), shape = RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFBFDBFE), shape = RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = state.statusMessage ?: "",
                        fontSize = 13.sp,
                        color = Color(0xFF1D4ED8),
                        fontWeight = EchoTheme.textMedium
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
                colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.primary)
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
                    color = EchoTheme.textSecondary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
