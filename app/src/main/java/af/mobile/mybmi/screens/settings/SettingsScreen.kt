package af.mobile.mybmi.screens.settings

import af.mobile.mybmi.components.ModernDialogContainer
import af.mobile.mybmi.components.StandardScreenLayout
import af.mobile.mybmi.theme.BrandPrimary
import af.mobile.mybmi.viewmodel.ReminderViewModel
import af.mobile.mybmi.viewmodel.ThemeViewModel
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Help
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPrivacy: () -> Unit,
    onNavigateToTerms: () -> Unit,
    onNavigateToGuide: () -> Unit,
    themeViewModel: ThemeViewModel = viewModel(),
    reminderViewModel: ReminderViewModel = viewModel()
) {
    // State dari ViewModel
    val isDarkMode by themeViewModel.isDarkMode.collectAsState()
    val isReminderEnabled by reminderViewModel.isReminderEnabled.collectAsState()
    val reminderDay by reminderViewModel.reminderDay.collectAsState()

    // State untuk Dialog
    var showDayPickerDialog by remember { mutableStateOf(false) }

    // Launcher Izin Notifikasi (Android 13+)
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) reminderViewModel.toggleReminder(true)
        }
    )

    // --- MAIN UI ---
    StandardScreenLayout(
        title = "Pengaturan",
        onBack = onNavigateBack
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            // 1. SEKSI TAMPILAN
            SettingsSectionTitle("Tampilan")

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.DarkMode, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Mode Gelap", style = MaterialTheme.typography.titleMedium)
                        Text("Sesuaikan kenyamanan mata", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

            Spacer(modifier = Modifier.height(24.dp))

            // 2. SEKSI PENGINGAT
            SettingsSectionTitle("Pengingat Rutin")

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                // Switch On/Off
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Notifications, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Ingatkan Saya", style = MaterialTheme.typography.titleMedium)
                        Text("Notifikasi cek BMI 1x sebulan", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Switch(
                        checked = isReminderEnabled,
                        onCheckedChange = { isChecked ->
                            if (isChecked) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                } else {
                                    reminderViewModel.toggleReminder(true)
                                }
                            } else {
                                reminderViewModel.toggleReminder(false)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = BrandPrimary,
                            checkedTrackColor = BrandPrimary.copy(alpha = 0.3f)
                        )
                    )
                }

                // Baris Jadwal & Tombol Tes (Hanya muncul jika aktif)
                if (isReminderEnabled) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

                    ListItem(
                        modifier = Modifier.clickable { showDayPickerDialog = true },
                        headlineContent = { Text("Jadwal Pengingat", style = MaterialTheme.typography.titleSmall) },
                        supportingContent = {
                            Text("Setiap tanggal $reminderDay, jam 09:00 pagi", color = BrandPrimary, fontWeight = FontWeight.Medium)
                        },
                        leadingContent = {
                            Icon(Icons.Rounded.CalendarMonth, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )

                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

                    Box(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = { reminderViewModel.testNotificationInstant() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Rounded.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Tes Notifikasi Sekarang")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. SEKSI TENTANG APLIKASI
            SettingsSectionTitle("Bantuan & Info")

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                // Item: Panduan
                ListItem(
                    modifier = Modifier.clickable { onNavigateToGuide() },
                    headlineContent = { Text("Panduan Penggunaan") },
                    // Use AutoMirrored version here
                    leadingContent = { Icon(Icons.AutoMirrored.Rounded.Help, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    trailingContent = { Icon(Icons.Rounded.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

                // Item: Syarat & Ketentuan
                ListItem(
                    modifier = Modifier.clickable { onNavigateToTerms() },
                    headlineContent = { Text("Syarat & Ketentuan") },
                    leadingContent = { Icon(Icons.Rounded.Description, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    trailingContent = { Icon(Icons.Rounded.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

                // Item: Kebijakan Privasi
                ListItem(
                    modifier = Modifier.clickable { onNavigateToPrivacy() },
                    headlineContent = { Text("Kebijakan Privasi") },
                    leadingContent = { Icon(Icons.Rounded.Security, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    trailingContent = { Icon(Icons.Rounded.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )

                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))

                // Item: Versi Aplikasi
                ListItem(
                    headlineContent = { Text("Versi Aplikasi") },
                    trailingContent = { Text("1.0.0", fontWeight = FontWeight.Bold, color = BrandPrimary) },
                    leadingContent = { Icon(Icons.Rounded.Info, null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                    colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // --- DIALOGS ---

    // Dialog Pilih Tanggal
    if (showDayPickerDialog) {
        ModernDialogContainer(onDismiss = { showDayPickerDialog = false }) {
            Text(
                text = "Pilih Tanggal",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pilih tanggal berapa setiap bulannya Anda ingin diingatkan:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                maxItemsInEachRow = 7
            ) {
                (1..28).forEach { day ->
                    val isSelected = reminderDay == day
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isSelected) BrandPrimary else Color.Transparent)
                            .border(1.dp, if (isSelected) Color.Transparent else MaterialTheme.colorScheme.outline.copy(alpha=0.3f), CircleShape)
                            .clickable {
                                reminderViewModel.updateReminderDay(day)
                                showDayPickerDialog = false
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$day",
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = { showDayPickerDialog = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Batal", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// Helper untuk Judul Seksi
@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = BrandPrimary,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
    )
}