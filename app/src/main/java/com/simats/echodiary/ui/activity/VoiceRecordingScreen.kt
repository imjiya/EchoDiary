package com.simats.echodiary.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoiceRecordingScreen(
    viewModel: VoiceRecordingViewModel = remember { VoiceRecordingViewModel() },
    onNavigateBack: () -> Unit,
    onNavigateToReviewTranscript: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    // Android Speech Recognition intent launcher fallback
    val speechIntentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!spoken.isNullOrEmpty()) {
                viewModel.onSpeechRecognized(spoken[0])
            }
        }
    }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
        if (!isGranted) {
            viewModel.setError("Audio permission is required to record voice.")
        }
    }

    // SpeechRecognizer reference
    val speechRecognizer = remember {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            SpeechRecognizer.createSpeechRecognizer(context)
        } else {
            null
        }
    }

    val recognizerIntent = remember {
        Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
        }
    }

    // Clean up SpeechRecognizer
    DisposableEffect(Unit) {
        onDispose {
            try {
                speechRecognizer?.stopListening()
                speechRecognizer?.cancel()
                speechRecognizer?.destroy()
            } catch (_: Exception) {}
        }
    }

    // Set listener and start listening when permission is ready
    LaunchedEffect(hasAudioPermission, speechRecognizer) {
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        } else if (speechRecognizer != null) {
            try {
                speechRecognizer.setRecognitionListener(object : RecognitionListener {
                    override fun onReadyForSpeech(params: Bundle?) {
                        viewModel.setStatus("Listening to your voice...")
                    }

                    override fun onBeginningOfSpeech() {
                        viewModel.setStatus("Recording speech...")
                    }

                    override fun onRmsChanged(rmsdB: Float) {}
                    override fun onBufferReceived(buffer: ByteArray?) {}
                    override fun onEndOfSpeech() {
                        viewModel.setStatus("Translating to proper English...")
                    }

                    override fun onError(error: Int) {
                        // Restart listening on timeout or silence if still recording and not paused
                        if (state.isRecording && !state.isPaused) {
                            try {
                                speechRecognizer.startListening(recognizerIntent)
                            } catch (_: Exception) {}
                        }
                    }

                    override fun onResults(results: Bundle?) {
                        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            viewModel.onSpeechRecognized(matches[0])
                        }
                        // Continuous listening: continue listening if still active
                        if (state.isRecording && !state.isPaused) {
                            try {
                                speechRecognizer.startListening(recognizerIntent)
                            } catch (_: Exception) {}
                        }
                    }

                    override fun onPartialResults(partialResults: Bundle?) {
                        val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        if (!matches.isNullOrEmpty()) {
                            viewModel.onPartialSpeech(matches[0])
                        }
                    }

                    override fun onEvent(eventType: Int, params: Bundle?) {}
                })

                speechRecognizer.startListening(recognizerIntent)
            } catch (e: Exception) {
                viewModel.setError("Could not initialize voice recording service.")
            }
        }
    }

    // Pause/Resume handling for SpeechRecognizer
    LaunchedEffect(state.isPaused) {
        if (state.isPaused) {
            try {
                speechRecognizer?.stopListening()
            } catch (_: Exception) {}
        } else if (hasAudioPermission && speechRecognizer != null && state.isRecording) {
            try {
                speechRecognizer.startListening(recognizerIntent)
            } catch (_: Exception) {}
        }
    }

    // Timer loop while recording and not paused
    LaunchedEffect(state.isPaused, state.isRecording) {
        var seconds = 0
        while (state.isRecording && !state.isPaused) {
            delay(1000L)
            seconds++
            viewModel.onTimerTick(seconds)
        }
    }

    // Navigation events
    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is VoiceRecordingNavigationEvent.NavigateBack -> onNavigateBack()
                is VoiceRecordingNavigationEvent.NavigateToReviewTranscript -> onNavigateToReviewTranscript(event.transcript)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Voice recording",
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
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Status Banner with detected language tag
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color(0xFFF0F4FF),
                        shape = RoundedCornerShape(14.dp)
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "🎙️", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = state.statusText,
                            fontSize = 13.sp,
                            color = Color(0xFF1E293B),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    if (state.detectedLanguage.isNotBlank() && state.detectedLanguage != "Unknown") {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF5165F6), shape = RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = state.detectedLanguage,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Main Recording & Waveform Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(vertical = 32.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Pulsing / Circular Mic Icon
                    Box(
                        modifier = Modifier
                            .size(96.dp)
                            .background(
                                color = if (state.isPaused) Color(0xFFCBD5E1) else Color(0xFF5165F6),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.size(40.dp)) {
                            val stroke = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
                            val white = Color.White

                            drawRoundRect(
                                color = white,
                                topLeft = Offset(size.width * 0.35f, size.height * 0.05f),
                                size = Size(size.width * 0.3f, size.height * 0.5f),
                                cornerRadius = CornerRadius(size.width * 0.15f),
                                style = stroke
                            )
                            drawArc(
                                color = white,
                                startAngle = 0f,
                                sweepAngle = 180f,
                                useCenter = false,
                                topLeft = Offset(size.width * 0.2f, size.height * 0.35f),
                                size = Size(size.width * 0.6f, size.height * 0.45f),
                                style = stroke
                            )
                            drawLine(
                                white,
                                Offset(size.width * 0.5f, size.height * 0.8f),
                                Offset(size.width * 0.5f, size.height * 0.95f),
                                strokeWidth = stroke.width
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = if (state.isPaused) "Recording paused" else "Tell us about your day",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Audio Waveform
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val heights = if (!state.isPaused) {
                            listOf(12, 18, 28, 16, 36, 22, 44, 28, 52, 34, 48, 26, 40, 20, 32, 14, 24, 10)
                        } else {
                            listOf(6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6)
                        }
                        heights.forEach { h ->
                            Box(
                                modifier = Modifier
                                    .width(3.dp)
                                    .height(h.dp)
                                    .background(
                                        color = if (state.isPaused) Color(0xFFCBD5E1) else Color(0xFF818CF8),
                                        shape = RoundedCornerShape(2.dp)
                                    )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = state.timerText,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (state.isPaused) "Tap Resume to continue" else "Speak in your preferred language...",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Spoken Words Card (Raw Voice from Speech Recognizer)
            if (state.spokenText.isNotBlank()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, shape = RoundedCornerShape(18.dp))
                        .border(1.dp, Color(0xFFE2E8F0), shape = RoundedCornerShape(18.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🗣️ Spoken Voice",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                            if (state.detectedLanguage.isNotBlank()) {
                                Text(
                                    text = "Language: ${state.detectedLanguage}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "\"${state.spokenText}\"",
                            fontSize = 14.sp,
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Normal
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // PROPER ENGLISH TRANSLATION CARD (Prominently displayed)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEEF2FF), shape = RoundedCornerShape(20.dp))
                    .border(1.5.dp, Color(0xFF818CF8), shape = RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "✨", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Proper English Translation",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3730A3)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(Color(0xFF4F46E5), shape = RoundedCornerShape(10.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "English",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (state.isTranslating) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(vertical = 8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = Color(0xFF5165F6)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Translating speech into proper English...",
                                fontSize = 13.sp,
                                color = Color(0xFF5165F6),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else if (state.translatedText.isNotBlank()) {
                        Text(
                            text = "\"${state.translatedText}\"",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF1E293B),
                            lineHeight = 22.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "✓ Live translation active · Ready for diary transcript",
                            fontSize = 12.sp,
                            color = Color(0xFF4338CA),
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = "Start speaking in any language. Your speech will be automatically transcribed and translated into clear, proper English here in real-time.",
                            fontSize = 13.sp,
                            color = Color(0xFF6366F1),
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            // Quick Speech Input / Test Voice Bar (Useful on Emulators)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tap to speak with Google Voice:",
                    fontSize = 12.sp,
                    color = Color(0xFF64748B)
                )

                Box(
                    modifier = Modifier
                        .background(Color.White, shape = RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFFE2E8F0), shape = RoundedCornerShape(12.dp))
                        .clickable {
                            try {
                                speechIntentLauncher.launch(recognizerIntent)
                            } catch (_: Exception) {
                                // If intent not available, test sample
                                viewModel.onSpeechRecognized("Today I attended lectures and worked on a great project with my team.")
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "🎤 Open Mic Intent",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF5165F6)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Pause / Stop Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (!state.isPaused) {
                    Button(
                        onClick = { viewModel.onPauseClicked() },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF2FF))
                    ) {
                        Text(
                            text = "⏸ Pause",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF5165F6)
                        )
                    }
                } else {
                    Button(
                        onClick = { viewModel.onResumeClicked() },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(26.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEEF2FF))
                    ) {
                        Text(
                            text = "▶ Resume",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF5165F6)
                        )
                    }
                }

                Button(
                    onClick = { viewModel.onStopClicked() },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    shape = RoundedCornerShape(26.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5165F6))
                ) {
                    Text(
                        text = "⏹ Stop & Review",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Cancel",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF64748B),
                modifier = Modifier
                    .clickable { viewModel.onCancelClicked() }
                    .padding(8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
