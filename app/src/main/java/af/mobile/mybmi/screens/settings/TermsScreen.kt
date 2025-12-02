package af.mobile.mybmi.screens.settings

import af.mobile.mybmi.components.StandardScreenLayout
import af.mobile.mybmi.theme.BrandPrimary
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
fun TermsScreen(
    onNavigateBack: () -> Unit
) {
    StandardScreenLayout(
        title = "Syarat & Ketentuan",
        onBack = onNavigateBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Badge Tanggal Update
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = "Terakhir diperbarui: 02 Des 2025",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // Intro Text
            Text(
                text = "Mohon baca Syarat dan Ketentuan ini dengan saksama sebelum menggunakan aplikasi myBMI. Penggunaan aplikasi menandakan persetujuan Anda terhadap poin-poin berikut.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Sections
            TermsSection(
                number = "1",
                title = "Lisensi Penggunaan",
                content = "myBMI memberikan Anda lisensi terbatas, non-eksklusif, dan tidak dapat dipindahtangankan untuk menggunakan aplikasi ini semata-mata untuk keperluan pemantauan kesehatan pribadi (non-komersial)."
            )

            TermsSection(
                number = "2",
                title = "Penafian Medis (Disclaimer)",
                content = "Seluruh hasil perhitungan (BMI, Berat Ideal) dan saran kesehatan hanyalah estimasi statistik. Informasi ini BUKAN diagnosis medis dan tidak dapat menggantikan saran, diagnosis, atau perawatan dari dokter profesional."
            )

            TermsSection(
                number = "3",
                title = "Akurasi Input Data",
                content = "Anda bertanggung jawab penuh atas keakuratan data (Berat, Tinggi, Umur) yang dimasukkan. Kami tidak bertanggung jawab atas ketidaksesuaian hasil analisis yang disebabkan oleh kesalahan input pengguna."
            )

            TermsSection(
                number = "4",
                title = "Hak Kekayaan Intelektual",
                content = "Seluruh komponen aplikasi termasuk namun tidak terbatas pada logo, desain antarmuka, kode sumber, dan aset visual adalah hak milik eksklusif pengembang myBMI. Dilarang menyalin atau mendistribusikan ulang tanpa izin."
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun TermsSection(number: String, title: String, content: String) {
    Row(modifier = Modifier.padding(bottom = 24.dp)) {
        // Kolom Nomor
        Text(
            text = "$number.",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = BrandPrimary,
            modifier = Modifier.width(28.dp)
        )

        // Kolom Konten
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = content,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}