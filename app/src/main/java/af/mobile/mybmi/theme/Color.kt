package af.mobile.mybmi.theme

import androidx.compose.ui.graphics.Color

// ==============================================
// BRAND COLORS (LIGHT THEME) 🌿
// ==============================================
val GreenPrimary = Color(0xFF10B981) // Emerald green
val GreenLight = Color(0xFF34D399) // Light green
val GreenBackground = Color(0xFFF0FDF4) // Very light green background
val GreenSurface = Color(0xFFFFFFFF) // White
val GreenAccent = Color(0xFF059669) // Darker green for accents

// Mint variations
val MintLight = Color(0xFFD1FAE5)
val MintMedium = Color(0xFFA7F3D0)

// Text Colors (Light Mode)
val TextPrimary = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF6B6B6B)
val TextDisabled = Color(0xFFBDBDBD)

// Status Colors
val ColorBlue = Color(0xFF3B82F6)
val ColorGreen = Color(0xFF10B981)
val ColorOrange = Color(0xFFF59E0B)
val ColorRed = Color(0xFFEF4444)

// Light Background Variants
val ColorBlueLovely = Color(0xFFEFF6FF)
val ColorGreenLovely = Color(0xFFF0FDF4)
val ColorOrangeLovely = Color(0xFFFEF3C7)
val ColorRedLovely = Color(0xFFFEF2F2)

// Neutral Colors
val Gray50 = Color(0xFFFAFAFA)
val Gray100 = Color(0xFFF5F5F5)
val Gray200 = Color(0xFFEEEEEE)
val Gray300 = Color(0xFFE0E0E0)
val Gray400 = Color(0xFFBDBDBD)

// Light Mode Specifics
val LightCardBackground = Gray50
// UPDATED: Navbar Light Mode sekarang HIJAU (GreenPrimary)
val LightNavBarBackground = GreenPrimary

// ==============================================
// DARK MODE FIXED COLORS 🔧
// ==============================================

val DarkBackground = Color(0xFF121212)
val DarkSurface = Color(0xFF3E3E3E)
val DarkSurfaceVariant = Color(0xFF4A4A4A)
val DarkSurfaceHighVariant = Color(0xFF555555)

// Navbar Dark Mode (Tetap Gelap Elegan)
val DarkNavBarBackground = Color(0xFF303030)
val DarkNavBarSurface = Color(0xFF353535)

// Text Colors (Dark)
val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFFF0F0F0)
val TextDisabledDark = Color(0xFFAAAAAA)
val DarkDivider = Color(0xFF555555)

// Category Backgrounds (Dark)
val DarkBlueLovely = Color(0xFF2B5C85)
val DarkGreenLovely = Color(0xFF267359)
val DarkOrangeLovely = Color(0xFF8F5A2E)
val DarkRedLovely = Color(0xFF8F2E2E)

val DarkCardSurface = DarkSurface

// ==============================================
// HELPER FUNCTIONS
// ==============================================

fun getBackgroundColor(isDarkMode: Boolean): Color = if (isDarkMode) DarkBackground else Color.White
fun getNavBarColor(isDarkMode: Boolean): Color = if (isDarkMode) DarkNavBarBackground else LightNavBarBackground // Ini akan return GreenPrimary saat Light Mode
fun getDividerColor(isDarkMode: Boolean): Color = if (isDarkMode) DarkDivider else Gray200