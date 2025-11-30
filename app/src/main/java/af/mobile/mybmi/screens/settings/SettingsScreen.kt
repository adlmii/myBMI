package af.mobile.mybmi.screens.settings

import af.mobile.mybmi.components.ModernDialogContainer
import af.mobile.mybmi.theme.BrandPrimary
import af.mobile.mybmi.viewmodel.ThemeViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    themeViewModel: ThemeViewModel = viewModel()
) {
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()

    var showPrivacyDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Pengaturan") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        // --- UPDATE ICON INI ---
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(24.dp)
        ) {
            // APPEARANCE SECTION
            Text(
                text = "Tampilan",
                style = MaterialTheme.typography.labelLarge,
                color = BrandPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.DarkMode,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            "Mode Gelap",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface // Aman di Dark Mode
                        )
                        Text(
                            "Sesuaikan dengan kenyamanan mata",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = isDarkMode,
                        onCheckedChange = { themeViewModel.toggleDarkMode() },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BrandPrimary,
                            checkedTrackColor = BrandPrimary.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ABOUT SECTION
            Text(
                text = "Tentang Aplikasi",
                style = MaterialTheme.typography.labelLarge,
                color = BrandPrimary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                // Versi Aplikasi
                ListItem(
                    headlineContent = {
                        Text("Versi Aplikasi", color = MaterialTheme.colorScheme.onSurface)
                    },
                    trailingContent = {
                        Text("1.0.0", color = BrandPrimary, fontWeight = FontWeight.Bold)
                    },
                    leadingContent = {
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                // Kebijakan Privasi
                ListItem(
                    modifier = Modifier.clickable { showPrivacyDialog = true },
                    headlineContent = {
                        Text("Kebijakan Privasi", color = MaterialTheme.colorScheme.onSurface)
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    colors = ListItemDefaults.colors(containerColor = androidx.compose.ui.graphics.Color.Transparent)
                )
            }
        }
    }

    // Dialog Popup
    if (showPrivacyDialog) {
        PrivacyPolicyDialog(onDismiss = { showPrivacyDialog = false })
    }
}

@Composable
fun PrivacyPolicyDialog(onDismiss: () -> Unit) {
    ModernDialogContainer(onDismiss = onDismiss) {
        // 1. Header Icon & Title
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(BrandPrimary.copy(alpha = 0.1f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.Lock,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Kebijakan Privasi",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 2. Scrollable Content
        // Kita batasi tinggi maksimal agar dialog tidak memenuhi layar penuh jika teks panjang
        Column(
            modifier = Modifier
                .weight(1f, fill = false) // Penting: agar dialog menyesuaikan isi tapi bisa scroll
                .verticalScroll(rememberScrollState())
        ) {
            PrivacySection(
                title = "1. Penyimpanan Lokal (Offline)",
                content = "Aplikasi myBMI bekerja sepenuhnya secara offline. Seluruh data kesehatan dan profil Anda tersimpan aman di memori internal perangkat ini dan tidak pernah dikirim ke server manapun."
            )
            PrivacySection(
                title = "2. Penggunaan Data",
                content = "Data tinggi badan, berat badan, dan foto profil yang Anda masukkan semata-mata hanya digunakan untuk kalkulasi BMI dan personalisasi tampilan aplikasi."
            )
            PrivacySection(
                title = "3. Izin Akses",
                content = "Kami menghargai privasi Anda. Aplikasi ini hanya meminta akses Galeri (Storage) saat Anda ingin mengubah foto profil. Tidak ada akses lokasi, kontak, atau kamera yang diambil diam-diam."
            )
            PrivacySection(
                title = "4. Keamanan",
                content = "Karena data tersimpan lokal, keamanan data Anda bergantung pada keamanan fisik perangkat (Pola/PIN/Sidik Jari) HP Anda sendiri."
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Footer Button
        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPrimary,
                contentColor = Color.White
            )
        ) {
            Text("Saya Mengerti", style = MaterialTheme.typography.titleMedium)
        }
    }
}

// Helper PrivacySection biarkan tetap sama seperti sebelumnya
@Composable
fun PrivacySection(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
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