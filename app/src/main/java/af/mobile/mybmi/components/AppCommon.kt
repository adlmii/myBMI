package af.mobile.mybmi.components

import af.mobile.mybmi.R
import af.mobile.mybmi.model.BMICategory
import af.mobile.mybmi.theme.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource // Import ini
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DetailRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (isHighlight) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyLarge,
            color = if (isHighlight) BrandPrimary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isHighlight) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun CustomDivider() {
    HorizontalDivider(
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
        thickness = 1.dp,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

fun getStatusColor(category: BMICategory): Color {
    return when (category) {
        BMICategory.UNDERWEIGHT -> StatusUnderweight
        BMICategory.NORMAL -> StatusNormal
        BMICategory.OVERWEIGHT -> StatusOverweight
        BMICategory.OBESE -> StatusObese
    }
}

@Composable
fun EmptyStateView(
    message: String = stringResource(R.string.empty_state_title),
    icon: androidx.compose.ui.graphics.vector.ImageVector = Icons.Rounded.Info
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}