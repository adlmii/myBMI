package af.mobile.mybmi.screens.settings

import af.mobile.mybmi.components.StandardScreenLayout
import af.mobile.mybmi.theme.BrandPrimary
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GuideScreen(
    onNavigateBack: () -> Unit
) {
    StandardScreenLayout(
        title = "Panduan Penggunaan",
        onBack = onNavigateBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GuideCard(1, "Lengkapi Profil", "Masuk ke menu Profil untuk mengatur nama, foto, tanggal lahir, dan jenis kelamin agar perhitungan lebih personal.")
            GuideCard(2, "Cek BMI Rutin", "Di menu Beranda, masukkan tinggi dan berat badan Anda, lalu tekan 'Hitung Sekarang'.")
            GuideCard(3, "Simpan Hasil", "Setelah hasil keluar, tekan tombol 'Simpan' agar data masuk ke riwayat dan grafik perkembangan Anda.")
            GuideCard(4, "Pantau Riwayat", "Buka menu Riwayat untuk melihat tren berat badan Anda dari waktu ke waktu.")
            GuideCard(5, "Atur Pengingat", "Aktifkan notifikasi di Pengaturan agar Anda tidak lupa mengecek kesehatan setiap bulannya.")

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun GuideCard(number: Int, title: String, description: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Nomor Bulat
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(BrandPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$number",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}