package af.mobile.mybmi.viewmodel

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : ViewModel() {

    // Kunci untuk menyimpan data
    private val KEY_IS_DARK = booleanPreferencesKey("is_dark_mode")

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    // Fungsi inisialisasi sekarang memantau DataStore
    fun initializeTheme(isSystemDark: Boolean) {
        viewModelScope.launch {
            dataStore.data.map { preferences ->
                // Jika belum ada setting (null), gunakan setting sistem
                preferences[KEY_IS_DARK] ?: isSystemDark
            }.collect { isDark ->
                _isDarkMode.value = isDark
            }
        }
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            // Edit data secara asynchronous
            dataStore.edit { preferences ->
                val current = preferences[KEY_IS_DARK] ?: _isDarkMode.value
                preferences[KEY_IS_DARK] = !current
            }
        }
    }
}