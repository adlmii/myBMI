package af.mobile.mybmi.screens.profile

import af.mobile.mybmi.components.GenderChip
import af.mobile.mybmi.components.ImageSourceOption
import af.mobile.mybmi.components.ModernAlertDialog
import af.mobile.mybmi.components.ModernDialogContainer
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
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.graphics.toColorInt
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
    val isDarkMode = MaterialTheme.colorScheme.background.luminance() < 0.5f

    // --- STATE FORM ---
    var name by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Laki-laki") }

    // Menggunakan mutableLongStateOf
    var birthDateMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }

    var profileImagePath by remember { mutableStateOf<String?>(null) }

    // State Awal (untuk cek perubahan)
    var initialName by remember { mutableStateOf("") }
    var initialGender by remember { mutableStateOf("") }

    // Menggunakan mutableLongStateOf (0L sebagai marker 'belum diisi')
    var initialBirthDate by remember { mutableLongStateOf(0L) }

    var initialImagePath by remember { mutableStateOf<String?>(null) }
    var isDataLoaded by remember { mutableStateOf(false) }

    // Dialog State
    var showDatePicker by remember { mutableStateOf(false) }
    var showUnsavedDialog by remember { mutableStateOf(false) }

    // --- STATE KAMERA & GALERI ---
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var tempCameraUri by remember { mutableStateOf<Uri?>(null) }

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

    // --- 1. CONFIG CROPPER ---
    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            result.uriContent?.let { uri ->
                profileImagePath = ImageUtils.saveImageToInternalStorage(context, uri)
            }
        }
    }

    fun launchCropper(uri: Uri) {
        val cropOptions = CropImageContractOptions(
            uri,
            CropImageOptions(
                imageSourceIncludeGallery = false,
                imageSourceIncludeCamera = false,
                cropShape = CropImageView.CropShape.OVAL,
                fixAspectRatio = true,
                aspectRatioX = 1,
                aspectRatioY = 1,
                toolbarColor = "#00C9A7".toColorInt(),
                activityMenuIconColor = android.graphics.Color.WHITE,
                activityBackgroundColor = "#121212".toColorInt()
            )
        )
        imageCropLauncher.launch(cropOptions)
    }

    // --- 2. CONFIG LAUNCHERS ---
    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
        uri?.let { launchCropper(it) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && tempCameraUri != null) {
            launchCropper(tempCameraUri!!)
        }
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            Toast.makeText(context, "Izin akses galeri dibutuhkan", Toast.LENGTH_SHORT).show()
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            val uri = ImageUtils.getTempUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            Toast.makeText(context, "Izin kamera dibutuhkan", Toast.LENGTH_SHORT).show()
        }
    }

    // --- 3. LOGIKA KLIK UTAMA ---
    fun onGalleryClick() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            galleryPermissionLauncher.launch(permission)
        }
    }

    fun onCameraClick() {
        val permission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            val uri = ImageUtils.getTempUri(context)
            tempCameraUri = uri
            cameraLauncher.launch(uri)
        } else {
            cameraPermissionLauncher.launch(permission)
        }
    }

    // --- UI START ---
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
            // HEADER TITLE
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { if (hasChanges) showUnsavedDialog = true else onNavigateBack() }) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
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
                        .clickable { showImageSourceDialog = true }, // TRIGGER DIALOG
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
                        .clickable { showImageSourceDialog = true }, // TRIGGER DIALOG
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

                    val localeID = Locale.forLanguageTag("id-ID")
                    val sdf = SimpleDateFormat("dd MMMM yyyy", localeID)

                    val dateString = if (birthDateMillis == 0L) {
                        ""
                    } else {
                        sdf.format(Date(birthDateMillis))
                    }

                    ModernClickableInput(
                        label = "Tanggal Lahir",
                        value = dateString,
                        // Menambahkan placeholder untuk UX yang lebih baik
                        placeholderText = "Pilih tanggal lahir Anda",
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
                            profileImagePath = profileImagePath
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

    // 1. IMAGE SOURCE DIALOG
    if (showImageSourceDialog) {
        ModernDialogContainer(onDismiss = { showImageSourceDialog = false }) {
            Text(
                text = "Ubah Foto Profil",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Pilih foto baru dari galeri atau ambil langsung dengan kamera.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            ImageSourceOption(
                icon = Icons.Rounded.CameraAlt,
                title = "Ambil Foto",
                subtitle = "Gunakan kamera",
                onClick = {
                    showImageSourceDialog = false
                    onCameraClick()
                }
            )

            Spacer(modifier = Modifier.height(12.dp))

            ImageSourceOption(
                icon = Icons.Rounded.PhotoLibrary,
                title = "Pilih dari Galeri",
                subtitle = "Cari di penyimpanan",
                onClick = {
                    showImageSourceDialog = false
                    onGalleryClick()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            TextButton(
                onClick = { showImageSourceDialog = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "Batal",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }

    // 2. UNSAVED DIALOG
    if (showUnsavedDialog) {
        ModernAlertDialog(
            onDismiss = { showUnsavedDialog = false },
            title = "Batalkan Perubahan?",
            description = "Anda memiliki perubahan yang belum disimpan. Yakin ingin kembali?",
            icon = Icons.Rounded.EditOff,
            mainColor = StatusObese,
            positiveText = "Ya, Keluar",
            onPositive = {
                showUnsavedDialog = false
                onNavigateBack()
            }
        )
    }

    // 3. DATE PICKER
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