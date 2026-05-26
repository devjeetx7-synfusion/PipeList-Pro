package com.synfusion.pipelistpro.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = PrimaryBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEBF2FF),
    onPrimaryContainer = PrimaryBlue,
    secondary = SecondaryPurple,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFF5F3FF),
    onSecondaryContainer = SecondaryPurple,
    background = BackgroundLight,
    onBackground = NavyHeading,
    surface = CardWhite,
    onSurface = NavyHeading,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = SlateGraySubtitle,
    outline = DividerColor,
    outlineVariant = Color(0xFFF1F5F9),
    error = ErrorRed,
    onError = Color.White
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF60A5FA),
    onPrimary = Color(0xFF003366),
    primaryContainer = Color(0xFF1E293B),
    onPrimaryContainer = Color(0xFFD1E9FF),
    secondary = Color(0xFFA78BFA),
    onSecondary = Color(0xFF2E1065),
    secondaryContainer = Color(0xFF2D1B69),
    onSecondaryContainer = Color(0xFFEDE9FE),
    background = BackgroundDark,
    onBackground = TextWhite,
    surface = CardDark,
    onSurface = TextWhite,
    surfaceVariant = Color(0xFF334155),
    onSurfaceVariant = TextGray,
    outline = Color(0xFF475569),
    outlineVariant = Color(0xFF1E293B),
    error = Color(0xFFF87171),
    onError = Color(0xFF450A0A)
)

val PipeListShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(22.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(30.dp)
)

@Composable
fun PipeListProTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = Color.Transparent.toArgb()
            window.navigationBarColor = Color.Transparent.toArgb()

            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = PipeListShapes,
        content = content
    )
}
