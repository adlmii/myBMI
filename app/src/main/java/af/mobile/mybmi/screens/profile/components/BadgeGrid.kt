package af.mobile.mybmi.screens.profile.components

import af.mobile.mybmi.components.ModernDialogContainer
import af.mobile.mybmi.database.UserBadgeEntity
import af.mobile.mybmi.model.Badge
import af.mobile.mybmi.theme.BrandPrimary
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BadgeGrid(
    userBadges: List<UserBadgeEntity>,
    onSeeAllClick: () -> Unit
) {
    val allBadges = Badge.entries
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }

    val sortedBadges = remember(userBadges) {
        allBadges.sortedByDescending { badge ->
            userBadges.any { it.badgeId == badge.id }
        }
    }

    val previewBadges = sortedBadges.take(6)

    Column(modifier = Modifier.fillMaxWidth()) {
        // Header Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp, start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pencapaian",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Row(
                modifier = Modifier.clickable { onSeeAllClick() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Lihat Semua",
                    style = MaterialTheme.typography.labelMedium,
                    color = BrandPrimary,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = BrandPrimary,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        val rows = previewBadges.chunked(3)

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rows.forEach { rowBadges ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowBadges.forEach { badge ->
                        val isUnlocked = userBadges.any { it.badgeId == badge.id }
                        Box(modifier = Modifier.weight(1f)) {
                            BadgeItem(badge = badge, isUnlocked = isUnlocked) {
                                selectedBadge = badge
                            }
                        }
                    }

                    val emptySlots = 3 - rowBadges.size
                    repeat(emptySlots) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    // Dialog Detail
    if (selectedBadge != null) {
        val isUnlocked = userBadges.any { it.badgeId == selectedBadge!!.id }
        BadgeDetailDialog(badge = selectedBadge!!, isUnlocked = isUnlocked) {
            selectedBadge = null
        }
    }
}

@Composable
fun BadgeItem(badge: Badge, isUnlocked: Boolean, onClick: () -> Unit) {
    val containerColor = if (isUnlocked) {
        BrandPrimary.copy(alpha = 0.15f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    val iconColor = if (isUnlocked) {
        BrandPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
    }

    val textColor = if (isUnlocked) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = badge.icon,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(32.dp)
                )

                if (!isUnlocked) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .background(Color.Black.copy(alpha = 0.2f), CircleShape), // Overlay redup
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Lock,
                            contentDescription = "Locked",
                            tint = Color.White.copy(alpha = 0.9f),
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = badge.title,
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (isUnlocked) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun BadgeDetailDialog(badge: Badge, isUnlocked: Boolean, onDismiss: () -> Unit) {
    ModernDialogContainer(onDismiss = onDismiss) {
        // --- HEADER ICON ---
        val headerBgColor = if (isUnlocked) BrandPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
        val headerIconColor = if (isUnlocked) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant

        Box(
            modifier = Modifier
                .size(80.dp)
                .background(headerBgColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = badge.icon,
                contentDescription = null,
                tint = headerIconColor,
                modifier = Modifier.size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- JUDUL ---
        Text(
            text = badge.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        // --- STATUS BADGE (Icon + Text) ---
        val statusIcon = if (isUnlocked) Icons.Rounded.CheckCircle else Icons.Rounded.Lock
        val statusText = if (isUnlocked) "Tercapai" else "Belum Didapat"
        val statusColor = if (isUnlocked) BrandPrimary else MaterialTheme.colorScheme.error

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = statusIcon,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = statusText,
                style = MaterialTheme.typography.labelLarge,
                color = statusColor,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- DESKRIPSI ---
        Text(
            text = badge.description,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // --- KOTAK SYARAT ---
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Syarat",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = badge.requirement,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // --- TOMBOL TUTUP ---
        Button(
            onClick = onDismiss,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPrimary,
                contentColor = Color.White
            )
        ) {
            Text("Tutup", fontWeight = FontWeight.Bold)
        }
    }
}