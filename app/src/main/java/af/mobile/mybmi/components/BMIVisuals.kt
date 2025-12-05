package af.mobile.mybmi.components

import af.mobile.mybmi.model.BMICategory
import af.mobile.mybmi.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun BigBMIIndicator(
    bmiValue: Double,
    category: BMICategory,
    modifier: Modifier = Modifier,
    size: Dp = 200.dp,
    strokeWidth: Dp = 15.dp
) {
    val statusColor = getStatusColor(category)
    val progress = (bmiValue.toFloat() / 40f).coerceIn(0f, 1f)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        // Lingkaran Background
        CircularProgressIndicator(
            progress = { 1f },
            modifier = Modifier.size(size),
            color = MaterialTheme.colorScheme.surfaceVariant,
            strokeWidth = strokeWidth,
        )
        // Lingkaran Progress
        CircularProgressIndicator(
            progress = { progress },
            modifier = Modifier.size(size),
            color = statusColor,
            strokeWidth = strokeWidth,
        )
        // Teks di tengah
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = bmiValue.toString(),
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.label_bmi),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
    }
}