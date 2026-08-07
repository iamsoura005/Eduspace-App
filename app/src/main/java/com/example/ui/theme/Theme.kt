package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ImmersivePrimary,
    onPrimary = ImmersiveOnPrimary,
    primaryContainer = ImmersivePrimaryContainer,
    onPrimaryContainer = ImmersiveOnPrimaryContainer,
    secondary = ImmersiveSecondary,
    onSecondary = ImmersiveOnSecondary,
    secondaryContainer = ImmersiveSecondaryContainer,
    tertiary = ImmersiveTertiary,
    onTertiary = ImmersiveOnTertiary,
    background = ImmersiveBackground,
    surface = ImmersiveSurface,
    surfaceVariant = ImmersiveSurfaceVariant,
    onBackground = ImmersiveOnBackground,
    onSurface = ImmersiveOnSurface,
    onSurfaceVariant = ImmersiveOnSurfaceVariant,
    outline = ImmersiveOutline,
    error = RoseError
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF6750A4),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF625B71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8DEF8),
    tertiary = Color(0xFF7D5260),
    background = ImmersiveBackground,
    surface = ImmersiveSurface,
    surfaceVariant = ImmersiveSurfaceVariant,
    onBackground = ImmersiveOnBackground,
    onSurface = ImmersiveOnSurface,
    onSurfaceVariant = ImmersiveOnSurfaceVariant,
    outline = ImmersiveOutline,
    error = RoseError
)

@Composable
fun NoteLoomTheme(
    darkTheme: Boolean = true, // Default to true for Immersive UI experience
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    NoteLoomTheme(darkTheme = darkTheme, dynamicColor = dynamicColor, content = content)
}


