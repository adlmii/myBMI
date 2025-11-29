package af.mobile.mybmi.ui.splash

import af.mobile.mybmi.R
import af.mobile.mybmi.theme.GreenPrimary
import af.mobile.mybmi.viewmodel.UserViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    userViewModel: UserViewModel? = null
) {
    // Navigasi otomatis setelah 2.5 detik
    LaunchedEffect(Unit) {
        delay(2500)
        onNavigateToHome()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GreenPrimary,
                        Color(0xFF34D399) // GreenLight manually defined to match theme feel
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // 1. ELEMEN DEKORATIF (Lingkaran Transparan)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-60).dp)
                .size(200.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-60).dp, y = 60.dp)
                .size(200.dp)
                .background(Color.White.copy(alpha = 0.1f), CircleShape)
        )

        // 2. KONTEN UTAMA
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            // LOGO CARD (White Container with Shadow)
            Card(
                modifier = Modifier
                    .size(140.dp)
                    .shadow(
                        elevation = 16.dp,
                        spotColor = Color.Black.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(32.dp)
                    ),
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "myBMI Logo",
                        modifier = Modifier
                            .size(100.dp) // Ukuran logo di dalam card
                            .padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // APP NAME
            Text(
                text = "myBMI",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // TAGLINE
            Text(
                text = "Health Tracker & Analyzer",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.9f)
            )

            Spacer(modifier = Modifier.height(48.dp))

            // LOADING INDICATOR
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(32.dp),
                strokeWidth = 3.dp,
                trackColor = Color.White.copy(alpha = 0.3f)
            )
        }

        // 3. FOOTER VERSION
        Text(
            text = "Versi 1.0.0",
            fontSize = 12.sp,
            color = Color.White.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
        )
    }
}