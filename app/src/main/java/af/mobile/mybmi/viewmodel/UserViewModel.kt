package af.mobile.mybmi.viewmodel

import af.mobile.mybmi.database.UserRepository
import af.mobile.mybmi.model.UserProfile
import af.mobile.mybmi.model.toEntity
import af.mobile.mybmi.model.toModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class UserViewModel(private val userRepository: UserRepository? = null) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _showNameInput = MutableStateFlow(false)
    val showNameInput: StateFlow<Boolean> = _showNameInput.asStateFlow()

    // START: Tambahkan state untuk dialog Tanggal Lahir
    private val _showDobInput = MutableStateFlow(false)
    val showDobInput: StateFlow<Boolean> = _showDobInput.asStateFlow()
    // END: Tambahkan state untuk dialog Tanggal Lahir

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadCurrentUser()
    }

    fun loadCurrentUser() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (userRepository != null) {
                    val userEntity = userRepository.getLatestUser()

                    if (userEntity != null) {
                        // CONVERT: Entity -> Model
                        val profile = userEntity.toModel()
                        _currentUser.value = profile

                        // --- LOGIKA CHAINING DIALOG ---

                        // 1. Cek Nama: Jika nama kosong (asumsi hanya terisi saat disave)
                        if (profile.name.isBlank()) {
                            _showNameInput.value = true
                            _showDobInput.value = false // Pastikan DOB disembunyikan
                        }
                        // 2. Jika Nama sudah ada, cek Tanggal Lahir
                        // (birthDate == 0L menandakan tanggal lahir belum diisi,
                        // memerlukan perubahan di UserEntity.kt agar default = 0L)
                        else if (profile.birthDate == 0L) {
                            _showNameInput.value = false // Sembunyikan Nama
                            _showDobInput.value = true   // Tampilkan DOB
                        } else {
                            // Semua sudah diisi
                            _showNameInput.value = false
                            _showDobInput.value = false
                        }
                    } else {
                        // Tidak ada user di database sama sekali
                        _showNameInput.value = true
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveUserName(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            if (userRepository != null) {
                // Simpan nama (Repository akan membuat Entity jika belum ada)
                userRepository.insertUser(name)
                loadCurrentUser() // Reload untuk memicu pengecekan DOB
            }
        }
    }

    // START: Fungsi baru untuk menyimpan Tanggal Lahir Pengguna
    fun saveUserBirthDate(day: Int, month: Int, year: Int) {
        if (userRepository == null) return

        val calendar = Calendar.getInstance()
        // Simpan sebagai Long (Millisecond)
        calendar.set(year, month - 1, day)
        val newBirthDateMillis = calendar.timeInMillis

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentProfile = _currentUser.value ?: return@launch

                // Buat Model baru dengan birthDate yang diupdate
                val updatedProfile = currentProfile.copy(birthDate = newBirthDateMillis)

                // Simpan ke database
                userRepository.updateUser(updatedProfile.toEntity())

                // Update state UI
                _currentUser.value = updatedProfile
                _showDobInput.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }
    // END: Fungsi baru untuk menyimpan Tanggal Lahir Pengguna

    // --- FUNGSI UPDATE DATA LENGKAP ---
    fun updateUserFull(user: UserProfile) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (userRepository != null) {
                    userRepository.updateUser(user.toEntity())
                    _currentUser.value = user
                }
            } finally {
                _isLoading.value = false
            }
        }
    }
}