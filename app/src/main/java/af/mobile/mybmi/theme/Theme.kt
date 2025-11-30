package af.mobile.mybmi.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

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

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}