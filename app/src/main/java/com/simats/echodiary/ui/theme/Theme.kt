package com.simats.echodiary.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import com.simats.echodiary.data.local.DiaryActivityStore
import java.util.Locale

data class EchoDiaryColors(
    val primary: Color,
    val primaryContainer: Color,
    val background: Color,
    val surface: Color,
    val onSurface: Color,
    val textSecondary: Color,
    val isDark: Boolean
)

val LocalEchoColors = staticCompositionLocalOf {
    EchoDiaryColors(
        primary = Color(0xFF5165F6),
        primaryContainer = Color(0xFFEEF2FF),
        background = Color(0xFFFAFBFC),
        surface = Color.White,
        onSurface = Color(0xFF1E293B),
        textSecondary = Color(0xFF64748B),
        isDark = false
    )
}

object EchoTheme {
    val colors: EchoDiaryColors
        @Composable
        get() = LocalEchoColors.current

    val primary: Color
        @Composable
        get() = LocalEchoColors.current.primary

    val container: Color
        @Composable
        get() = LocalEchoColors.current.primaryContainer

    val background: Color
        @Composable
        get() = LocalEchoColors.current.background

    val surface: Color
        @Composable
        get() = LocalEchoColors.current.surface

    val onSurface: Color
        @Composable
        get() = LocalEchoColors.current.onSurface

    val textSecondary: Color
        @Composable
        get() = LocalEchoColors.current.textSecondary

    val isDark: Boolean
        @Composable
        get() = LocalEchoColors.current.isDark
}

fun getPaletteColors(paletteName: String): Pair<Color, Color> {
    return when (paletteName.trim().lowercase(Locale.ROOT)) {
        "serene lavender" -> Pair(Color(0xFF8B5CF6), Color(0xFFF5F3FF))
        "emerald forest" -> Pair(Color(0xFF10B981), Color(0xFFECFDF5))
        "sunset amber" -> Pair(Color(0xFFF97316), Color(0xFFFFF7ED))
        "rose blossom" -> Pair(Color(0xFFEC4899), Color(0xFFFDF2F8))
        "midnight slate" -> Pair(Color(0xFF475569), Color(0xFFF1F5F9))
        else -> Pair(Color(0xFF5165F6), Color(0xFFEEF2FF)) // Calm Blue (Default)
    }
}

@Composable
fun EchoDiaryTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    paletteName: String = DiaryActivityStore.themePalette.value,
    content: @Composable () -> Unit
) {
    val (primary, container) = getPaletteColors(paletteName)

    val background = if (darkTheme) Color(0xFF0F172A) else Color(0xFFFAFBFC)
    val surface = if (darkTheme) Color(0xFF1E293B) else Color.White
    val onSurface = if (darkTheme) Color(0xFFF8FAFC) else Color(0xFF1E293B)
    val textSecondary = if (darkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)

    val echoColors = EchoDiaryColors(
        primary = primary,
        primaryContainer = container,
        background = background,
        surface = surface,
        onSurface = onSurface,
        textSecondary = textSecondary,
        isDark = darkTheme
    )

    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = primary,
            primaryContainer = container,
            background = background,
            surface = surface,
            onPrimary = Color.White,
            onBackground = onSurface,
            onSurface = onSurface
        )
    } else {
        lightColorScheme(
            primary = primary,
            primaryContainer = container,
            background = background,
            surface = surface,
            onPrimary = Color.White,
            onBackground = onSurface,
            onSurface = onSurface
        )
    }

    CompositionLocalProvider(LocalEchoColors provides echoColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
