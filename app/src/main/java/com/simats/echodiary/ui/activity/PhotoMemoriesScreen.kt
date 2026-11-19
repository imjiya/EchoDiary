package com.simats.echodiary.ui.activity

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.echodiary.data.local.DiaryActivityStore
import com.simats.echodiary.ui.theme.EchoTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhotoMemoriesScreen(
    viewModel: PhotoMemoriesViewModel = remember { PhotoMemoriesViewModel() },
    onNavigateBack: () -> Unit,
    onNavigateToActivityDetails: (DiaryEntryItem?) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var previewPhotoUri by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is PhotoMemoriesNavigationEvent.NavigateBack -> onNavigateBack()
                is PhotoMemoriesNavigationEvent.NavigateToActivityDetails -> onNavigateToActivityDetails(event.item)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Photo memories",
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

            // Header Title
            Text(
                text = state.headerTitle,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = EchoTheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Header Subtitle
            Text(
                text = "Photos connected to your diary entries, organized by date and captured moments.",
                fontSize = 14.sp,
                color = EchoTheme.textSecondary
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (state.photoMemories.isEmpty()) {
                // Empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                        .background(color = EchoTheme.surface, shape = RoundedCornerShape(20.dp))
                        .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(20.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🖼️", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No photo memories yet",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = EchoTheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Add photos to your diary entries to see your moments captured and formatted into collages according to their date and time.",
                            fontSize = 13.sp,
                            color = EchoTheme.textSecondary,
                            textAlign = TextAlign.Center,
                            lineHeight = 18.sp
                        )
                    }
                }
            } else {
                // Date Groups Timeline
                state.dateGroups.forEach { dateGroup ->
                    // Date Header Section
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "📅", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = dateGroup.formattedDate,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoTheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(EchoTheme.container, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = if (dateGroup.totalPhotosInDate == 1) "1 photo" else "${dateGroup.totalPhotosInDate} photos",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EchoTheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Memories inside this Date
                    dateGroup.memories.forEach { item ->
                        val photos = item.getAllPhotos()

                        // Photo Memory Card Container
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = EchoTheme.surface, shape = RoundedCornerShape(24.dp))
                                .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(24.dp))
                                .padding(16.dp)
                        ) {
                            Column {
                                // Time & Category Header
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = item.timeHeader,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = EchoTheme.primary
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .background(EchoTheme.container, RoundedCornerShape(8.dp))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = item.category,
                                                fontSize = 11.sp,
                                                fontWeight = EchoTheme.textMedium,
                                                color = EchoTheme.primary
                                            )
                                        }
                                    }

                                    if (item.mood.isNotBlank() && !item.mood.equals("not specified", ignoreCase = true)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = DiaryActivityStore.getMoodEmoji(item.mood), fontSize = 14.sp)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(
                                                text = item.mood,
                                                fontSize = 12.sp,
                                                fontWeight = EchoTheme.textMedium,
                                                color = EchoTheme.textSecondary
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Dynamic Photo Collage according to time & photo count
                                MemoryPhotoCollage(
                                    photos = photos,
                                    context = context,
                                    icon = item.icon,
                                    title = item.title,
                                    onPhotoClick = { clickedUri ->
                                        previewPhotoUri = clickedUri
                                    }
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = item.imageCaption,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = EchoTheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = item.imageSubcaption,
                                    fontSize = 12.sp,
                                    color = EchoTheme.textSecondary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Associated Entry Card for the respective memory
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(color = EchoTheme.surface, shape = RoundedCornerShape(20.dp))
                                .border(1.dp, EchoTheme.cardBorder, RoundedCornerShape(20.dp))
                                .clickable {
                                    val raw = item.rawEntry ?: DiaryEntryItem(
                                        id = item.id,
                                        title = item.title,
                                        category = item.category,
                                        date = item.date,
                                        description = item.description,
                                        icon = item.icon,
                                        mood = item.mood,
                                        hasPhoto = item.hasPhoto,
                                        photoUri = item.photoUri,
                                        photoUris = item.photoUris,
                                        time = item.time
                                    )
                                    viewModel.onEntryClicked(raw)
                                }
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(EchoTheme.container, shape = RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = item.icon.ifBlank { "📝" }, fontSize = 20.sp)
                                }
                                Spacer(modifier = Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.title,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EchoTheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${item.category} · ${item.date}${if (item.time.isNotBlank()) " at ${item.time}" else ""}",
                                        fontSize = 12.sp,
                                        color = EchoTheme.textSecondary
                                    )
                                    if (item.description.isNotBlank()) {
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = item.description,
                                            fontSize = 13.sp,
                                            color = EchoTheme.onSurface,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                                Text(text = "›", fontSize = 18.sp, color = EchoTheme.textMuted, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

            // Footer Text
            Text(
                text = state.footerText,
                fontSize = 13.sp,
                color = EchoTheme.textSecondary
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Photo Preview Dialog
    previewPhotoUri?.let { pathOrUri ->
        val previewBitmap = remember(pathOrUri) {
            decodeImageBitmap(context, pathOrUri)
        }

        AlertDialog(
            onDismissRequest = { previewPhotoUri = null },
            title = {
                Text(
                    text = "Photo Memory",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = EchoTheme.onSurface
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(EchoTheme.container),
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
                        Text(text = "🖼️ Preview not available", color = EchoTheme.textSecondary)
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { previewPhotoUri = null }) {
                    Text(text = "Close", color = EchoTheme.primary, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = EchoTheme.surface,
            shape = RoundedCornerShape(24.dp)
        )
    }
}

@Composable
private fun MemoryPhotoCollage(
    photos: List<String>,
    context: Context,
    icon: String,
    title: String,
    onPhotoClick: (String) -> Unit
) {
    when (photos.size) {
        0 -> {
            // Illustrative Placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(EchoTheme.container, EchoTheme.surface)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = icon.ifBlank { "🖼️" }, fontSize = 36.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "$title photo",
                        fontSize = 14.sp,
                        fontWeight = EchoTheme.textMedium,
                        color = EchoTheme.textSecondary
                    )
                }
            }
        }
        1 -> {
            // 1 Photo: Full width banner
            CollagePhotoCell(
                pathOrUri = photos[0],
                context = context,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .clip(RoundedCornerShape(16.dp)),
                onClick = { onPhotoClick(photos[0]) }
            )
        }
        2 -> {
            // 2 Photos: Side-by-side equal columns
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CollagePhotoCell(
                    pathOrUri = photos[0],
                    context = context,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(14.dp)),
                    onClick = { onPhotoClick(photos[0]) }
                )
                CollagePhotoCell(
                    pathOrUri = photos[1],
                    context = context,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(14.dp)),
                    onClick = { onPhotoClick(photos[1]) }
                )
            }
        }
        3 -> {
            // 3 Photos: 1 Hero left + 2 stacked right
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CollagePhotoCell(
                    pathOrUri = photos[0],
                    context = context,
                    modifier = Modifier
                        .weight(1.3f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(14.dp)),
                    onClick = { onPhotoClick(photos[0]) }
                )
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CollagePhotoCell(
                        pathOrUri = photos[1],
                        context = context,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp)),
                        onClick = { onPhotoClick(photos[1]) }
                    )
                    CollagePhotoCell(
                        pathOrUri = photos[2],
                        context = context,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp)),
                        onClick = { onPhotoClick(photos[2]) }
                    )
                }
            }
        }
        else -> {
            // 4+ Photos: 2x2 Grid Collage
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CollagePhotoCell(
                        pathOrUri = photos[0],
                        context = context,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp)),
                        onClick = { onPhotoClick(photos[0]) }
                    )
                    CollagePhotoCell(
                        pathOrUri = photos[1],
                        context = context,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp)),
                        onClick = { onPhotoClick(photos[1]) }
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CollagePhotoCell(
                        pathOrUri = photos[2],
                        context = context,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp)),
                        onClick = { onPhotoClick(photos[2]) }
                    )
                    // 4th cell: with overlay if more than 4 photos
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        CollagePhotoCell(
                            pathOrUri = photos[3],
                            context = context,
                            modifier = Modifier.fillMaxSize(),
                            onClick = { onPhotoClick(photos[3]) }
                        )
                        if (photos.size > 4) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.55f))
                                    .clickable { onPhotoClick(photos[3]) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "+${photos.size - 3}",
                                    color = Color.White,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CollagePhotoCell(
    pathOrUri: String,
    context: Context,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val bitmap = remember(pathOrUri) {
        decodeImageBitmap(context, pathOrUri)
    }

    Box(
        modifier = modifier
            .background(Color(0xFFE2E8F0))
            .clickable { onClick() }
    ) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap,
                contentDescription = "Collage photo",
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
    }
}

private fun decodeImageBitmap(context: Context, pathOrUri: String): ImageBitmap? {
    return try {
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
