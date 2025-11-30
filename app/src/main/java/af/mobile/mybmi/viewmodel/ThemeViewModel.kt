package af.mobile.mybmi.viewmodel

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ThemeViewModel(application: Application) : AndroidViewModel(application) {

    private val PREFS_NAME = "mybmi_theme_prefs"
    private val KEY_IS_DARK = "is_dark_mode"

    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode

    private var isInitialized = false

    fun initializeTheme(isSystemDark: Boolean) {
        if (isInitialized) return

        if (prefs.contains(KEY_IS_DARK)) {
            _isDarkMode.value = prefs.getBoolean(KEY_IS_DARK, false)
        } else {
            _isDarkMode.value = isSystemDark
        }
        isInitialized = true
    }

    fun toggleDarkMode() {
        val newValue = !_isDarkMode.value
        _isDarkMode.value = newValue

        // LEBIH BERSIH: Menggunakan KTX extension
        // Blok ini otomatis melakukan .apply() setelah selesai
        prefs.edit {
            putBoolean(KEY_IS_DARK, newValue)
        }
    }
}