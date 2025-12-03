package af.mobile.mybmi.screens.settings

import af.mobile.mybmi.R
import af.mobile.mybmi.components.StandardScreenLayout
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TermsScreen(
    onNavigateBack: () -> Unit
) {
    StandardScreenLayout(
        title = stringResource(R.string.terms_title),
        onBack = onNavigateBack
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // Badge Tanggal
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.padding(bottom = 24.dp)
            ) {
                Text(
                    text = stringResource(R.string.last_updated_fmt, "02 Des 2025"),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }

            // Intro
            Text(
                text = stringResource(R.string.terms_intro),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Sections (Menggunakan Card tipis agar lebih rapi)
            TermsSectionItem(stringResource(R.string.terms_sec_1_title), stringResource(R.string.terms_sec_1_desc))
            TermsSectionItem(stringResource(R.string.terms_sec_2_title), stringResource(R.string.terms_sec_2_desc))
            TermsSectionItem(stringResource(R.string.terms_sec_3_title), stringResource(R.string.terms_sec_3_desc))
            TermsSectionItem(stringResource(R.string.terms_sec_4_title), stringResource(R.string.terms_sec_4_desc))

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun TermsSectionItem(title: String, content: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
    }
}