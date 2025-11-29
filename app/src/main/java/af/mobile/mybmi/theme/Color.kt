package af.mobile.mybmi.theme

import androidx.compose.ui.graphics.Color

// ==============================================
// BRAND COLORS (LIGHT THEME) 🌿
// ==============================================
val GreenPrimary = Color(0xFF10B981)
val GreenLight = Color(0xFF34D399)
val GreenBackground = Color(0xFFF0FDF4)
val GreenSurface = Color(0xFFFFFFFF)
val GreenAccent = Color(0xFF059669)

val MintLight = Color(0xFFD1FAE5)
val MintMedium = Color(0xFFA7F3D0)

val TextPrimary = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF6B6B6B)
val TextDisabled = Color(0xFFBDBDBD)

val ColorBlue = Color(0xFF3B82F6)
val ColorGreen = Color(0xFF10B981)
val ColorOrange = Color(0xFFF59E0B)
val ColorRed = Color(0xFFEF4444)

val ColorBlueLovely = Color(0xFFEFF6FF)
val ColorGreenLovely = Color(0xFFF0FDF4)
val ColorOrangeLovely = Color(0xFFFEF3C7)
val ColorRedLovely = Color(0xFFFEF2F2)

val Gray50 = Color(0xFFFAFAFA)
val Gray100 = Color(0xFFF5F5F5)
val Gray200 = Color(0xFFEEEEEE)
val Gray300 = Color(0xFFE0E0E0)
val Gray400 = Color(0xFFBDBDBD)

val LightCardBackground = Gray50
val LightNavBarBackground = GreenPrimary

// ==============================================
// DARK MODE FIXED COLORS 🔧
// ==============================================

val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF3E3E3E)
val DarkSurfaceVariant = Color(0xFF4A4A4A)
val DarkSurfaceHighVariant = Color(0xFF555555)

val DarkNavBarBackground = Color(0xFF303030)
val DarkNavBarSurface = Color(0xFF353535)

val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFFF0F0F0)
val TextDisabledDark = Color(0xFFAAAAAA)
val DarkDivider = Color(0xFF555555)

val DarkBlueLovely = Color(0xFF2B5C85)
val DarkGreenLovely = Color(0xFF267359)
val DarkOrangeLovely = Color(0xFF8F5A2E)
val DarkRedLovely = Color(0xFF8F2E2E)

val DarkCardSurface = DarkSurface

// ==============================================
// COMPONENT SPECIFIC COLORS 🧩
// ==============================================

// --- Input Fields Placeholder ---
// FIX: Light Mode pakai Abu-abu Solid agar JELAS
val LightInputPlaceholder = Color.Gray

// FIX: Dark Mode pakai Putih
val DarkInputPlaceholder = Color.White

// --- Buttons (Disabled State) ---
val LightButtonDisabledContainer = GreenPrimary.copy(alpha = 0.5f)
val LightButtonDisabledContent = Color.White

val DarkButtonDisabledContainer = GreenPrimary.copy(alpha = 0.2f)
val DarkButtonDisabledContent = Color.White.copy(alpha = 0.7f)


// ==============================================
// NAVIGATION BAR COLORS 🧭
// ==============================================
val LightNavBarIndicator = Color.White
val LightNavBarSelectedIcon = GreenPrimary
val LightNavBarSelectedText = Color.White
val LightNavBarUnselectedContent = Color.White.copy(alpha = 0.7f)

val DarkNavBarIndicator = GreenPrimary.copy(alpha = 0.15f)
val DarkNavBarSelectedIcon = Color.White
val DarkNavBarSelectedText = Color.White
val DarkNavBarUnselectedContent = Color.White.copy(alpha = 0.5f)

// ==============================================
// HELPER FUNCTIONS
// ==============================================

fun getBackgroundColor(isDarkMode: Boolean): Color = if (isDarkMode) DarkBackground else Color.White
fun getDividerColor(isDarkMode: Boolean): Color = if (isDarkMode) DarkDivider else Gray200

// Helper untuk Komponen
fun getInputPlaceholderColor(isDarkMode: Boolean): Color =
    if (isDarkMode) DarkInputPlaceholder else LightInputPlaceholder

fun getButtonDisabledContainerColor(isDarkMode: Boolean): Color =
    if (isDarkMode) DarkButtonDisabledContainer else LightButtonDisabledContainer

fun getButtonDisabledContentColor(isDarkMode: Boolean): Color =
    if (isDarkMode) DarkButtonDisabledContent else LightButtonDisabledContent

fun getNavBarContainerColor(isDarkMode: Boolean): Color =
    if (isDarkMode) DarkNavBarBackground else LightNavBarBackground

fun getNavBarIndicatorColor(isDarkMode: Boolean): Color =
    if (isDarkMode) DarkNavBarIndicator else LightNavBarIndicator

fun getNavBarSelectedIconColor(isDarkMode: Boolean): Color =
    if (isDarkMode) DarkNavBarSelectedIcon else LightNavBarSelectedIcon

fun getNavBarSelectedTextColor(isDarkMode: Boolean): Color =
    if (isDarkMode) DarkNavBarSelectedText else LightNavBarSelectedText

fun getNavBarUnselectedContentColor(isDarkMode: Boolean): Color =
    if (isDarkMode) DarkNavBarUnselectedContent else LightNavBarUnselectedContent