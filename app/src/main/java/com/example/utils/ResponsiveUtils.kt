package com.example.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Screen Device Type based on Material 3 Window Width Size Classes
 */
enum class DeviceType {
    COMPACT,   // Phone portrait (<600dp)
    MEDIUM,    // Large Phone / Foldable / Small Tablet (600dp - 840dp)
    EXPANDED   // Large Tablet / Desktop / ChromeOS (>=840dp)
}

/**
 * Screen Orientation
 */
enum class ScreenOrientation {
    PORTRAIT,
    LANDSCAPE
}

/**
 * Holds calculated screen information and scaling utilities for responsive layouts.
 */
data class ResponsiveDimensions(
    val screenWidthDp: Int,
    val screenHeightDp: Int,
    val deviceType: DeviceType,
    val orientation: ScreenOrientation
) {
    val isCompact: Boolean get() = deviceType == DeviceType.COMPACT
    val isMedium: Boolean get() = deviceType == DeviceType.MEDIUM
    val isExpanded: Boolean get() = deviceType == DeviceType.EXPANDED
    val isLandscape: Boolean get() = orientation == ScreenOrientation.LANDSCAPE

    // Dynamic grid columns recommendation
    val gridColumns: Int
        get() = when {
            isExpanded -> 3
            isMedium || (isCompact && isLandscape) -> 2
            else -> 1
        }

    // Adaptive padding based on device class
    val outerPadding: Dp
        get() = when (deviceType) {
            DeviceType.COMPACT -> 16.dp
            DeviceType.MEDIUM -> 24.dp
            DeviceType.EXPANDED -> 32.dp
        }

    // Adaptive card corner radius
    val cardCornerRadius: Dp
        get() = when (deviceType) {
            DeviceType.COMPACT -> 16.dp
            DeviceType.MEDIUM -> 20.dp
            DeviceType.EXPANDED -> 24.dp
        }

    /**
     * Scale a dimension linearly based on standard 375dp reference width.
     */
    fun scale(value: Float): Dp {
        val factor = (screenWidthDp / 375f).coerceIn(0.85f, 1.8f)
        return (value * factor).dp
    }

    /**
     * Moderate scale dampens scaling so small screens don't get too squeezed
     * and large screens don't become excessively huge.
     */
    fun moderateScale(value: Float, factor: Float = 0.5f): Dp {
        val scaleFactor = (screenWidthDp / 375f).coerceIn(0.85f, 1.8f)
        val scaled = value + (value * scaleFactor - value) * factor
        return scaled.dp
    }

    /**
     * Responsive SP font scaling with floor and ceiling safeguards.
     */
    fun responsiveSp(baseSp: Float): TextUnit {
        val multiplier = when (deviceType) {
            DeviceType.COMPACT -> if (screenWidthDp < 360) 0.9f else 1.0f
            DeviceType.MEDIUM -> 1.12f
            DeviceType.EXPANDED -> 1.25f
        }
        return (baseSp * multiplier).sp
    }
}

val LocalResponsiveDimensions = staticCompositionLocalOf {
    ResponsiveDimensions(
        screenWidthDp = 375,
        screenHeightDp = 812,
        deviceType = DeviceType.COMPACT,
        orientation = ScreenOrientation.PORTRAIT
    )
}

/**
 * Provider wrapper to inject responsive dimension calculations into Composition.
 */
@Composable
fun ProvideResponsiveDimensions(content: @Composable () -> Unit) {
    val configuration = LocalConfiguration.current
    val widthDp = configuration.screenWidthDp
    val heightDp = configuration.screenHeightDp

    val deviceType = when {
        widthDp < 600 -> DeviceType.COMPACT
        widthDp < 840 -> DeviceType.MEDIUM
        else -> DeviceType.EXPANDED
    }

    val orientation = if (widthDp > heightDp) {
        ScreenOrientation.LANDSCAPE
    } else {
        ScreenOrientation.PORTRAIT
    }

    val dimensions = ResponsiveDimensions(
        screenWidthDp = widthDp,
        screenHeightDp = heightDp,
        deviceType = deviceType,
        orientation = orientation
    )

    CompositionLocalProvider(LocalResponsiveDimensions provides dimensions) {
        content()
    }
}

/**
 * Convenience accessor for ResponsiveDimensions
 */
object Responsive {
    val dimensions: ResponsiveDimensions
        @Composable
        @ReadOnlyComposable
        get() = LocalResponsiveDimensions.current
}
