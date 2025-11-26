package af.mobile.mybmi.ui.result

import af.mobile.mybmi.theme.ColorRed
import af.mobile.mybmi.theme.ColorBlueLovely
import af.mobile.mybmi.theme.ColorGreenLovely
import af.mobile.mybmi.theme.ColorOrangeLovely
import af.mobile.mybmi.theme.ColorRedLovely
import af.mobile.mybmi.theme.GreenPrimary
import af.mobile.mybmi.model.BMICategory
import af.mobile.mybmi.viewmodel.ResultViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance // PENTING: Untuk deteksi brightness
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// Helper functions
fun getStatusColor(category: BMICategory): Color {
    return when (category) {
        BMICategory.UNDERWEIGHT -> Color(0xFF3B82F6)
        BMICategory.NORMAL -> Color(0xFF10B981)
        BMICategory.OVERWEIGHT -> Color(0xFFF59E0B)
        BMICategory.OBESE -> Color(0xFFEF4444)
    }
}

fun getStatusBackgroundColor(category: BMICategory): Color {
    return when (category) {
        BMICategory.UNDERWEIGHT -> ColorBlueLovely
        BMICategory.NORMAL -> ColorGreenLovely
        BMICategory.OVERWEIGHT -> ColorOrangeLovely
        BMICategory.OBESE -> ColorRedLovely
    }
}

@Composable
fun ResultScreen(
    onNavigateBack: () -> Unit,
    resultViewModel: ResultViewModel = viewModel()
) {
    val currentResult by resultViewModel.currentResult.collectAsState()
    var showUnsavedDialog by remember { mutableStateOf(false) }
    var isSaved by remember { mutableStateOf(false) }

    // LOGIC FIX: Deteksi dark mode dari warna background tema yang sedang aktif
    // Jika luminance (kecerahan) background rendah (< 0.5), berarti sedang Dark Mode
    val isDarkTheme = MaterialTheme.colorScheme.background.luminance() < 0.5f

    currentResult?.let { summary ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Top Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (isSaved) {
                        onNavigateBack()
                    } else {
                        showUnsavedDialog = true
                    }
                }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Detail Hasil",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    lineHeight = 32.sp
                )
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant, thickness = 1.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // CARD 1: Status Utama
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = getStatusColor(summary.category)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Status Berat Anda",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = summary.category.displayName,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "BMI: ${summary.bmi}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.95f)
                        )
                    }
                }

                // CARD 2: Penjelasan Detail
                // Gunakan background Surface (Abu-abu) di Dark Mode agar nyaman
                val detailCardColor = if (isDarkTheme) MaterialTheme.colorScheme.surface else getStatusBackgroundColor(summary.category)
                val statusTextColor = getStatusColor(summary.category)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = detailCardColor
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "Penjelasan Detail",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Berat Ideal
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Berat Ideal",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${summary.idealWeightRange.first.toInt()} - ${summary.idealWeightRange.second.toInt()} kg",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = statusTextColor
                                )
                            }
                        }

                        HorizontalDivider(
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        // Status Description
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Status",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = summary.category.description,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Advice
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Saran",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = summary.category.advice,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                // Button Simpan
                Button(
                    onClick = {
                        isSaved = true
                        resultViewModel.saveToHistory(summary)
                        resultViewModel.clearCurrentResult()
                        onNavigateBack()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        text = "Simpan Hasil",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }
        }

        // ==============================================
        // ALERT DIALOG FIX (Pure White Text in Dark Mode)
        // ==============================================
        if (showUnsavedDialog) {
            AlertDialog(
                onDismissRequest = { showUnsavedDialog = false },
                containerColor = MaterialTheme.colorScheme.surface,

                titleContentColor = MaterialTheme.colorScheme.onSurface,
                textContentColor = MaterialTheme.colorScheme.onSurface,

                shape = RoundedCornerShape(24.dp),
                title = {
                    Text(
                        text = "Belum Disimpan?",
                        color = if (isDarkTheme) Color.White else Color.Black,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text(
                        text = "Data hasil BMI ini akan hilang kalau kamu kembali sekarang. Yakin tidak mau menyimpannya?",
                        color = if (isDarkTheme) Color.White else Color.Black,
                        fontSize = 15.sp,
                        lineHeight = 22.sp
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showUnsavedDialog = false
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ColorRed
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text("Buang Data", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showUnsavedDialog = false }
                    ) {
                        Text(
                            text = "Batal",
                            // Gunakan Hijau jika background gelap, abu-abu jika terang
                            color = if (isDarkTheme) GreenPrimary else Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            )
        }
    }
}