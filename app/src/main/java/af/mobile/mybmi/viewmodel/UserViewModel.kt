package af.mobile.mybmi.viewmodel

import af.mobile.mybmi.database.BadgeDao // Tambah
import af.mobile.mybmi.database.UserBadgeEntity // Tambah
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

// UPDATE KONSTRUKTOR: Tambahkan badgeDao
class UserViewModel(
    private val userRepository: UserRepository? = null,
    private val badgeDao: BadgeDao? = null
) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    // STATE BARU: List Badge yang dimiliki User
    private val _userBadges = MutableStateFlow<List<UserBadgeEntity>>(emptyList())
    val userBadges: StateFlow<List<UserBadgeEntity>> = _userBadges.asStateFlow()

    private val _showNameInput = MutableStateFlow(false)
    val showNameInput: StateFlow<Boolean> = _showNameInput.asStateFlow()

    private val _showDobInput = MutableStateFlow(false)
    val showDobInput: StateFlow<Boolean> = _showDobInput.asStateFlow()

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
                        val profile = userEntity.toModel()
                        _currentUser.value = profile

                        // LOAD BADGES SAAT USER TERDETEKSI
                        loadUserBadges(profile.id)

                        // ... (Logika Dialog Nama/DOB tetap sama) ...
                        if (profile.name.isBlank()) {
                            _showNameInput.value = true
                            _showDobInput.value = false
                        } else if (profile.birthDate == 0L) {
                            _showNameInput.value = false
                            _showDobInput.value = true
                        } else {
                            _showNameInput.value = false
                            _showDobInput.value = false
                        }
                    } else {
                        _showNameInput.value = true
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    // FUNGSI BARU: Ambil Badge dari Database
    private fun loadUserBadges(userId: Int) {
        if (badgeDao != null) {
            viewModelScope.launch {
                badgeDao.getUserBadges(userId).collect { badges ->
                    _userBadges.value = badges
                }
            }
        }
    }

    // ... (Sisa fungsi saveUserName, saveUserBirthDate, updateUserFull BIARKAN SAMA) ...
    // Copy paste kode lama di bawah sini
    fun saveUserName(name: String) {
        if (name.isBlank()) return
        viewModelScope.launch {
            if (userRepository != null) {
                userRepository.insertUser(name)
                loadCurrentUser()
            }
        }
    }

    fun saveUserBirthDate(day: Int, month: Int, year: Int) {
        if (userRepository == null) return
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, day)
        val newBirthDateMillis = calendar.timeInMillis

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val currentProfile = _currentUser.value ?: return@launch
                val updatedProfile = currentProfile.copy(birthDate = newBirthDateMillis)
                userRepository.updateUser(updatedProfile.toEntity())
                _currentUser.value = updatedProfile
                _showDobInput.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

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