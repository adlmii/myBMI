package af.mobile.mybmi.screens.home

import af.mobile.mybmi.components.GradientScreenLayout
import af.mobile.mybmi.components.ModernDialogContainer
import af.mobile.mybmi.components.ModernInput
import af.mobile.mybmi.components.PrimaryButton
import af.mobile.mybmi.model.BMICheckSummary
import af.mobile.mybmi.theme.*
import af.mobile.mybmi.viewmodel.InputViewModel
import af.mobile.mybmi.viewmodel.UserViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    onNavigateToResult: (BMICheckSummary) -> Unit,
    inputViewModel: InputViewModel = viewModel(),
    userViewModel: UserViewModel? = null
) {
    val input by inputViewModel.input.collectAsState()
    val isCalculating by inputViewModel.isCalculating.collectAsState()
    val currentUser by userViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }
    val showNameInput by userViewModel?.showNameInput?.collectAsState() ?: remember { mutableStateOf(false) }

    // Gunakan Layout Gradient Baru
    GradientScreenLayout(
        headerContent = {
            // Header Content (Fixed di atas)
            // Bisa kosong jika ingin judul ikut scroll, tapi di sini kita buat semi-fixed dekorasi
        },
        content = {
            // Konten yang bisa di-scroll
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // HEADER TEXT (Ikut scroll agar tidak menutupi form di HP kecil)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Rounded.HealthAndSafety, null, tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = if (currentUser != null) "Halo, ${currentUser!!.name}" else "Selamat Datang",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                        Text(
                            text = "Cek Kesehatanmu",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // MAIN INPUT CARD
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(16.dp, spotColor = BrandPrimary.copy(alpha = 0.2f), shape = RoundedCornerShape(32.dp)),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(32.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Kalkulator BMI",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        ModernInput(
                            label = "Tinggi Badan",
                            value = input.height,
                            onValueChange = { inputViewModel.updateHeight(it) },
                            suffix = "cm",
                            placeholderText = "Contoh: 170"
                        )

                        ModernInput(
                            label = "Berat Badan",
                            value = input.weight,
                            onValueChange = { inputViewModel.updateWeight(it) },
                            suffix = "kg",
                            placeholderText = "Contoh: 65"
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // TOMBOL UTAMA BARU
                        PrimaryButton(
                            text = "Hitung Sekarang",
                            onClick = {
                                inputViewModel.calculateBMI { summary -> onNavigateToResult(summary) }
                            },
                            enabled = inputViewModel.canCalculate(),
                            isLoading = isCalculating
                        )
                    }
                }

                Spacer(modifier = Modifier.height(100.dp)) // Padding bawah untuk scroll
            }
        }
    )

    // Name Input Dialog (Sudah diperbaiki sebelumnya)
    if (showNameInput) {
        NameInputDialog(onConfirm = { name -> userViewModel?.saveUserName(name) })
    }
}

@Composable
fun NameInputDialog(onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    // Menggunakan Template ModernDialogContainer
    ModernDialogContainer(onDismiss = {}) { // onDismiss kosong agar user tidak bisa menutup paksa tanpa isi nama
        // 1. Header Icon (Style Seragam)
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(BrandPrimary.copy(alpha = 0.1f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = null,
                tint = BrandPrimary,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 2. Title & Description
        Text(
            text = "Selamat Datang!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Siapa nama panggilanmu?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 3. Input Field (Styling disesuaikan)
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            placeholder = { Text("Contoh: Budi") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = BrandPrimary,
                cursorColor = BrandPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Button
        Button(
            onClick = { if (name.isNotBlank()) onConfirm(name) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BrandPrimary,
                contentColor = Color.White
            )
        ) {
            Text("Mulai Sekarang", style = MaterialTheme.typography.titleMedium)
        }
    }
}