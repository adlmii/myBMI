package af.mobile.mybmi.screens.result

import af.mobile.mybmi.components.* // Import StandardScreenLayout, InfoRow, PrimaryButton, dll
import af.mobile.mybmi.theme.*
import af.mobile.mybmi.viewmodel.ResultViewModel
import af.mobile.mybmi.viewmodel.UserViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ResultScreen(
    onNavigateBack: () -> Unit,
    resultViewModel: ResultViewModel = viewModel(),
    userViewModel: UserViewModel? = null
) {
    val currentResult by resultViewModel.currentResult.collectAsState()
    val currentUser by userViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }

    var isSaved by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = !isSaved) { showUnsavedDialog = true }

    currentResult?.let { summary ->
        val statusColor = getStatusColor(summary.category)

        // Gunakan Layout Standar
        StandardScreenLayout(
            title = "Hasil Analisa",
            onBack = { if (isSaved) onNavigateBack() else showUnsavedDialog = true }
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // BIG BMI INDICATOR
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = { 1f },
                        modifier = Modifier.size(200.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        strokeWidth = 15.dp,
                    )
                    CircularProgressIndicator(
                        progress = { (summary.bmi.toFloat() / 40f).coerceIn(0f, 1f) },
                        modifier = Modifier.size(200.dp),
                        color = statusColor,
                        strokeWidth = 15.dp,
                    )
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = summary.bmi.toString(),
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold
                        )
                        Text("BMI", style = MaterialTheme.typography.labelLarge)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // STATUS CARD
                Card(
                    colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(20.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = summary.category.displayName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = statusColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = summary.category.description,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // DETAILS CARD (Menggunakan InfoRow agar lebih rapi)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Info, null, tint = BrandPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Informasi Lengkap", style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        // Refactor pakai InfoRow (Harus ditambahkan ke AppCommon.kt atau AppCards.kt sesuai saran sebelumnya)
                        // Jika belum ada InfoRow, gunakan DetailRow yang lama
                        DetailRow("Berat Badan", "${summary.weight} kg")
                        CustomDivider()
                        DetailRow("Tinggi Badan", "${summary.height} cm")
                        CustomDivider()
                        DetailRow("Berat Ideal", "${summary.idealWeightRange.first.toInt()} - ${summary.idealWeightRange.second.toInt()} kg", isHighlight = true)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ADVICE CARD
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Saran Medis", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(summary.category.advice, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // TOMBOL SIMPAN
                PrimaryButton(
                    text = if (isSaved) "Tersimpan" else "Simpan Hasil",
                    onClick = {
                        if (!isSaved && currentUser != null) {
                            resultViewModel.saveToHistory(summary, currentUser!!.id)
                            isSaved = true
                        }
                        onNavigateBack()
                    },
                    enabled = !isSaved
                )
            }
        }

        // Dialog Belum Disimpan (ModernAlertDialog sudah ada)
        if (showUnsavedDialog) {
            ModernAlertDialog(
                onDismiss = { showUnsavedDialog = false },
                title = "Belum Disimpan",
                description = "Data hasil cek ini akan hilang jika Anda kembali sekarang. Yakin ingin keluar?",
                icon = Icons.Rounded.Warning,
                mainColor = StatusObese,
                positiveText = "Ya, Keluar",
                onPositive = {
                    showUnsavedDialog = false
                    resultViewModel.clearCurrentResult()
                    onNavigateBack()
                }
            )
        }
    }
}