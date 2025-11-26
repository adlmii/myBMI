package af.mobile.mybmi.ui.profile

import af.mobile.mybmi.theme.ColorRed
import af.mobile.mybmi.theme.Gray50
import af.mobile.mybmi.theme.Gray200
import af.mobile.mybmi.theme.GreenPrimary
import af.mobile.mybmi.theme.TextPrimary
import af.mobile.mybmi.theme.TextSecondary
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(GreenPrimary.copy(alpha = 0.15f), shape = RoundedCornerShape(60.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("👤", fontSize = 56.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Profil",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Menu items
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(8.dp)
            ) {
                ProfileMenuItem("Ubah Profil", "→", onClick = {})
                Divider(modifier = Modifier.padding(horizontal = 12.dp, vertical = 0.dp), thickness = 1.dp, color = Gray200)
                ProfileMenuItem("Pengaturan", "→", onClick = onNavigateToSettings)
                Divider(modifier = Modifier.padding(horizontal = 12.dp, vertical = 0.dp), thickness = 1.dp, color = Gray200)
                ProfileMenuItem("Bantuan & Laporan", "→", onClick = {})
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Logout button
        Button(
            onClick = { /* TODO */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = ColorRed.copy(alpha = 0.12f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Keluar",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = ColorRed,
                lineHeight = 24.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun ProfileMenuItem(text: String, icon: String, onClick: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            lineHeight = 22.sp
        )
        Text(
            text = icon,
            fontSize = 18.sp,
            color = TextSecondary
        )
    }
}