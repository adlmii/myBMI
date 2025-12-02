package af.mobile.mybmi.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class Badge(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val requirement: String
) {
    FIRST_STEP(
        id = "first_step",
        title = "Langkah Awal",
        description = "Melakukan pengecekan BMI untuk pertama kalinya.",
        icon = Icons.Rounded.DirectionsRun,
        requirement = "Cek BMI 1x"
    ),

    STREAK_1(
        id = "streak_1",
        title = "Mulai Rutin",
        description = "Berhasil menjaga streak kesehatan selama 1 bulan.",
        icon = Icons.Rounded.LocalFireDepartment,
        requirement = "Streak 1 Bulan"
    ),
    STREAK_3(
        id = "streak_3",
        title = "Makin Konsisten",
        description = "Luar biasa! Konsisten cek kesehatan selama 3 bulan.",
        icon = Icons.Rounded.TrendingUp,
        requirement = "Streak 3 Bulan"
    ),
    STREAK_6(
        id = "streak_6",
        title = "Separuh Tahun",
        description = "Setengah tahun perjalanan kesehatan tanpa putus.",
        icon = Icons.Rounded.WorkspacePremium,
        requirement = "Streak 6 Bulan"
    ),
    STREAK_12(
        id = "streak_12",
        title = "Master Kesehatan",
        description = "Satu tahun penuh dedikasi untuk tubuhmu.",
        icon = Icons.Rounded.EmojiEvents, // Ikon Piala
        requirement = "Streak 1 Tahun"
    ),

    CONSISTENCY_3(
        id = "consistency_3",
        title = "Si Rajin",
        description = "Melakukan pengecekan sebanyak 3 kali (total).",
        icon = Icons.Rounded.Repeat,
        requirement = "Total 3x Cek"
    ),
    IDEAL_GOAL(
        id = "ideal_goal",
        title = "Tubuh Ideal",
        description = "Mencapai kategori Berat Badan Normal.",
        icon = Icons.Rounded.Star,
        requirement = "Hasil BMI Normal"
    ),
    PROFILE_MASTER(
        id = "profile_master",
        title = "Profil Lengkap",
        description = "Melengkapi foto profil dan data diri.",
        icon = Icons.Rounded.AccountCircle,
        requirement = "Set Foto Profil"
    ),
    NIGHT_OWL(
        id = "night_owl",
        title = "Night Owl",
        description = "Mengecek kesehatan di malam hari.",
        icon = Icons.Rounded.DarkMode,
        requirement = "Cek BMI di atas jam 9 malam"
    );

    companion object {
        fun fromId(id: String): Badge? {
            return entries.find { it.id == id }
        }
    }
}