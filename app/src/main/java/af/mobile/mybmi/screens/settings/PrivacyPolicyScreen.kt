package af.mobile.mybmi.screens.settings

import af.mobile.mybmi.components.StandardScreenLayout
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit
) {
    StandardScreenLayout(
        title = "Kebijakan Privasi",
        onBack = onNavigateBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Intro kecil (Opsional)
            Text(
                text = "Keamanan dan privasi data Anda adalah prioritas utama kami.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            PrivacySectionItem(
                title = "1. Privasi Absolut",
                content = "Kami menjunjung tinggi privasi Anda. myBMI didesain sebagai aplikasi 'Offline-First', artinya tidak ada data yang keluar dari HP Anda."
            )

            PrivacySectionItem(
                title = "2. Penyimpanan Data",
                content = "Seluruh data kesehatan (Berat, Tinggi, BMI) dan foto profil disimpan secara LOKAL (Local Database) di dalam memori perangkat Anda. Kami tidak memiliki server cloud."
            )

            PrivacySectionItem(
                title = "3. Penggunaan Izin",
                content = "• Kamera/Galeri: Hanya digunakan saat Anda ingin mengganti foto profil.\n• Notifikasi: Hanya digunakan untuk menjadwalkan pengingat cek BMI bulanan secara lokal."
            )

            PrivacySectionItem(
                title = "4. Tanpa Pelacakan",
                content = "Aplikasi ini tidak menggunakan cookie, pelacak iklan, atau analitik pihak ketiga yang mengambil data pribadi Anda."
            )

            PrivacySectionItem(
                title = "5. Penghapusan Data",
                content = "Anda memegang kendali penuh. Anda dapat menghapus riwayat per item melalui menu Riwayat, atau menghapus seluruh data dengan melakukan 'Hapus Data Aplikasi' di pengaturan Android."
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun PrivacySectionItem(title: String, content: String) {
    Card(
        colors = CardDefaults.cardColors(
            // Warna disesuaikan agar kontras di Light/Dark Mode
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 20.sp
            )
        }
    }
}