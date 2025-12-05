package af.mobile.mybmi.screens.history

import af.mobile.mybmi.R
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
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HistoryDetailScreen(
    onNavigateBack: () -> Unit,
    resultViewModel: ResultViewModel = viewModel()
) {
    val selectedHistory by resultViewModel.selectedResult.collectAsState()
    var showDeleteDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()

    var hideUIForScreenshot by remember { mutableStateOf(false) }
    var isCapturing by remember { mutableStateOf(false) }

    selectedHistory?.let { summary ->
        StandardScreenLayout(
            title = stringResource(R.string.history_detail_title),
            onBack = if (!hideUIForScreenshot) onNavigateBack else null,
            actions = {
                if (!hideUIForScreenshot) {
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Rounded.Delete, contentDescription = "Delete", tint = StatusObese)
                    }
                }
            },
            bottomBar = {
                if (!hideUIForScreenshot) {
                    Surface(
                        shadowElevation = 16.dp,
                        color = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(16.dp)
                                .navigationBarsPadding()
                        ) {
                            PrimaryButton(
                                text = if (isCapturing) stringResource(R.string.btn_downloading) else stringResource(R.string.btn_download_result),
                                onClick = {
                                    coroutineScope.launch {
                                        isCapturing = true
                                        hideUIForScreenshot = true
                                        delay(500)
                                        try {
                                            val bitmap = ImageUtils.captureViewToBitmap(view)
                                            val filename = "BMI_Result_${System.currentTimeMillis()}"
                                            val message = ImageUtils.saveBitmapToGallery(context, bitmap, filename)
                                            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            Toast.makeText(context, "Gagal: ${e.message}", Toast.LENGTH_LONG).show()
                                        } finally {
                                            hideUIForScreenshot = false
                                            isCapturing = false
                                        }
                                    }
                                },
                                enabled = !isCapturing,
                                isLoading = isCapturing
                            )
                        }
                    }
                }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Tanggal
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        Icons.Rounded.CalendarToday,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${summary.getDateFormatted()} • ${summary.getTimeFormatted()}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // REUSABLE INDICATOR
                BigBMIIndicator(
                    bmiValue = summary.bmi,
                    category = summary.category
                )

                Spacer(modifier = Modifier.height(32.dp))

                // REUSABLE STATUS
                BMIStatusCard(category = summary.category)

                Spacer(modifier = Modifier.height(24.dp))

                // REUSABLE DETAILS
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Info, null, tint = BrandPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(stringResource(R.string.result_info_section), style = MaterialTheme.typography.titleMedium)
                        }
                        Spacer(modifier = Modifier.height(16.dp))

                        DetailRow(stringResource(R.string.label_weight), "${summary.weight} kg")
                        CustomDivider()
                        DetailRow(stringResource(R.string.label_height), "${summary.height} cm")
                        CustomDivider()
                        DetailRow(
                            stringResource(R.string.label_ideal_weight),
                            "${summary.idealWeightRange.first.toInt()} - ${summary.idealWeightRange.second.toInt()} kg",
                            isHighlight = true
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // REUSABLE ADVICE
                BMIAdviceCard(
                    advice = summary.category.advice,
                    title = stringResource(R.string.result_medical_notes)
                )

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }

    if (showDeleteDialog) {
        ModernAlertDialog(
            onDismiss = { showDeleteDialog = false },
            title = stringResource(R.string.dialog_delete_single_title),
            description = stringResource(R.string.dialog_delete_single_desc),
            icon = Icons.Rounded.Delete,
            mainColor = StatusObese,
            positiveText = stringResource(R.string.btn_delete),
            onPositive = {
                selectedHistory?.let { resultViewModel.deleteHistory(it.id) }
                showDeleteDialog = false
                onNavigateBack()
            }
        )
    }
}