package af.mobile.mybmi.screens.history

import af.mobile.mybmi.components.*
import af.mobile.mybmi.theme.*
import af.mobile.mybmi.util.ImageUtils
import af.mobile.mybmi.viewmodel.ResultViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    onNavigateBack: () -> Unit,
    resultViewModel: ResultViewModel = viewModel()
) {
    val selectedHistory by resultViewModel.selectedHistory.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()
    var hideUIForScreenshot by remember { mutableStateOf(false) }

    selectedHistory?.let { summary ->
        val statusColor = getStatusColor(summary.category)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Detail Riwayat") },
                    navigationIcon = {
                        if (!hideUIForScreenshot) {
                            IconButton(onClick = onNavigateBack) {
                                // PERBAIKAN: Menggunakan AutoMirrored.Rounded.ArrowBack
                                Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                            }
                        }
                    },
                    actions = {
                        if (!hideUIForScreenshot) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = StatusObese)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            },
            bottomBar = {
                if (!hideUIForScreenshot) {
                    Surface(shadowElevation = 16.dp, color = MaterialTheme.colorScheme.surface) {
                        Box(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
                            var isCapturing by remember { mutableStateOf(false) }
                            Button(
                                onClick = {
                                    coroutineScope.launch {
                                        isCapturing = true
                                        hideUIForScreenshot = true
                                        delay(500)
                                        try {
                                            val bitmap = ImageUtils.captureViewToBitmap(view)
                                            val filename = "BMI_Result_${System.currentTimeMillis()}"
                                            val success = ImageUtils.saveBitmapToGallery(context, bitmap, filename)
                                            if (success) Toast.makeText(context, "Hasil tersimpan!", Toast.LENGTH_SHORT).show()
                                            else Toast.makeText(context, "Gagal menyimpan.", Toast.LENGTH_SHORT).show()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_SHORT).show()
                                        } finally {
                                            hideUIForScreenshot = false
                                            isCapturing = false
                                        }
                                    }
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                enabled = !isCapturing
                            ) {
                                if (isCapturing) {
                                    Text("Memproses...", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                } else {
                                    Icon(Icons.Rounded.Download, contentDescription = null, tint = Color.White)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Download Hasil", style = MaterialTheme.typography.titleMedium, color = Color.White)
                                }
                            }
                        }
                    }
                }
            },
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Tanggal
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Rounded.CalendarToday, null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${summary.getDateFormatted()} • ${summary.getTimeFormatted()}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(24.dp))

                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(progress = { 1f }, modifier = Modifier.size(200.dp), color = MaterialTheme.colorScheme.surfaceVariant, strokeWidth = 15.dp)
                    CircularProgressIndicator(progress = { (summary.bmi.toFloat() / 40f).coerceIn(0f, 1f) }, modifier = Modifier.size(200.dp), color = statusColor, strokeWidth = 15.dp)
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(summary.bmi.toString(), style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onBackground, fontWeight = FontWeight.Bold)
                        Text("BMI", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Status Card
                Card(colors = CardDefaults.cardColors(containerColor = statusColor.copy(alpha = 0.1f)), shape = RoundedCornerShape(20.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(summary.category.displayName, style = MaterialTheme.typography.headlineSmall, color = statusColor, textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(summary.category.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface, textAlign = TextAlign.Center)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Details Card
                Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(24.dp)) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Info, null, tint = BrandPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Informasi Lengkap", style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        DetailRow("Berat Badan", "${summary.weight} kg")
                        CustomDivider()
                        DetailRow("Tinggi Badan", "${summary.height} cm")
                        CustomDivider()
                        DetailRow("Berat Ideal", "${summary.idealWeightRange.first.toInt()} - ${summary.idealWeightRange.second.toInt()} kg", isHighlight = true)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Advice Card
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), shape = RoundedCornerShape(24.dp)) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Catatan Medis", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(summary.category.advice, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }

    // --- GANTI DIALOG DI SINI ---
    if (showDeleteDialog) {
        ModernAlertDialog(
            onDismiss = { showDeleteDialog = false },
            title = "Hapus Data Ini?",
            description = "Data yang sedang Anda lihat akan dihapus permanen dari penyimpanan.",
            icon = Icons.Rounded.Delete,
            mainColor = StatusObese,
            positiveText = "Hapus",
            onPositive = {
                selectedHistory?.let { resultViewModel.deleteHistory(it.id) }
                showDeleteDialog = false
                onNavigateBack()
            }
        )
    }
}