package af.mobile.mybmi.screens.profile

import af.mobile.mybmi.components.GenderChip
import af.mobile.mybmi.components.ModernClickableInput
import af.mobile.mybmi.components.ModernInput
import af.mobile.mybmi.theme.*
import af.mobile.mybmi.util.ImageUtils
import af.mobile.mybmi.viewmodel.UserViewModel
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    onNavigateBack: () -> Unit,
    userViewModel: UserViewModel = viewModel()
) {
    val currentUser by userViewModel.currentUser.collectAsState()
    val context = LocalContext.current

    // Deteksi Dark Mode untuk helper warna
    val isDarkMode = MaterialTheme.colorScheme.background.luminance() < 0.5f

    // --- STATE FORM ---
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Laki-laki") }
    var birthDateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var profileImagePath by remember { mutableStateOf<String?>(null) }

    // State Awal (untuk cek perubahan)
    var initialName by remember { mutableStateOf("") }
    var initialGender by remember { mutableStateOf("") }
    var initialBirthDate by remember { mutableStateOf(0L) }
    var initialImagePath by remember { mutableStateOf<String?>(null) }
    var isDataLoaded by remember { mutableStateOf(false) }

    // Dialog State
    var showDatePicker by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = birthDateMillis)

    // Load Data
    LaunchedEffect(currentUser) {
        if (!isDataLoaded && currentUser != null) {
            name = currentUser!!.name
            gender = currentUser!!.gender
            birthDateMillis = currentUser!!.birthDate
            profileImagePath = currentUser!!.profileImagePath

            initialName = name
            initialGender = gender
            initialBirthDate = birthDateMillis
            initialImagePath = profileImagePath
            isDataLoaded = true
        }
    }

    val hasChanges = remember(name, gender, birthDateMillis, profileImagePath) {
        name != initialName || gender != initialGender ||
                birthDateMillis != initialBirthDate || profileImagePath != initialImagePath
    }

    BackHandler(enabled = hasChanges) { showUnsavedDialog = true }

    // --- LAUNCHERS ---
    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { uri ->
                profileImagePath = ImageUtils.saveImageToInternalStorage(context, uri)
            }
        }
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let {
            val cropOptions = CropImageContractOptions(
                it,
                CropImageOptions(
                    imageSourceIncludeGallery = false,
                    imageSourceIncludeCamera = false,
                    cropShape = CropImageView.CropShape.OVAL,
                    fixAspectRatio = true,
                    aspectRatioX = 1,
                    aspectRatioY = 1,

                    toolbarColor = android.graphics.Color.parseColor("#00C9A7"),
                    activityMenuIconColor = android.graphics.Color.WHITE,
                    activityBackgroundColor = android.graphics.Color.parseColor("#121212")
                )
            )
            imageCropLauncher.launch(cropOptions)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            Toast.makeText(context, "Izin akses galeri dibutuhkan", Toast.LENGTH_SHORT).show()
        }
    }

    fun checkPermissionAndOpenGallery() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            permissionLauncher.launch(permission)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // 1. HEADER GRADIENT
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    brush = Brush.verticalGradient(colors = listOf(GradientStart, GradientEnd)),
                    shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                )
        )

        // 2. FORM SCROLLABLE
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // HEADER TITLE (Manual TopBar agar menyatu dengan gradient)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (hasChanges) showUnsavedDialog = true else onNavigateBack() }) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "Edit Profil",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // PHOTO PROFILE (Floating)
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface) // Border luar
                        .padding(4.dp) // Ketebalan border
                        .clip(CircleShape)
                        .background(BrandPrimary.copy(alpha = 0.1f))
                        .clickable { checkPermissionAndOpenGallery() },
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImagePath != null) {
                        Image(
                            painter = rememberAsyncImagePainter(model = File(profileImagePath!!)),
                            contentDescription = "Profile Photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = BrandPrimary
                        )
                    }
                }

                // Edit Icon Badge
                Box(
                    modifier = Modifier
                        .offset(x = 4.dp, y = 4.dp)
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(BrandSecondary)
                        .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
                        .clickable { checkPermissionAndOpenGallery() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Change Photo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CARD FORM
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .shadow(8.dp, spotColor = Color.Black.copy(alpha = 0.05f), shape = RoundedCornerShape(24.dp)),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    ModernInput(
                        label = "Nama Panggilan",
                        value = name,
                        onValueChange = { name = it },
                        suffix = "",
                        placeholderText = "Nama Anda"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Jenis Kelamin",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 8.dp, start = 4.dp)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GenderChip(
                            text = "Laki-laki",
                            icon = Icons.Rounded.Male,
                            isSelected = gender == "Laki-laki",
                            onClick = { gender = "Laki-laki" },
                            modifier = Modifier.weight(1f)
                        )
                        GenderChip(
                            text = "Perempuan",
                            icon = Icons.Rounded.Female,
                            isSelected = gender == "Perempuan",
                            onClick = { gender = "Perempuan" },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))
                    val dateString = sdf.format(Date(birthDateMillis))

                    ModernClickableInput(
                        label = "Tanggal Lahir",
                        value = dateString,
                        onClick = { showDatePicker = true }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // SAVE BUTTON
            Button(
                onClick = {
                    if (name.isNotBlank() && currentUser != null) {
                        val updatedUser = currentUser!!.copy(
                            name = name,
                            gender = gender,
                            birthDate = birthDateMillis,
                            profileImagePath = profileImagePath,
                            updatedAt = System.currentTimeMillis()
                        )
                        userViewModel.updateUserFull(updatedUser)
                        Toast.makeText(context, "Profil Berhasil Diperbarui", Toast.LENGTH_SHORT).show()
                        onNavigateBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(56.dp),

                colors = ButtonDefaults.buttonColors(
                    containerColor = getActionButtonContainerColor(isDarkMode, hasChanges),
                    contentColor = getActionButtonContentColor(hasChanges),
                    disabledContainerColor = getActionButtonContainerColor(isDarkMode, false),
                    disabledContentColor = getActionButtonContentColor(false)
                ),
                shape = RoundedCornerShape(16.dp),
                enabled = hasChanges
            ) {
                Icon(Icons.Rounded.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Simpan Perubahan", style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
    }

    // --- DIALOGS ---
    if (showUnsavedDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedDialog = false },
            title = { Text("Batalkan Perubahan?") },
            text = { Text("Anda memiliki perubahan yang belum disimpan. Yakin ingin kembali?") },
            containerColor = MaterialTheme.colorScheme.surface,
            confirmButton = {
                Button(
                    onClick = { showUnsavedDialog = false; onNavigateBack() },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusObese)
                ) { Text("Ya, Keluar", color = Color.White) }
            },
            dismissButton = {
                TextButton(onClick = { showUnsavedDialog = false }) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { birthDateMillis = it }
                    showDatePicker = false
                }) { Text("Pilih", color = BrandPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Batal", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        ) { DatePicker(state = datePickerState) }
    }
}