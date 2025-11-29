package af.mobile.mybmi.ui.profile

import af.mobile.mybmi.theme.ColorRed
import af.mobile.mybmi.theme.GreenPrimary
import af.mobile.mybmi.viewmodel.UserViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector // Import ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit = {},
    userViewModel: UserViewModel? = null
) {
    val currentUser by userViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. HEADER MELENGKUNG
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GreenPrimary, GreenPrimary.copy(alpha = 0.8f))
                    ),
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
        )

        // 2. CONTENT
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "Profil Saya",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(40.dp))

            // AVATAR (Overlap effect)
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .shadow(16.dp, shape = CircleShape)
                    .background(Color.White, shape = CircleShape)
                    .padding(4.dp) // Border putih
                    .clip(CircleShape)
                    .background(GreenPrimary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("👤", fontSize = 56.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // NAME
            Text(
                text = currentUser?.name ?: "Pengguna",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Member Sejak 2024",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            // MENU LIST
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(8.dp, spotColor = Color.Black.copy(alpha = 0.05f), shape = RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    // Item 1: Ubah Profil (Icon Edit)
                    ProfileMenuItem(
                        text = "Ubah Profil",
                        icon = Icons.Default.Edit,
                        onClick = {}
                    )

                    Divider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f),
                        thickness = 1.dp
                    )

                    // Item 2: Pengaturan (Icon Settings)
                    ProfileMenuItem(
                        text = "Pengaturan",
                        icon = Icons.Default.Settings,
                        onClick = onNavigateToSettings
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // LOGOUT BUTTON
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = ColorRed.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text("Keluar", color = ColorRed, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(text: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon di Kiri (Warna Primary)
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        // Teks Menu
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )

        // Icon Panah di Kanan
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = "Open",
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}