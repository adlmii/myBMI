package af.mobile.mybmi.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = GreenPrimary,
    onPrimary = Color.White,
    secondary = GreenLight,
    onSecondary = TextPrimary,
    tertiary = GreenAccent,
    background = Color.White, // Pure white background
    onBackground = TextPrimary,
    surface = GreenSurface,
    onSurface = TextPrimary,
    surfaceVariant = MintLight,
    onSurfaceVariant = TextSecondary,
    primaryContainer = MintMedium,
    onPrimaryContainer = TextPrimary
)

private val DarkColorScheme = darkColorScheme(
    primary = GreenPrimary,
    onPrimary = TextPrimaryDark,
    secondary = GreenLight,
    onSecondary = TextPrimaryDark,
    tertiary = GreenAccent,
    background = DarkBackground,
    onBackground = TextPrimaryDark,
    surface = DarkSurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    primaryContainer = DarkGreenLovely,
    onPrimaryContainer = TextPrimaryDark,
    outlineVariant = DarkDivider,
    scrim = Color.Black.copy(alpha = 0.5f),
    inverseSurface = TextPrimaryDark,
    inverseOnSurface = DarkBackground
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


