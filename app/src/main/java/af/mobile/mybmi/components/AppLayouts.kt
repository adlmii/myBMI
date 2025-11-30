package af.mobile.mybmi.components

import af.mobile.mybmi.theme.BrandPrimary
import af.mobile.mybmi.theme.GradientEnd
import af.mobile.mybmi.theme.GradientStart
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

// LAYOUT 1: Standar (Putih/Dark dengan dekorasi minimalis)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StandardScreenLayout(
    title: String,
    onBack: (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = actions, // Pasang actions di sini
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        bottomBar = bottomBar, // Pasang bottomBar di sini
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Dekorasi Background
            Box(
                modifier = Modifier
                    .offset(x = (-100).dp, y = (-50).dp)
                    .size(300.dp)
                    .background(BrandPrimary.copy(alpha = 0.03f), CircleShape)
            )

            // Konten
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp) // Padding kiri-kanan
            ) {
                // Spacer kecil di atas agar tidak terlalu mepet header
                Spacer(modifier = Modifier.height(16.dp))
                content()
            }
        }
    }
}

// LAYOUT 2: Gradient Header (Untuk Home, Profile, History)
@Composable
fun GradientScreenLayout(
    headerContent: @Composable BoxScope.() -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Bagian Header Melengkung
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp) // Tinggi standar header
                .background(
                    brush = Brush.verticalGradient(listOf(GradientStart, GradientEnd)),
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
        ) {
            headerContent()
        }

        // Bagian Konten (Menumpuk di atasnya jika perlu, atau di bawah)
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            content()
        }
    }
}