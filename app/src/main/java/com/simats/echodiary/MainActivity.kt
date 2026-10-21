package com.simats.echodiary

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.echodiary.ui.theme.EchoDiaryTheme
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EchoDiaryTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(
                        onTimeout = {
                            showSplash = false
                        }
                    )
                } else {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                            Greeting(
                                name = "Jiya",
                                modifier = Modifier.padding(innerPadding)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(onTimeout: () -> Unit) {
    LaunchedEffect(Unit) {
        delay(2500)
        onTimeout()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .background(
                        color = Color(0xFF5165F6),
                        shape = RoundedCornerShape(24.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.size(52.dp)) {
                    val stroke = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
                    val white = Color.White

                    // Left page outline
                    val leftPath = Path().apply {
                        moveTo(size.width * 0.5f, size.height * 0.15f)
                        cubicTo(
                            size.width * 0.35f, size.height * 0.12f,
                            size.width * 0.15f, size.height * 0.18f,
                            size.width * 0.1f, size.height * 0.25f
                        )
                        lineTo(size.width * 0.1f, size.height * 0.85f)
                        cubicTo(
                            size.width * 0.15f, size.height * 0.78f,
                            size.width * 0.35f, size.height * 0.75f,
                            size.width * 0.5f, size.height * 0.82f
                        )
                        close()
                    }
                    drawPath(leftPath, white, style = stroke)

                    // Right page outline
                    val rightPath = Path().apply {
                        moveTo(size.width * 0.5f, size.height * 0.15f)
                        cubicTo(
                            size.width * 0.65f, size.height * 0.12f,
                            size.width * 0.85f, size.height * 0.18f,
                            size.width * 0.9f, size.height * 0.25f
                        )
                        lineTo(size.width * 0.9f, size.height * 0.85f)
                        cubicTo(
                            size.width * 0.85f, size.height * 0.78f,
                            size.width * 0.65f, size.height * 0.75f,
                            size.width * 0.5f, size.height * 0.82f
                        )
                        close()
                    }
                    drawPath(rightPath, white, style = stroke)

                    // Spine line
                    drawLine(
                        color = white,
                        start = Offset(size.width * 0.5f, size.height * 0.15f),
                        end = Offset(size.width * 0.5f, size.height * 0.82f),
                        strokeWidth = 2.5.dp.toPx(),
                        cap = StrokeCap.Round
                    )

                    // Soundwave lines left
                    drawLine(white, Offset(size.width * 0.25f, size.height * 0.35f), Offset(size.width * 0.25f, size.height * 0.60f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                    drawLine(white, Offset(size.width * 0.37f, size.height * 0.30f), Offset(size.width * 0.37f, size.height * 0.65f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)

                    // Soundwave lines right
                    drawLine(white, Offset(size.width * 0.63f, size.height * 0.30f), Offset(size.width * 0.63f, size.height * 0.65f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                    drawLine(white, Offset(size.width * 0.75f, size.height * 0.35f), Offset(size.width * 0.75f, size.height * 0.60f), strokeWidth = 2.dp.toPx(), cap = StrokeCap.Round)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "EchoDiary AI",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1E293B),
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Your Voice. Your Day. Your Memories.",
                fontSize = 14.sp,
                color = Color(0xFF64748B),
                textAlign = TextAlign.Center
            )
        }

        Text(
            text = "A little space for your everyday.",
            fontSize = 12.sp,
            color = Color(0xFF94A3B8),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    EchoDiaryTheme {
        SplashScreen(onTimeout = {})
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    EchoDiaryTheme {
        Greeting("Android")
    }
}