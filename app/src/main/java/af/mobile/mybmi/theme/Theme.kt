package af.mobile.mybmi.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = BrandPrimary,
    onPrimary = LightSurface,
    secondary = BrandSecondary,
    onSecondary = LightSurface,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightInputFill,
    onSurfaceVariant = LightTextSecondary,
    outline = LightTextSecondary.copy(alpha = 0.5f)
)

private val DarkColorScheme = darkColorScheme(
    primary = BrandPrimary,
    onPrimary = DarkBackground,
    secondary = BrandSecondary,
    onSecondary = DarkTextPrimary,
    background = DarkBackground,
    onBackground = DarkTextPrimary,
    surface = DarkSurface,
    onSurface = DarkTextPrimary,
    surfaceVariant = DarkInputFill,
    onSurfaceVariant = DarkTextSecondary,
    outline = DarkTextSecondary.copy(alpha = 0.5f)
)

@Composable
fun myBMITheme(
    isDarkMode: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (isDarkMode) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window

            // 1. Atur Warna Background
            window.statusBarColor = if (isDarkMode) {
                DarkBackground.toArgb()
            } else {
                BrandPrimary.toArgb()
            }

            // 2. Atur Warna Ikon/Teks
            val insetsController = WindowCompat.getInsetsController(window, view)

            insetsController.isAppearanceLightStatusBars = !isDarkMode
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}