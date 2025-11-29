package af.mobile.mybmi.ui.history

import af.mobile.mybmi.model.BMICheckSummary
import af.mobile.mybmi.viewmodel.ResultViewModel
import af.mobile.mybmi.viewmodel.UserViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HistoryScreen(
    onNavigateToDetail: () -> Unit,
    resultViewModel: ResultViewModel = viewModel(),
    userViewModel: UserViewModel? = null
) {
    val history by resultViewModel.history.collectAsState()
    val currentUser by userViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }

    LaunchedEffect(currentUser?.id) {
        if (currentUser != null && currentUser!!.id > 0) {
            resultViewModel.loadHistory(currentUser!!.id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        // 1. SIMPLE HEADER (Tanpa Background Gradient)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Riwayat",
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Perjalanan kesehatanmu sejauh ini",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        // 2. LIST CONTENT
        if (history.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                EmptyStateCard()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 100.dp) // Ruang untuk scroll
            ) {
                items(history) { summary ->
                    ModernHistoryCard(
                        summary = summary,
                        onClick = {
                            resultViewModel.selectHistory(summary)
                            onNavigateToDetail()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernHistoryCard(
    summary: BMICheckSummary,
    onClick: () -> Unit
) {
    // KARTU MODERN: Shadow halus, rounded corner, layout bersih
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp,
                spotColor = Color.Black.copy(alpha = 0.05f), // Bayangan sangat halus
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = summary.getDateFormatted(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "BMI: ${summary.bmi} • ${summary.weight} kg",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Badge Kategori Minimalis
            Surface(
                color = Color(android.graphics.Color.parseColor(summary.category.colorHex)).copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = summary.category.displayName,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(android.graphics.Color.parseColor(summary.category.colorHex)),
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyStateCard() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.offset(y = (-40).dp) // Sedikit naik ke atas agar center visual
    ) {
        // ICON STYLE: Circle with Shadow & Glow (Sama seperti Profile/Dialog)
        Box(
            modifier = Modifier
                .size(100.dp)
                .shadow(16.dp, shape = CircleShape)
                .background(Color.White, shape = CircleShape)
                .padding(4.dp) // Border Putih
                .clip(CircleShape)
                .background(af.mobile.mybmi.theme.GreenPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.DateRange, // Icon Kalender/Riwayat
                contentDescription = "Empty History",
                tint = af.mobile.mybmi.theme.GreenPrimary,
                modifier = Modifier.size(48.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Belum ada riwayat",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Data akan muncul di sini setelah\nAnda menghitung BMI",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )
    }
}