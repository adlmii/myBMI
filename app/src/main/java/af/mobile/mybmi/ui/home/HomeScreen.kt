package af.mobile.mybmi.ui.home

import af.mobile.mybmi.theme.GreenPrimary
import af.mobile.mybmi.viewmodel.InputViewModel
import af.mobile.mybmi.viewmodel.UserViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    onNavigateToResult: (af.mobile.mybmi.model.BMICheckSummary) -> Unit,
    inputViewModel: InputViewModel = viewModel(),
    userViewModel: UserViewModel? = null,
    resultViewModel: af.mobile.mybmi.viewmodel.ResultViewModel? = null
) {
    val input by inputViewModel.input.collectAsState()
    val isCalculating by inputViewModel.isCalculating.collectAsState()
    val currentUser by userViewModel?.currentUser?.collectAsState() ?: remember { mutableStateOf(null) }
    val showNameInput by userViewModel?.showNameInput?.collectAsState() ?: remember { mutableStateOf(false) }
    val isLoading by userViewModel?.isLoading?.collectAsState() ?: remember { mutableStateOf(false) }
    val history by resultViewModel?.history?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
    var inputName by remember { mutableStateOf("") }

    // Get latest BMI from history
    val lastBmi = history.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // User greeting
        if (currentUser != null && !showNameInput) {
            Text(
                text = "Halo, ${currentUser!!.name}! 👋",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                textAlign = TextAlign.Start
            )
        }

        // Title
        Text(
            text = "Pantau BMI Kamu",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Subtitle
        Text(
            text = "Atur kesehatan Anda dengan mengetahui status BMI",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = 20.sp,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Input Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Height Input
                Text(
                    text = "Tinggi Badan (cm)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Start
                )

                OutlinedTextField(
                    value = input.height,
                    onValueChange = { inputViewModel.updateHeight(it) },
                    placeholder = { Text("Contoh: 170") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                    ),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Weight Input
                Text(
                    text = "Berat Badan (kg)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    textAlign = TextAlign.Start
                )

                OutlinedTextField(
                    value = input.weight,
                    onValueChange = { inputViewModel.updateWeight(it) },
                    placeholder = { Text("Contoh: 65") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                        focusedContainerColor = MaterialTheme.colorScheme.background,
                        unfocusedContainerColor = MaterialTheme.colorScheme.background
                    ),
                    singleLine = true,
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Calculate Button
                Button(
                    onClick = {
                        inputViewModel.calculateBMI { summary ->
                            // Save to database immediately with current user ID
                            if (currentUser != null && currentUser!!.id > 0 && resultViewModel != null) {
                                resultViewModel.saveToHistory(summary, currentUser!!.id)
                            }
                            // Navigate to result
                            onNavigateToResult(summary)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    enabled = inputViewModel.canCalculate() && !isCalculating,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 3.dp,
                        pressedElevation = 6.dp
                    )
                ) {
                    if (isCalculating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Hitung BMI",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Info Card - Dynamic tips based on latest BMI
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (lastBmi != null) {
                    when (lastBmi!!.category.name) {
                        "UNDERWEIGHT" -> Color(0xFF3B82F6).copy(alpha = 0.1f)
                        "NORMAL" -> GreenPrimary.copy(alpha = 0.1f)
                        "OVERWEIGHT" -> Color(0xFFF59E0B).copy(alpha = 0.1f)
                        "OBESE" -> Color(0xFFEF4444).copy(alpha = 0.1f)
                        else -> GreenPrimary.copy(alpha = 0.1f)
                    }
                } else {
                    GreenPrimary.copy(alpha = 0.1f)
                }
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = if (lastBmi != null) "📊 Hasil Terakhir" else "💡 Tip Kesehatan",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (lastBmi != null) {
                        when (lastBmi!!.category.name) {
                            "UNDERWEIGHT" -> Color(0xFF3B82F6)
                            "NORMAL" -> GreenPrimary
                            "OVERWEIGHT" -> Color(0xFFF59E0B)
                            "OBESE" -> Color(0xFFEF4444)
                            else -> GreenPrimary
                        }
                    } else {
                        GreenPrimary
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                if (lastBmi != null) {
                    // Show latest BMI info
                    Text(
                        text = "BMI Terakhir: ${String.format("%.1f", lastBmi!!.bmi)} (${lastBmi!!.category.displayName})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = lastBmi!!.category.advice,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                } else {
                    // Show general tip if no history
                    Text(
                        text = "Pertahankan BMI normal antara 18.5-24.9 untuk kesehatan optimal. Konsultasikan dengan dokter jika Anda memiliki kekhawatiran kesehatan.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))
    }

    // Dialog untuk input nama user (hanya tampil jika belum punya nama)
    if (showNameInput) {
        AlertDialog(
            onDismissRequest = {},
            title = {
                Text(
                    text = "Selamat Datang! 👋",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            text = {
                Column {
                    Text(
                        text = "Silakan masukkan nama Anda untuk memulai",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp),
                        lineHeight = 20.sp
                    )
                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        placeholder = { Text("Contoh: Ahmad") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                            focusedContainerColor = MaterialTheme.colorScheme.background
                        ),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (inputName.isNotBlank()) {
                            userViewModel?.saveUserName(inputName)
                        }
                    },
                    enabled = inputName.isNotBlank() && !isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GreenPrimary
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Mulai", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.surface
        )
    }
}

