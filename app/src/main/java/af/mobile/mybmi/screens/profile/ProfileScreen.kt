package af.mobile.mybmi.screens.profile

import af.mobile.mybmi.screens.profile.components.BadgeGrid
import af.mobile.mybmi.theme.*
import af.mobile.mybmi.viewmodel.UserViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import af.mobile.mybmi.R
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import java.io.File

@Composable
fun ProfileScreen(
    onNavigateToEdit: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToBadgeList: () -> Unit,
    userViewModel: UserViewModel? = null
) {
    val currentUser by userViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }
    val userBadges by userViewModel?.userBadges?.collectAsState() ?: remember { mutableStateOf(emptyList()) }

    // --- STRUKTUR UTAMA ---
    // Scroll dipindahkan ke SINI (Root Column) agar header ikut tergulir
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState()) // <--- Scrollable Parent
    ) {
        // --- BAGIAN 1: HEADER ---
        // Tidak perlu logic fixed/weight lagi. Cukup Box biasa.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(
                    brush = Brush.verticalGradient(listOf(GradientStart, GradientEnd)),
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentUser?.profileImagePath != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = File(currentUser!!.profileImagePath!!)),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(Icons.Rounded.Person, null, modifier = Modifier.size(50.dp), tint = BrandPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nama
                Text(
                    text = currentUser?.name ?: stringResource(R.string.default_user_name),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White
                )

                // Info Umur
                if (currentUser != null) {
                    Text(
                        text = "${currentUser!!.gender} • ${currentUser!!.getAgeDisplayString()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Badge Grid
            BadgeGrid(
                userBadges = userBadges,
                onSeeAllClick = onNavigateToBadgeList
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Menu Akun
            Text(
                text = stringResource(R.string.account_section_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                ProfileMenuItem(
                    Icons.Rounded.Edit,
                    stringResource(R.string.menu_edit_profile),
                    onNavigateToEdit
                )
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                ProfileMenuItem(
                    Icons.Rounded.Settings,
                    stringResource(R.string.menu_settings),
                    onNavigateToSettings)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = BrandPrimary)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        Icon(Icons.Rounded.ChevronRight, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}