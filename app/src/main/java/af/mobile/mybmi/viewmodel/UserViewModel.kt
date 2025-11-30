package af.mobile.mybmi.viewmodel

import af.mobile.mybmi.database.UserRepository
import af.mobile.mybmi.database.UserEntity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UserViewModel(private val userRepository: UserRepository? = null) : ViewModel() {

    // ... (kode lama: _currentUser, _showNameInput, dll TETAP SAMA) ...
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _showNameInput = MutableStateFlow(false)
    val showNameInput: StateFlow<Boolean> = _showNameInput.asStateFlow()

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
                    val user = userRepository.getLatestUser()
                    if (user != null) {
                        _currentUser.value = user
                        _showNameInput.value = false
                    } else {
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
                // Default value untuk user baru
                userRepository.insertUser(name)
                loadCurrentUser()
            }
        }
    }

    // --- FUNGSI UPDATE BARU ---
    fun updateUserFull(user: UserEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                userRepository?.updateUser(user)
                _currentUser.value = user
            } finally {
                _isLoading.value = false
            }
        }
    }
}