package af.mobile.mybmi.screens.profile.components

import af.mobile.mybmi.components.ModernDialogContainer
import af.mobile.mybmi.database.UserBadgeEntity
import af.mobile.mybmi.model.Badge
import af.mobile.mybmi.theme.BrandPrimary
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun BadgeGrid(
    userBadges: List<UserBadgeEntity>
) {
    // Ambil semua definisi Badge dari Enum
    val allBadges = Badge.entries
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Pencapaian",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(3), // 3 Kolom
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.heightIn(max = 300.dp) // Batasi tinggi agar scroll parent jalan
        ) {
            items(allBadges) { badge ->
                val isUnlocked = userBadges.any { it.badgeId == badge.id }
                BadgeItem(badge = badge, isUnlocked = isUnlocked) {
                    selectedBadge = badge
                }
            }
        }
    }

    // Dialog Detail Badge
    if (selectedBadge != null) {
        val isUnlocked = userBadges.any { it.badgeId == selectedBadge!!.id }
        BadgeDetailDialog(badge = selectedBadge!!, isUnlocked = isUnlocked) {
            selectedBadge = null
        }
    }
}

@Composable
fun BadgeItem(badge: Badge, isUnlocked: Boolean, onClick: () -> Unit) {
    // Logic Warna: Jika locked -> Abu-abu, Jika unlocked -> BrandPrimary
    val bgColor = if (isUnlocked) BrandPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    val iconColor = if (isUnlocked) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)

    // Grayscale Filter untuk Locked (Opsional, tapi keren)
    val colorFilter = if (!isUnlocked) {
        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
    } else null

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = badge.icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(32.dp)
            )
            // Icon Gembok Kecil jika Locked
            if (!isUnlocked) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = "Locked",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = badge.title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun BadgeDetailDialog(badge: Badge, isUnlocked: Boolean, onDismiss: () -> Unit) {
    ModernDialogContainer(onDismiss = onDismiss) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    if (isUnlocked) BrandPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = badge.icon,
                contentDescription = null,
                tint = if (isUnlocked) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = badge.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Status Badge
        Text(
            text = if (isUnlocked) "Tercapai! 🎉" else "Belum Didapat 🔒",
            style = MaterialTheme.typography.labelLarge,
            color = if (isUnlocked) BrandPrimary else MaterialTheme.colorScheme.error
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = badge.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Requirement Box
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Syarat: ${badge.requirement}",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
            Text("Tutup")
        }
    }
}