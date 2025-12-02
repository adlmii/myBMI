package af.mobile.mybmi.screens.profile

import af.mobile.mybmi.components.StandardScreenLayout
import af.mobile.mybmi.model.Badge
import af.mobile.mybmi.screens.profile.components.BadgeDetailDialog
import af.mobile.mybmi.screens.profile.components.BadgeItem
import af.mobile.mybmi.viewmodel.UserViewModel
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun BadgeListScreen(
    onNavigateBack: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    val userBadges by userViewModel.userBadges.collectAsState()
    val allBadges = Badge.entries
    var selectedBadge by remember { mutableStateOf<Badge?>(null) }

    val sortedBadges = remember(userBadges) {
        allBadges.sortedByDescending { badge ->
            userBadges.any { it.badgeId == badge.id }
        }
    }

    StandardScreenLayout(
        title = "Koleksi Pencapaian",
        onBack = onNavigateBack
    ) {
        // Grid Full (Scrollable)
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp)
        ) {
            // Gunakan sortedBadges
            items(sortedBadges) { badge ->
                val isUnlocked = userBadges.any { it.badgeId == badge.id }
                BadgeItem(badge = badge, isUnlocked = isUnlocked) {
                    selectedBadge = badge
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