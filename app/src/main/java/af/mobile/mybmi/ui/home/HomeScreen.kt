package af.mobile.mybmi.ui.home

import af.mobile.mybmi.theme.Gray200
import af.mobile.mybmi.theme.GreenPrimary
import af.mobile.mybmi.viewmodel.InputViewModel
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(
    onNavigateToResult: () -> Unit,
    inputViewModel: InputViewModel = viewModel()
) {
    val input by inputViewModel.input.collectAsState()
    val isCalculating by inputViewModel.isCalculating.collectAsState()

    // Use background color from MaterialTheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // IMPORTANT: Follows theme background (Dark/Light)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Avatar section
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(GreenPrimary.copy(alpha = 0.15f), shape = RoundedCornerShape(60.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("👤", fontSize = 56.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Title & Subtitle
        Text(
            text = "Pantau BMI Kamu",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground, // AUTOMATIC: Black in Light, White in Dark
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Hitung dan monitor kesehatan badan Anda dengan mudah",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant, // AUTOMATIC: Dark Gray in Light, Light Gray in Dark
            textAlign = TextAlign.Center,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Input Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface // AUTOMATIC: White in Light, Medium Gray in Dark
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(28.dp)
            ) {
                Text(
                    text = "Hitung BMI Kamu",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface, // Text inside card
                    lineHeight = 30.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Input Height
                Text(
                    text = "Tinggi Badan",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = input.height,
                    onValueChange = { inputViewModel.updateHeight(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Contoh: 170 cm", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = Gray200,
                        focusedContainerColor = MaterialTheme.colorScheme.surface, // Matches card color
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface, // User input text
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    textStyle = TextStyle(fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Input Weight
                Text(
                    text = "Berat Badan",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 10.dp)
                )

                OutlinedTextField(
                    value = input.weight,
                    onValueChange = { inputViewModel.updateWeight(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text("Contoh: 65 kg", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GreenPrimary,
                        unfocusedBorderColor = Gray200,
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    textStyle = TextStyle(fontSize = 14.sp)
                )

                Spacer(modifier = Modifier.height(28.dp))

                // Calculate Button
                Button(
                    onClick = {
                        inputViewModel.calculateBMI { summary ->
                            // Save to ResultViewModel via MainActivity
                            onNavigateToResult()
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
                            text = "Cek Hasilnya",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            lineHeight = 24.sp
                        )
                    }
                }
            }
        }
    }
}