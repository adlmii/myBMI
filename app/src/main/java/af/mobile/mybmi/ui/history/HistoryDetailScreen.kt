package af.mobile.mybmi.ui.history

import af.mobile.mybmi.theme.Gray200
import af.mobile.mybmi.theme.Gray50
import af.mobile.mybmi.theme.TextPrimary
import af.mobile.mybmi.theme.TextSecondary
import af.mobile.mybmi.theme.ColorBlueLovely
import af.mobile.mybmi.theme.ColorGreenLovely
import af.mobile.mybmi.theme.ColorOrangeLovely
import af.mobile.mybmi.theme.ColorRedLovely
import af.mobile.mybmi.model.BMICategory
import af.mobile.mybmi.viewmodel.ResultViewModel
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.graphics.Color

// Helper function untuk mendapatkan warna berdasarkan kategori
fun getStatusColor(category: BMICategory): Color {
    return when (category) {
        BMICategory.UNDERWEIGHT -> Color(0xFF3B82F6) // Blue
        BMICategory.NORMAL -> Color(0xFF10B981) // Green
        BMICategory.OVERWEIGHT -> Color(0xFFF59E0B) // Orange
        BMICategory.OBESE -> Color(0xFFEF4444) // Red
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
fun HistoryDetailScreen(
    onNavigateBack: () -> Unit,
    resultViewModel: ResultViewModel = viewModel()
) {
    val selectedHistory by resultViewModel.selectedHistory.collectAsState()

    selectedHistory?.let { summary ->
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar with back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextPrimary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Detail Hasil",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    lineHeight = 32.sp
                )
            }

            Divider(color = Gray200, thickness = 1.dp)

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Status Card
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
                            fontWeight = FontWeight.Medium,
                            lineHeight = 20.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = summary.category.displayName,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 40.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "BMI: ${summary.bmi}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White.copy(alpha = 0.95f),
                            lineHeight = 26.sp
                        )
                    }
                }

                // Penjelasan Card
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = getStatusBackgroundColor(summary.category)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        // Title
                        Text(
                            text = "Penjelasan Detail",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            lineHeight = 24.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Ideal Weight Section
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
                                    color = TextSecondary,
                                    lineHeight = 18.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${summary.idealWeightRange.first.toInt()} - ${summary.idealWeightRange.second.toInt()} kg",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = getStatusColor(summary.category),
                                    lineHeight = 24.sp
                                )
                            }
                        }

                        Divider(color = Gray200, thickness = 1.dp, modifier = Modifier.padding(vertical = 12.dp))

                        // Status Section
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Status",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = summary.category.description,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                lineHeight = 22.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Advice Section
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = "Saran",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = summary.category.advice,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = TextPrimary,
                                lineHeight = 22.sp
                            )
                        }
                    }
                }
            }
        }
    } ?: run {
        // Fallback if no history selected
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Data tidak ditemukan", color = TextSecondary)
        }
    }
}