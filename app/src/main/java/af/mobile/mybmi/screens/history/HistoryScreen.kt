package af.mobile.mybmi.screens.history

import af.mobile.mybmi.R
import af.mobile.mybmi.components.*
import af.mobile.mybmi.theme.*
import af.mobile.mybmi.viewmodel.ResultViewModel
import af.mobile.mybmi.viewmodel.UserViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

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

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (history.isEmpty()) {
            EmptyHistoryState(modifier = Modifier.padding(paddingValues))
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    top = paddingValues.calculateTopPadding() + 24.dp,
                    bottom = 100.dp,
                    start = 24.dp,
                    end = 24.dp
                ),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // 1. JUDUL HALAMAN
                item {
                    Column {
                        Text(
                            text = stringResource(R.string.history_title),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = stringResource(R.string.history_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // 2. KARTU GRAFIK
                item {
                    if (history.size >= 2) {
                        ChartCardContainer {
                            BMITrendChart(history = history)
                        }
                    } else {
                        LockedTrendCard()
                    }
                }

                // 3. SECTION HEADER
                item {
                    Text(
                        text = stringResource(R.string.history_recent_section),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                }

                // 4. LIST ITEM
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

    // Dialog Konfirmasi Hapus
    if (showDeleteDialog && itemToDelete != null) {
        ModernAlertDialog(
            onDismiss = { showDeleteDialog = false; itemToDelete = null },
            title = stringResource(R.string.dialog_delete_title),
            description = stringResource(R.string.dialog_delete_desc),
            icon = Icons.Rounded.Delete,
            mainColor = StatusObese,
            positiveText = stringResource(R.string.btn_delete),
            onPositive = {
                itemToDelete?.let { resultViewModel.deleteHistory(it) }
                showDeleteDialog = false
                itemToDelete = null
            }
        )
    }
}

// --- KOMPONEN UI ---

@Composable
fun ChartCardContainer(content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHighest.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(modifier = Modifier.padding(top = 16.dp, bottom = 8.dp, start = 8.dp, end = 8.dp)) {
            content()
        }
    }
}

@Composable
fun LockedTrendCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.BarChart,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.size(28.dp)
                    )
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.BottomEnd)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.chart_locked_title),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = stringResource(R.string.chart_locked_desc),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun EmptyHistoryState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(BrandPrimary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Rounded.BarChart,
                contentDescription = null,
                tint = BrandPrimary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.empty_history_title),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.empty_history_desc),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}