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

    // Current user yang sedang login
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    // Flag untuk show dialog input nama
    private val _showNameInput = MutableStateFlow(false)
    val showNameInput: StateFlow<Boolean> = _showNameInput.asStateFlow()

    // Flag untuk loading
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
                } else {
                    _showNameInput.value = true
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun saveUserName(name: String) {
        if (name.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (userRepository != null) {
                    userRepository.insertUser(name)
                    val user = userRepository.getLatestUser()
                    if (user != null) {
                        _currentUser.value = user
                        _showNameInput.value = false
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateUserName(name: String) {
        if (name.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true
            try {
                val user = _currentUser.value
                if (user != null && userRepository != null) {
                    val updatedUser = user.copy(
                        name = name,
                        updatedAt = System.currentTimeMillis()
                    )
                    userRepository.updateUser(updatedUser)
                    _currentUser.value = updatedUser
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun hideNameInput() {
        _showNameInput.value = false
    }
}

