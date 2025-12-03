package af.mobile.mybmi.screens.home

import af.mobile.mybmi.R
import af.mobile.mybmi.components.AchievementDialog
import af.mobile.mybmi.components.GradientScreenLayout
import af.mobile.mybmi.components.ModernDialogContainer
import af.mobile.mybmi.components.ModernInput
import af.mobile.mybmi.components.PrimaryButton
import af.mobile.mybmi.model.BMICheckSummary
import af.mobile.mybmi.theme.*
import af.mobile.mybmi.viewmodel.InputViewModel
import af.mobile.mybmi.viewmodel.ReminderViewModel
import af.mobile.mybmi.viewmodel.ResultViewModel
import af.mobile.mybmi.viewmodel.UserViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.LocalFireDepartment
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToResult: (BMICheckSummary) -> Unit,
    onNavigateToSettings: () -> Unit,
    inputViewModel: InputViewModel = viewModel(),
    userViewModel: UserViewModel? = null,
    reminderViewModel: ReminderViewModel = viewModel(),
    resultViewModel: ResultViewModel = viewModel()
) {
    val input by inputViewModel.input.collectAsState()
    val isCalculating by inputViewModel.isCalculating.collectAsState()
    val currentUser by userViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }
    val showNameInput by userViewModel?.showNameInput?.collectAsState() ?: remember { mutableStateOf(false) }

    val isReminderEnabled by reminderViewModel.isReminderEnabled.collectAsState()
    val reminderDay by reminderViewModel.reminderDay.collectAsState()

    val newBadges by resultViewModel.newlyUnlockedBadges.collectAsState()
    val streakCount by resultViewModel.streakCount.collectAsState()

    if (newBadges.isNotEmpty()) {
        AchievementDialog(
            badge = newBadges.first(),
            onDismiss = { resultViewModel.dismissBadge() }
        )
    }

    GradientScreenLayout(
        headerContent = { /* Kosong */ },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // HEADER
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.HealthAndSafety, null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (currentUser != null)
                                stringResource(R.string.welcome_user, currentUser!!.name)
                            else
                                stringResource(R.string.welcome_default),
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = stringResource(R.string.home_subtitle),
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                    }

                    // STREAK BADGE
                    StreakBadge(count = streakCount)
                }

                Spacer(modifier = Modifier.height(32.dp))

                // JADWAL CARD (REFACTORED)
                ScheduleStatusCard(
                    isEnabled = isReminderEnabled,
                    day = reminderDay,
                    onClick = onNavigateToSettings
                )

                Spacer(modifier = Modifier.height(24.dp))

                // INPUT CARD
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, spotColor = BrandPrimary.copy(alpha = 0.2f), shape = RoundedCornerShape(32.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = stringResource(R.string.input_card_title),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        ModernInput(
                            label = stringResource(R.string.label_height),
                            value = input.height,
                            onValueChange = { inputViewModel.updateHeight(it) },
                            suffix = stringResource(R.string.suffix_cm),
                            placeholderText = stringResource(R.string.hint_height)
                        )

                        ModernInput(
                            label = stringResource(R.string.label_weight),
                            value = input.weight,
                            onValueChange = { inputViewModel.updateWeight(it) },
                            suffix = stringResource(R.string.suffix_kg),
                            placeholderText = stringResource(R.string.hint_weight)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        PrimaryButton(
                            text = stringResource(R.string.btn_calculate),
                            onClick = {
                                inputViewModel.calculateBMI { summary -> onNavigateToResult(summary) }
                                inputViewModel.clearInput()
                            },
                            enabled = inputViewModel.canCalculate(),
                            isLoading = isCalculating
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    )

    if (showNameInput) {
        NameInputDialog(onConfirm = { name -> userViewModel?.saveUserName(name) })
    }
}

@Composable
fun StreakBadge(count: Int) {
    val isActive = count > 0
    val iconColor = if (isActive) StatusStreak else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    val textColor = if (isActive) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)

    Surface(
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(50),
        shadowElevation = if (isActive) 4.dp else 0.dp,
        modifier = Modifier.wrapContentSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.LocalFireDepartment,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$count",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
        }
    }
}

@Composable
fun ScheduleStatusCard(isEnabled: Boolean, day: Int, onClick: () -> Unit) {
    val containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    val contentColor = MaterialTheme.colorScheme.onSurface
    val icon = if (isEnabled) Icons.Rounded.CalendarMonth else Icons.Rounded.NotificationsOff
    val iconBgColor = if (isEnabled) BrandPrimary else MaterialTheme.colorScheme.error

    // String Resources
    val titleText = if (isEnabled)
        stringResource(R.string.reminder_title_on)
    else
        stringResource(R.string.reminder_title_off)

    val subtitleText = remember(isEnabled, day) {
        if (isEnabled) {
            val today = Calendar.getInstance()
            val targetDate = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, day) }
            if (today.get(Calendar.DAY_OF_MONTH) > day) targetDate.add(Calendar.MONTH, 1)
            val localeID = Locale.forLanguageTag("id-ID")
            SimpleDateFormat("dd MMMM yyyy", localeID).format(targetDate.time)
        } else {
            ""
        }
    }

    val finalSubtitle = if (isEnabled) subtitleText else stringResource(R.string.reminder_subtitle_off)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(48.dp).background(iconBgColor.copy(alpha = 0.15f), CircleShape),
            contentAlignment = Alignment.Center
        ) { Icon(icon, null, tint = iconBgColor, modifier = Modifier.size(24.dp)) }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(titleText, style = MaterialTheme.typography.labelMedium, color = contentColor.copy(alpha = 0.7f))
            Text(finalSubtitle, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = contentColor)
        }
        Spacer(modifier = Modifier.weight(1f))
        if (!isEnabled) Icon(Icons.Rounded.Notifications, null, tint = BrandPrimary, modifier = Modifier.size(20.dp))
    }
}

@Composable
fun NameInputDialog(onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    ModernDialogContainer(onDismiss = {}) {
        Box(modifier = Modifier.size(56.dp).background(BrandPrimary.copy(alpha = 0.1f), CircleShape), contentAlignment = Alignment.Center) {
            Icon(Icons.Rounded.Person, null, tint = BrandPrimary, modifier = Modifier.size(28.dp))
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text(stringResource(R.string.dialog_name_title), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        Spacer(modifier = Modifier.height(8.dp))
        Text(stringResource(R.string.dialog_name_question), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = name, onValueChange = { name = it },
            placeholder = { Text(stringResource(R.string.dialog_name_placeholder)) },
            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = BrandPrimary, cursorColor = BrandPrimary), singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = { if (name.isNotBlank()) onConfirm(name) }, modifier = Modifier.fillMaxWidth().height(50.dp), shape = RoundedCornerShape(12.dp), colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary, contentColor = Color.White)) {
            Text(stringResource(R.string.btn_start_now), style = MaterialTheme.typography.titleMedium)
        }
    }
}