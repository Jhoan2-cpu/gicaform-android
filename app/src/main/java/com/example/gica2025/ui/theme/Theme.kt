package com.example.gica2025.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryOrangeDarkTheme,
    secondary = PrimaryBlueDarkTheme,
    tertiary = PrimaryOrangeVariantDarkTheme,
    background = BackgroundDark,
    surface = SurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    surfaceContainer = SurfaceContainerDark,
    surfaceContainerHigh = SurfaceContainerHighDark,
    onPrimary = Color(0xFF000000),
    onSecondary = Color(0xFF000000),
    onTertiary = Color(0xFF000000),
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    onSurfaceVariant = TextSecondaryDark,
    primaryContainer = PrimaryContainerDark,
    secondaryContainer = SecondaryContainerDark,
    tertiaryContainer = TertiaryContainerDark,
    errorContainer = ErrorContainerDark,
    onPrimaryContainer = PrimaryOrangeDarkTheme,
    onSecondaryContainer = PrimaryBlueDarkTheme,
    onTertiaryContainer = PrimaryOrangeVariantDarkTheme,
    onErrorContainer = ErrorDark,
    error = ErrorDark,
    onError = Color(0xFF000000),
    outline = DividerDark,
    outlineVariant = Color(0xFF444444)
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryOrange,
    secondary = PrimaryBlue,
    tertiary = PrimaryOrangeVariant,
    background = BackgroundLight,
    surface = SurfaceLight,
    surfaceVariant = Color(0xFFF5F5F5),
    onPrimary = Color(0xFFFFFFFF),
    onSecondary = Color(0xFFFFFFFF),
    onTertiary = Color(0xFFFFFFFF),
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    onSurfaceVariant = TextSecondaryLight,
    primaryContainer = Color(0xFFFFE0B2),
    secondaryContainer = Color(0xFFE1F5FE),
    tertiaryContainer = Color(0xFFFFF3E0),
    errorContainer = ErrorLight,
    onPrimaryContainer = PrimaryOrangeDark,
    onSecondaryContainer = PrimaryBlueDark,
    onTertiaryContainer = Color(0xFFE65100),
    onErrorContainer = Error,
    error = Error,
    onError = Color(0xFFFFFFFF),
    outline = DividerLight,
    outlineVariant = Color(0xFFE0E0E0)
)

@Composable
fun GicaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Deshabilitado para mantener consistencia
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.surface.toArgb()
            window.navigationBarColor = colorScheme.surface.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}