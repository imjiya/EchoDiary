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
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
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
import com.simats.echodiary.ui.theme.EchoTheme
import java.io.File

@Composable
fun EditYourMemoryScreen(
    viewModel: EditYourMemoryViewModel = remember { EditYourMemoryViewModel() },
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val primaryColor = EchoTheme.primary
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
                    text = "Edit your memory",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Subheader
            Text(
                text = state.dateRangeText,
                fontSize = 13.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Field 1: Memory Title
            Text(
                text = "Memory title",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = state.memoryTitle,
                onValueChange = { viewModel.onTitleChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EchoTheme.primary,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Field 2: Weekly Narrative
            Text(
                text = "Weekly narrative",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B)
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = state.weeklyNarrative,
                onValueChange = { viewModel.onNarrativeChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 4,
                maxLines = 8,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EchoTheme.primary,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Editable summary · make it sound like you.",
                fontSize = 12.sp,
                color = Color(0xFF64748B)
            )

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
                            contentDescription = "Attached memory photo preview",
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
                                    text = "Photo attached to memory ✓",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "Saved with this weekly summary",
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
                text = if (state.hasPhoto || !state.photoUri.isNullOrBlank()) "Photo attached to this weekly memory." else "Click above to select and attach a photo from your gallery.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Info Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = EchoTheme.container, shape = RoundedCornerShape(16.dp))
                    .padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Canvas(modifier = Modifier.size(20.dp).padding(top = 2.dp)) {
                    val stroke = Stroke(width = 1.6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                    val color = primaryColor
                    drawCircle(color, radius = size.width * 0.42f, center = Offset(size.width * 0.5f, size.height * 0.5f), style = stroke)
                    drawCircle(color, radius = 1.2.dp.toPx(), center = Offset(size.width * 0.5f, size.height * 0.32f))
                    drawLine(color, Offset(size.width * 0.5f, size.height * 0.48f), Offset(size.width * 0.5f, size.height * 0.72f), strokeWidth = stroke.width)
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = "Your changes apply to this summary, saved in your memory of activities.",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B),
                    lineHeight = 17.sp,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Source Material Text
            Text(
                text = state.sourceMaterialText,
                fontSize = 12.sp,
                color = Color(0xFF64748B),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Action Buttons
            Button(
                onClick = {
                    viewModel.onSaveClicked()
                    onNavigateBack()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EchoTheme.primary)
            ) {
                Text(
                    text = "Save",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            TextButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
            ) {
                Text(
                    text = "Cancel",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = EchoTheme.primary
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
