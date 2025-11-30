package af.mobile.mybmi.screens.history

import af.mobile.mybmi.components.EmptyStateCard
import af.mobile.mybmi.components.ModernHistoryCard
import af.mobile.mybmi.components.ModernAlertDialog
import af.mobile.mybmi.theme.*
import af.mobile.mybmi.viewmodel.ResultViewModel
import af.mobile.mybmi.viewmodel.UserViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateToDetail: () -> Unit,
    resultViewModel: ResultViewModel = viewModel(),
    userViewModel: UserViewModel? = null
) {
    val history by resultViewModel.history.collectAsState()
    val currentUser by userViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }
    var itemToDelete by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(currentUser?.id) {
        if (currentUser != null && currentUser!!.id > 0) {
            resultViewModel.loadHistory(currentUser!!.id)
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)
    ) {
        // HEADER GRADIENT
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    brush = Brush.verticalGradient(colors = listOf(GradientStart, GradientEnd)),
                    shape = RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp)
                )
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart).padding(24.dp)
            ) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Riwayat Kesehatan",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Perjalanan sehatmu tercatat di sini",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        // LIST
        if (history.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                EmptyStateCard()
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp, start = 24.dp, end = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Text(
                        text = "Data Terakhir",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )
                }

                items(history) { summary ->
                    ModernHistoryCard(
                        summary = summary,
                        onClick = {
                            resultViewModel.selectHistory(summary)
                            onNavigateToDetail()
                        },
                        onDelete = {
                            itemToDelete = summary.id
                            showDeleteDialog = true
                        }
                    )
                }
            }
        }
    }

    // --- GANTI DIALOG DI SINI ---
    if (showDeleteDialog && itemToDelete != null) {
        ModernAlertDialog(
            onDismiss = {
                showDeleteDialog = false
                itemToDelete = null
            },
            title = "Hapus Data?",
            description = "Data ini akan dihapus permanen dari riwayat Anda.",
            icon = Icons.Rounded.Delete,
            mainColor = StatusObese,
            positiveText = "Hapus",
            onPositive = {
                itemToDelete?.let { resultViewModel.deleteHistory(it) }
                showDeleteDialog = false
                itemToDelete = null
            }
        )
    }
}