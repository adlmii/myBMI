package af.mobile.mybmi.theme

import androidx.compose.ui.graphics.Color

// --- BRAND PALETTE (MODERN MINT & SLATE) ---
val BrandPrimary = Color(0xFF00C9A7) // Mint Segar
val BrandSecondary = Color(0xFF008F7A) // Teal Gelap
val BrandTertiary = Color(0xFF4D8076)
val BrandAccent = Color(0xFFC4FCEF)   // Mint Sangat Muda

// --- GRADIENTS COLORS ---
val GradientStart = Color(0xFF00C9A7)
val GradientEnd = Color(0xFF008F7A)

// --- NEUTRALS (LIGHT) ---
val LightBackground = Color(0xFFF8F9FA) // Bukan putih murni, tapi abu sangat muda (Clean look)
val LightSurface = Color(0xFFFFFFFF)
val LightTextPrimary = Color(0xFF1E293B) // Slate 800 (Lebih lembut dari hitam pekat)
val LightTextSecondary = Color(0xFF64748B) // Slate 500

// --- NEUTRALS (DARK) ---
val DarkBackground = Color(0xFF0F172A) // Slate 900 (Deep Blue-Black)
val DarkSurface = Color(0xFF1E293B)    // Slate 800
val DarkTextPrimary = Color(0xFFF1F5F9) // Slate 100
val DarkTextSecondary = Color(0xFF94A3B8) // Slate 400

// --- STATUS COLORS (VIBRANT) ---
val StatusUnderweight = Color(0xFF3B82F6) // Blue 500
val StatusNormal = Color(0xFF10B981)      // Emerald 500
val StatusOverweight = Color(0xFFF59E0B)  // Amber 500
val StatusObese = Color(0xFFEF4444)       // Red 500

// --- COMPONENT HELPERS ---
val LightInputFill = Color(0xFFF1F5F9)    // Slate 100
val DarkInputFill = Color(0xFF334155)     // Slate 700

// Navigation Bar
fun getNavBarContainerColor(isDarkMode: Boolean) = if (isDarkMode) DarkSurface else LightSurface
fun getNavBarSelectedIconColor(isDarkMode: Boolean) = if (isDarkMode) BrandPrimary else BrandPrimary
fun getNavBarUnselectedColor(isDarkMode: Boolean) = if (isDarkMode) DarkTextSecondary else LightTextSecondary

// --- ACTION BUTTON HELPERS (BARU) ---
fun getActionButtonContainerColor(isDarkMode: Boolean, isEnabled: Boolean): Color {
    return if (isEnabled) {
        if (isDarkMode) BrandSecondary else BrandPrimary
    } else {
        if (isDarkMode) BrandPrimary.copy(alpha = 0.2f) else BrandPrimary.copy(alpha = 0.5f)
    }
}
fun getActionButtonContentColor(isEnabled: Boolean): Color {
    return if (isEnabled) Color.White else Color.White.copy(alpha = 0.7f)
}