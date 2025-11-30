package af.mobile.mybmi.screens.result

import af.mobile.mybmi.components.*
import af.mobile.mybmi.theme.*
import af.mobile.mybmi.viewmodel.ResultViewModel
import af.mobile.mybmi.viewmodel.UserViewModel
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    onNavigateBack: () -> Unit,
    resultViewModel: ResultViewModel = viewModel(),
    userViewModel: UserViewModel? = null
) {
    val currentResult by resultViewModel.currentResult.collectAsState()
    val currentUser by userViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }

    // Deteksi Dark Mode untuk tombol
    val isDarkMode = MaterialTheme.colorScheme.background.luminance() < 0.5f

    var isSaved by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }

    // Handle Back Press
    BackHandler(enabled = !isSaved) {
        showUnsavedDialog = true
    }

    currentResult?.let { summary ->
        val statusColor = getStatusColor(summary.category)

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Hasil Analisa", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = {
                            if (isSaved) onNavigateBack() else showUnsavedDialog = true
                        }) {
                            Icon(Icons.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
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
                        Text(
                            text = "BMI",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // STATUS CARD (Centered)
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
                            color = statusColor,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = summary.category.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // DETAILS CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Info, contentDescription = null, tint = BrandPrimary)
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

                // ADVICE
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text("Saran Medis", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = summary.category.advice,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // --- TOMBOL SIMPAN HASIL ---
                Button(
                    onClick = {
                        if (!isSaved && currentUser != null) {
                            resultViewModel.saveToHistory(summary, currentUser!!.id)
                            isSaved = true
                        }
                        onNavigateBack()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),

                    // MENGGUNAKAN HELPER (Selalu Active)
                    colors = ButtonDefaults.buttonColors(
                        containerColor = getActionButtonContainerColor(isDarkMode, true),
                        contentColor = getActionButtonContentColor(true)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(if (isSaved) Icons.Rounded.CheckCircle else Icons.Rounded.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (isSaved) "Tersimpan" else "Simpan Hasil")
                }
            }
        }

        // Alert Dialog
        if (showUnsavedDialog) {
            AlertDialog(
                onDismissRequest = { showUnsavedDialog = false },
                title = { Text("Belum Disimpan") },
                text = { Text("Data hasil cek ini akan hilang jika Anda kembali sekarang. Yakin ingin keluar?") },
                containerColor = MaterialTheme.colorScheme.surface,
                confirmButton = {
                    Button(
                        onClick = {
                            showUnsavedDialog = false
                            resultViewModel.clearCurrentResult()
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = StatusObese)
                    ) {
                        Text("Keluar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUnsavedDialog = false }) {
                        Text("Batal", color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            )
        }
    }
}