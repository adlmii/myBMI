package af.mobile.mybmi.components

import af.mobile.mybmi.theme.BrandPrimary
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

// --- 1. MODERN INPUT BIASA (Untuk Text/Angka) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String,
    placeholderText: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(placeholderText, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
            },
            suffix = {
                if (suffix.isNotEmpty()) {
                    Text(
                        text = suffix,
                        style = MaterialTheme.typography.titleMedium,
                        color = BrandPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = BrandPrimary
            ),
            singleLine = true,
            textStyle = MaterialTheme.typography.titleMedium
        )
    }
}

// --- 2. MODERN CLICKABLE INPUT (Untuk Date Picker/Dropdown) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModernClickableInput(
    label: String,
    value: String,
    onClick: () -> Unit,
    suffix: String = "",
    placeholderText: String = ""
) {
    Column(modifier = Modifier.padding(bottom = 16.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
        )

        // TextField ReadOnly dengan event Click manual
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },

            // LOGIKA PLACEHOLDER BARU: Tampil jika value kosong
            placeholder = {
                if (value.isEmpty() && placeholderText.isNotEmpty()) {
                    Text(placeholderText, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
                }
            },

            trailingIcon = {
                Icon(
                    imageVector = Icons.Rounded.CalendarToday,
                    contentDescription = null,
                    tint = BrandPrimary
                )
            },
            suffix = {
                if (suffix.isNotEmpty()) {
                    Text(
                        text = suffix,
                        style = MaterialTheme.typography.titleMedium,
                        color = BrandPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledBorderColor = Color.Transparent,
                disabledTrailingIconColor = BrandPrimary
            ),
            textStyle = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun GenderChip(text: String, icon: ImageVector, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bgColor = if (isSelected) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = modifier
            .height(50.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, style = MaterialTheme.typography.bodyMedium, color = contentColor, fontWeight = FontWeight.Bold)
    }
}