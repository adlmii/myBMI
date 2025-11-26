package af.mobile.mybmi.theme

import androidx.compose.ui.graphics.Color

// Main brand colors (green theme) 🌿
val GreenPrimary = Color(0xFF10B981) // Emerald green
val GreenLight = Color(0xFF34D399) // Light green
val GreenBackground = Color(0xFFF0FDF4) // Very light green background
val GreenSurface = Color(0xFFFFFFFF) // White
val GreenAccent = Color(0xFF059669) // Darker green for accents

// Mint variations for softer touch
val MintLight = Color(0xFFD1FAE5) // Mint light
val MintMedium = Color(0xFFA7F3D0) // Mint medium

// Text colors
val TextPrimary = Color(0xFF1A1A1A)
val TextSecondary = Color(0xFF6B6B6B)
val TextDisabled = Color(0xFFBDBDBD)

// Status colors (from BMI categories)
val ColorBlue = Color(0xFF3B82F6)
val ColorGreen = Color(0xFF10B981)
val ColorOrange = Color(0xFFF59E0B)
val ColorRed = Color(0xFFEF4444)

// Light variants for backgrounds
val ColorBlueLovely = Color(0xFFEFF6FF) // Very light blue background
val ColorGreenLovely = Color(0xFFF0FDF4) // Very light green background
val ColorOrangeLovely = Color(0xFFFEF3C7) // Very light orange background
val ColorRedLovely = Color(0xFFFEF2F2) // Very light red background

// Neutral colors
val Gray50 = Color(0xFFFAFAFA)
val Gray100 = Color(0xFFF5F5F5)
val Gray200 = Color(0xFFEEEEEE)
val Gray300 = Color(0xFFE0E0E0)
val Gray400 = Color(0xFFBDBDBD)

// Light mode card colors
val LightCardBackground = Gray50
val LightNavBarBackground = Color.White

// Dark Mode Colors - Adjusted for better visibility
val DarkBackground = Color(0xFF121212) // Sedikit lebih terang dari 0F0F0F (Standar Material)
val DarkSurface = Color(0xFF252525) // Surface dasar lebih terang
val DarkSurfaceVariant = Color(0xFF353535) // Variant untuk elevasi
val DarkSurfaceHighVariant = Color(0xFF454545) // High elevation

// Text Colors - Brighter for better readability
val TextPrimaryDark = Color(0xFFFFFFFF) // Pure White (Paling terang)
val TextSecondaryDark = Color(0xFFE0E0E0) // Light Gray (Jauh lebih terang dari sebelumnya)
val TextDisabledDark = Color(0xFF9E9E9E) // Disabled text lebih terlihat
val DarkDivider = Color(0xFF484848) // Divider sedikit lebih tegas

// Dark mode category backgrounds (Saturasi tetap, sedikit diterangkan brightness-nya)
val DarkBlueLovely = Color(0xFF204563)
val DarkGreenLovely = Color(0xFF1F5C48)
val DarkOrangeLovely = Color(0xFF704524)
val DarkRedLovely = Color(0xFF702424)

// Dark mode card surfaces (Brighter Cards)
val DarkCardSurface = Color(0xFF2D2D2D) // Kartu standar sekarang abu-abu solid, bukan hampir hitam
val DarkCardSurfaceHigh = Color(0xFF383838) // Kartu yang lebih tinggi elevasinya
val DarkNavBarBackground = Color(0xFF1E1E1E) // Navbar
val DarkNavBarSurface = Color(0xFF252525) // Navbar surface

// Helper function to get background color based on theme
fun getBackgroundColor(isDarkMode: Boolean): Color {
    return if (isDarkMode) DarkBackground else Color.White
}

// Helper function to get card color based on theme
fun getCardColor(isDarkMode: Boolean): Color {
    return if (isDarkMode) DarkCardSurface else LightCardBackground
}

// Helper function to get navbar color based on theme
fun getNavBarColor(isDarkMode: Boolean): Color {
    return if (isDarkMode) DarkNavBarBackground else LightNavBarBackground
}

// Helper function to get text primary color based on theme
fun getTextPrimaryColor(isDarkMode: Boolean): Color {
    return if (isDarkMode) TextPrimaryDark else TextPrimary
}

// Helper function to get text secondary color based on theme
fun getTextSecondaryColor(isDarkMode: Boolean): Color {
    return if (isDarkMode) TextSecondaryDark else TextSecondary
}

// Helper function to get divider color based on theme
fun getDividerColor(isDarkMode: Boolean): Color {
    return if (isDarkMode) DarkDivider else Gray200
}

// Helper function to get category background color based on theme
fun getCategoryBackgroundColor(isDarkMode: Boolean, categoryColorLight: Color): Color {
    return if (isDarkMode) {
        when (categoryColorLight) {
            ColorBlueLovely -> DarkBlueLovely
            ColorGreenLovely -> DarkGreenLovely
            ColorOrangeLovely -> DarkOrangeLovely
            ColorRedLovely -> DarkRedLovely
            else -> DarkSurfaceVariant
        }
    } else {
        categoryColorLight
    }
}
