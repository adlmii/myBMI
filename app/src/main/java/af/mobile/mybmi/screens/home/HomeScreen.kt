package af.mobile.mybmi.screens.home

import af.mobile.mybmi.components.ModernInput
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
import androidx.compose.material.icons.rounded.Calculate
import androidx.compose.material.icons.rounded.HealthAndSafety
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    onNavigateToResult: (BMICheckSummary) -> Unit,
    inputViewModel: InputViewModel = viewModel(),
    userViewModel: UserViewModel? = null,
    resultViewModel: af.mobile.mybmi.viewmodel.ResultViewModel? = null
) {
    val input by inputViewModel.input.collectAsState()
    val isCalculating by inputViewModel.isCalculating.collectAsState()
    val currentUser by userViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }
    val showNameInput by userViewModel?.showNameInput?.collectAsState() ?: remember { mutableStateOf(false) }

    val isDarkMode = MaterialTheme.colorScheme.background.luminance() < 0.5f


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(GradientStart, GradientEnd)
                    ),
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // HEADER
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color.White.copy(alpha = 0.2f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.HealthAndSafety,
                        contentDescription = null,
                        tint = Color.White
                    )
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- TOMBOL HITUNG SEKARANG ---
                    val isButtonEnabled = inputViewModel.canCalculate() && !isCalculating

                    Button(
                        onClick = {
                            inputViewModel.calculateBMI { summary -> onNavigateToResult(summary) }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            // Shadow hanya muncul jika aktif
                            .then(
                                if (isButtonEnabled)
                                    Modifier.shadow(8.dp, spotColor = BrandPrimary.copy(alpha = 0.4f), shape = RoundedCornerShape(16.dp))
                                else Modifier
                            ),

                        // MENGGUNAKAN HELPER DARI COLOR.KT
                        colors = ButtonDefaults.buttonColors(
                            containerColor = getActionButtonContainerColor(isDarkMode, true),
                            contentColor = getActionButtonContentColor(true),
                            disabledContainerColor = getActionButtonContainerColor(isDarkMode, false),
                            disabledContentColor = getActionButtonContentColor(false)
                        ),

                        shape = RoundedCornerShape(16.dp),
                        enabled = isButtonEnabled
                    ) {
                        if (isCalculating) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Icon(Icons.Rounded.Calculate, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Hitung Sekarang", style = MaterialTheme.typography.titleMedium)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // Name Input Dialog
    if (showNameInput) {
        NameInputDialog(
            onConfirm = { name -> userViewModel?.saveUserName(name) }
        )
    }
}

@Composable
fun NameInputDialog(onConfirm: (String) -> Unit) {
    var name by remember { mutableStateOf("") }
    Dialog(onDismissRequest = {}) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Rounded.Person,
                    contentDescription = null,
                    modifier = Modifier.size(56.dp),
                    tint = BrandPrimary
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Siapa namamu?", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nama Panggilan") },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { if (name.isNotBlank()) onConfirm(name) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Mulai")
                }
            }
        }
    }
}