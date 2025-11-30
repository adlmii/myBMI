package af.mobile.mybmi.screens.home

import af.mobile.mybmi.components.GradientScreenLayout
import af.mobile.mybmi.components.ModernDialogContainer
import af.mobile.mybmi.components.ModernInput
import af.mobile.mybmi.components.PrimaryButton
import af.mobile.mybmi.model.BMICheckSummary
import af.mobile.mybmi.theme.*
import af.mobile.mybmi.viewmodel.InputViewModel
import af.mobile.mybmi.viewmodel.ReminderViewModel
import af.mobile.mybmi.viewmodel.UserViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToResult: (BMICheckSummary) -> Unit,
    onNavigateToSettings: () -> Unit,
    inputViewModel: InputViewModel = viewModel(),
    userViewModel: UserViewModel? = null,
    reminderViewModel: ReminderViewModel = viewModel()
) {
    val input by inputViewModel.input.collectAsState()
    val isCalculating by inputViewModel.isCalculating.collectAsState()
    val currentUser by userViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }
    val showNameInput by userViewModel?.showNameInput?.collectAsState() ?: remember { mutableStateOf(false) }

    // State Pengingat
    val isReminderEnabled by reminderViewModel.isReminderEnabled.collectAsState()
    val reminderDay by reminderViewModel.reminderDay.collectAsState()

    GradientScreenLayout(
        headerContent = { /* Kosong (Dekorasi saja) */ },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // HEADER
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.HealthAndSafety, null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (currentUser != null) "Halo, ${currentUser!!.name}" else "Selamat Datang",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Cek Kesehatanmu",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- KARTU JADWAL PENGINGAT (YANG DI-UPDATE) ---
                // Kartu ini selalu muncul, isinya berubah sesuai status
                ScheduleStatusCard(
                    isEnabled = isReminderEnabled,
                    day = reminderDay,
                    onClick = onNavigateToSettings // Klik lari ke Settings
                )

                Spacer(modifier = Modifier.height(24.dp))

                // MAIN INPUT CARD
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, spotColor = BrandPrimary.copy(alpha = 0.2f), shape = RoundedCornerShape(32.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Kalkulator BMI",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        ModernInput(
                            label = "Tinggi Badan",
                            value = input.height,
                            onValueChange = { inputViewModel.updateHeight(it) },
                            suffix = "cm",
                            placeholderText = "Contoh: 170"
                        )

                        ModernInput(
                            label = "Berat Badan",
                            value = input.weight,
                            onValueChange = { inputViewModel.updateWeight(it) },
                            suffix = "kg",
                            placeholderText = "Contoh: 65"
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        PrimaryButton(
                            text = "Hitung Sekarang",
                            onClick = {
                                inputViewModel.calculateBMI { summary ->
                                    onNavigateToResult(summary) }
                                inputViewModel.clearInput()
                            },
                            enabled = inputViewModel.canCalculate(),
                            isLoading = isCalculating
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    )

    if (showNameInput) {
        NameInputDialog(onConfirm = { name -> userViewModel?.saveUserName(name) })
    }
}

@Composable
fun ScheduleStatusCard(
    isEnabled: Boolean,
    day: Int,
    onClick: () -> Unit
) {
    // 1. Tentukan Warna & Icon berdasarkan Mode (Dark/Light) & Status (On/Off)
    // Menggunakan MaterialTheme.colorScheme.surface agar otomatis gelap di Dark Mode
    val containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    val contentColor = MaterialTheme.colorScheme.onSurface

    // Icon & Text Logic
    val icon = if (isEnabled) Icons.Rounded.CalendarMonth else Icons.Rounded.NotificationsOff
    val iconBgColor = if (isEnabled) BrandPrimary else MaterialTheme.colorScheme.error // Merah jika mati
    val titleText = if (isEnabled) "Jadwal Cek Berikutnya" else "Pengingat Rutin Mati"

    // Logika Tanggal (Hanya dihitung jika aktif)
    val subtitleText = remember(isEnabled, day) {
        if (isEnabled) {
            val today = Calendar.getInstance()
            val targetDate = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, day) }

            if (today.get(Calendar.DAY_OF_MONTH) > day) {
                targetDate.add(Calendar.MONTH, 1)
            }

            val localeID = Locale.forLanguageTag("id-ID")
            val sdf = SimpleDateFormat("dd MMMM yyyy", localeID)
            sdf.format(targetDate.time)
        } else {
            "Ketuk untuk atur jadwal"
        }
    }

    // UI
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon Box
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(iconBgColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconBgColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = titleText,
                style = MaterialTheme.typography.labelMedium,
                color = contentColor.copy(alpha = 0.7f)
            )
            Text(
                text = subtitleText,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Chevron (Indikator bisa diklik)
        if (!isEnabled) {
            Icon(
                imageVector = Icons.Rounded.Notifications,
                contentDescription = "Setup",
                tint = BrandPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun NameInputDialog(onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    // Menggunakan Template ModernDialogContainer
    ModernDialogContainer(onDismiss = {}) { // onDismiss kosong agar user tidak bisa menutup paksa tanpa isi nama
        // 1. Header Icon (Style Seragam)
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(BrandPrimary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = null,
                tint = BrandPrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Title & Description
        Text(
            text = "Selamat Datang!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Siapa nama panggilanmu?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Input Field (Styling disesuaikan)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Contoh: Budi") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                cursorColor = BrandPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Button
        Button(
            onClick = { if (name.isNotBlank()) onConfirm(name) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPrimary,
                contentColor = Color.White
            )
        ) {
            Text("Mulai Sekarang", style = MaterialTheme.typography.titleMedium)
        }
    }
}