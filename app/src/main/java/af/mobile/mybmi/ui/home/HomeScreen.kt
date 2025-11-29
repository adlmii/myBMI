package af.mobile.mybmi.ui.home

import af.mobile.mybmi.model.BMICheckSummary
import af.mobile.mybmi.theme.GreenPrimary
import af.mobile.mybmi.theme.getButtonDisabledContainerColor
import af.mobile.mybmi.theme.getButtonDisabledContentColor
import af.mobile.mybmi.theme.getInputPlaceholderColor
import af.mobile.mybmi.viewmodel.InputViewModel
import af.mobile.mybmi.viewmodel.UserViewModel
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
// HAPUS: import androidx.compose.foundation.isSystemInDarkTheme (Tidak dipakai lagi)
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance // PENTING: Tambahkan ini untuk deteksi warna
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
    val isLoading by userViewModel?.isLoading?.collectAsState() ?: remember { mutableStateOf(false) }
    val history by resultViewModel?.history?.collectAsState() ?: remember { mutableStateOf(emptyList()) }

    var inputName by remember { mutableStateOf("") }
    val lastBmi = history.firstOrNull()

    // --- 1. DETEKSI DARK MODE DARI TEMA APLIKASI ---
    val isDarkMode = MaterialTheme.colorScheme.background.luminance() < 0.5f

    // Ambil warna custom dari Color.kt berdasarkan status isDarkMode di atas
    val placeholderColor = getInputPlaceholderColor(isDarkMode)
    val disabledButtonContainer = getButtonDisabledContainerColor(isDarkMode)
    val disabledButtonContent = getButtonDisabledContentColor(isDarkMode)

    // --- MAIN CONTAINER ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 2. HEADER BACKGROUND
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            GreenPrimary,
                            GreenPrimary.copy(alpha = 0.8f)
                        )
                    ),
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
        )

        // 3. CONTENT SCROLLABLE
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(60.dp))

            // --- GREETING SECTION ---
            if (currentUser != null && !showNameInput) {
                Column(horizontalAlignment = Alignment.Start, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "Halo, ${currentUser!!.name}!",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cek Kesehatanmu",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        lineHeight = 34.sp
                    )
                    Text(
                        text = "Hari Ini 🌿",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        lineHeight = 34.sp
                    )
                }
            } else {
                Text(
                    text = "Pantau BMI Kamu",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- INPUT CARD ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Height Input
                    ModernInput(
                        label = "Tinggi Badan",
                        value = input.height,
                        onValueChange = { inputViewModel.updateHeight(it) },
                        suffix = "cm",
                        placeholderText = "0",
                        placeholderColor = placeholderColor // Warna mengikuti tema app
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Weight Input
                    ModernInput(
                        label = "Berat Badan",
                        value = input.weight,
                        onValueChange = { inputViewModel.updateWeight(it) },
                        suffix = "kg",
                        placeholderText = "0",
                        placeholderColor = placeholderColor // Warna mengikuti tema app
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // CALCULATE BUTTON
                    Button(
                        onClick = {
                            inputViewModel.calculateBMI { summary ->
                                onNavigateToResult(summary)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp),
                        enabled = inputViewModel.canCalculate() && !isCalculating,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary,
                            contentColor = Color.White,
                            disabledContainerColor = disabledButtonContainer,
                            disabledContentColor = disabledButtonContent
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (isCalculating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                        } else {
                            Text(
                                text = "Hitung Sekarang",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TIPS CARD ---
            val cardBorderColor = if (lastBmi != null) {
                when (lastBmi.category.name) {
                    "UNDERWEIGHT" -> Color(0xFF3B82F6)
                    "NORMAL" -> GreenPrimary
                    "OVERWEIGHT" -> Color(0xFFF59E0B)
                    "OBESE" -> Color(0xFFEF4444)
                    else -> GreenPrimary
                }
            } else {
                GreenPrimary
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, cardBorderColor.copy(alpha = 0.3f)),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "💡 Tips Kesehatan",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = cardBorderColor,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (lastBmi != null) {
                        Text(
                            text = lastBmi.category.advice,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.Normal
                        )
                    } else {
                        Text(
                            text = "Silakan lakukan pengecekan BMI pertama Anda untuk mendapatkan tips kesehatan yang sesuai.",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 22.sp,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(100.dp))
        }
    }

    // --- DIALOG INPUT NAMA ---
    if (showNameInput) {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Avatar Icon
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(Color.White, shape = CircleShape)
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(GreenPrimary.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Welcome Icon",
                            tint = GreenPrimary,
                            modifier = Modifier.size(48.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "Selamat Datang!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Supaya lebih akrab, boleh tau siapa nama panggilanmu?",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Input Nama Dialog
                    OutlinedTextField(
                        value = inputName,
                        onValueChange = { inputName = it },
                        placeholder = {
                            Text(
                                text = "Nama Panggilan",
                                color = placeholderColor, // Warna custom
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GreenPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
                            // OVERRIDE PLACEHOLDER COLOR
                            focusedPlaceholderColor = placeholderColor,
                            unfocusedPlaceholderColor = placeholderColor
                        ),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Button Dialog
                    Button(
                        onClick = {
                            if (inputName.isNotBlank()) {
                                userViewModel?.saveUserName(inputName)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        elevation = ButtonDefaults.buttonElevation(0.dp),
                        enabled = inputName.isNotBlank() && !isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GreenPrimary,
                            contentColor = Color.White,
                            disabledContainerColor = disabledButtonContainer,
                            disabledContentColor = disabledButtonContent
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                        } else {
                            Text(
                                text = "Mulai Sekarang",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- REUSABLE COMPONENT: Modern Input Field ---
@Composable
fun ModernInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    suffix: String,
    placeholderText: String,
    placeholderColor: Color // Parameter Warna
) {
    Column {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(
                    text = placeholderText,
                    color = placeholderColor // Pakai warna parameter
                )
            },
            suffix = {
                Text(
                    text = suffix,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GreenPrimary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                // OVERRIDE PLACEHOLDER COLOR
                focusedPlaceholderColor = placeholderColor,
                unfocusedPlaceholderColor = placeholderColor
            ),
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        )
    }
}