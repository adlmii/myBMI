package af.mobile.mybmi.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

// Definisi Badge sebagai Enum (Data Statis)
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
    CONSISTENCY_3(
        id = "consistency_3",
        title = "Si Rajin",
        description = "Konsisten mengecek kesehatanmu sebanyak 3 kali.",
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

    // Helper untuk mencari Badge berdasarkan ID string dari database
    companion object {
        fun fromId(id: String): Badge? {
            return entries.find { it.id == id }
        }
    }
}