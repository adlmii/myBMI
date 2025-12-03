package af.mobile.mybmi.screens.settings

import af.mobile.mybmi.R
import af.mobile.mybmi.components.StandardScreenLayout
import af.mobile.mybmi.theme.BrandPrimary
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun GuideScreen(
    onNavigateBack: () -> Unit
) {
    StandardScreenLayout(
        title = stringResource(R.string.guide_title),
        onBack = onNavigateBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GuideCard(
                number = 1,
                title = stringResource(R.string.guide_step_1_title),
                description = stringResource(R.string.guide_step_1_desc)
            )
            GuideCard(
                number = 2,
                title = stringResource(R.string.guide_step_2_title),
                description = stringResource(R.string.guide_step_2_desc)
            )
            GuideCard(
                number = 3,
                title = stringResource(R.string.guide_step_3_title),
                description = stringResource(R.string.guide_step_3_desc)
            )
            GuideCard(
                number = 4,
                title = stringResource(R.string.guide_step_4_title),
                description = stringResource(R.string.guide_step_4_desc)
            )
            GuideCard(
                number = 5,
                title = stringResource(R.string.guide_step_5_title),
                description = stringResource(R.string.guide_step_5_desc)
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun GuideCard(number: Int, title: String, description: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Nomor Bulat
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(BrandPrimary, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "$number",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}