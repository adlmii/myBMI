package af.mobile.mybmi.screens.result

import af.mobile.mybmi.R
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
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ResultScreen(
    onNavigateBack: () -> Unit,
    resultViewModel: ResultViewModel = viewModel(),
    userViewModel: UserViewModel? = null
) {

    val currentResult by resultViewModel.selectedResult.collectAsState()

    val currentUser by userViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }

    var isSaved by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }

    BackHandler(enabled = !isSaved) { showUnsavedDialog = true }

    currentResult?.let { summary ->
        StandardScreenLayout(
            title = stringResource(R.string.result_screen_title),
            onBack = { if (isSaved) onNavigateBack() else showUnsavedDialog = true }
        ) {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. REUSABLE BMI INDICATOR
                BigBMIIndicator(
                    bmiValue = summary.bmi,
                    category = summary.category
                )

                Spacer(modifier = Modifier.height(32.dp))

                // 2. REUSABLE STATUS CARD
                BMIStatusCard(category = summary.category)

                Spacer(modifier = Modifier.height(24.dp))

                // 3. DETAIL INFO
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Rounded.Info, null, tint = BrandPrimary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                stringResource(R.string.result_info_section),
                                style = MaterialTheme.typography.titleMedium
                            )
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

                // 4. REUSABLE ADVICE CARD
                BMIAdviceCard(
                    advice = summary.category.advice,
                    title = stringResource(R.string.result_advice_section)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // BUTTONS
                PrimaryButton(
                    text = if (isSaved) stringResource(R.string.btn_status_saved) else stringResource(R.string.btn_save_result),
                    onClick = {
                        if (!isSaved && currentUser != null) {
                            resultViewModel.saveResult(currentUser!!.id, summary)

                            isSaved = true
                        }
                        onNavigateBack()
                    },
                    enabled = !isSaved
                )
            }
        }

        // Dialog Unsaved
        if (showUnsavedDialog) {
            ModernAlertDialog(
                onDismiss = { showUnsavedDialog = false },
                title = stringResource(R.string.dialog_unsaved_title),
                description = stringResource(R.string.dialog_unsaved_desc),
                icon = Icons.Rounded.Warning,
                mainColor = StatusObese,
                positiveText = stringResource(R.string.btn_exit),
                onPositive = {
                    showUnsavedDialog = false
                    resultViewModel.selectHistory(af.mobile.mybmi.model.BMICheckSummary.empty())

                    onNavigateBack()
                }
            )
        }
    }
}