package af.mobile.mybmi.viewmodel

import af.mobile.mybmi.model.BMICheckInput
import af.mobile.mybmi.model.BMICheckSummary
import af.mobile.mybmi.util.ScoringEngine
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InputViewModel @Inject constructor() : ViewModel() {

    private val _input = MutableStateFlow(BMICheckInput())
    val input: StateFlow<BMICheckInput> = _input.asStateFlow()

    private val _isCalculating = MutableStateFlow(false)
    val isCalculating: StateFlow<Boolean> = _isCalculating.asStateFlow()

    // VALIDASI INPUT LEBIH KETAT
    fun updateHeight(value: String) {
        if (isValidInput(value)) {
            _input.value = _input.value.copy(height = value)
        }
    }

    fun updateWeight(value: String) {
        if (isValidInput(value)) {
            _input.value = _input.value.copy(weight = value)
        }
    }

    // Helper: Hanya boleh angka, satu titik, dan tidak boleh spasi/karakter aneh
    private fun isValidInput(value: String): Boolean {
        // Kosong boleh (user menghapus)
        if (value.isEmpty()) return true

        // Cek hanya angka dan titik
        if (value.any { !it.isDigit() && it != '.' }) return false

        // Cek titik tidak boleh lebih dari satu
        if (value.count { it == '.' } > 1) return false

        // Cek titik tidak boleh di awal (misal ".5") - Optional, tergantung selera
        if (value.startsWith('.')) return false

        return true
    }

    fun clearInput() {
        _input.value = BMICheckInput()
    }

    fun canCalculate(): Boolean {
        return _input.value.isValid()
    }

    fun calculateBMI(onResult: (BMICheckSummary) -> Unit) {
        if (!canCalculate()) return

        viewModelScope.launch {
            _isCalculating.value = true

            try {
                // Konversi aman dengan toDoubleOrNull
                // (Meskipun sudah di-prevalidate, double check is good practice)
                val height = _input.value.height.toDoubleOrNull()
                val weight = _input.value.weight.toDoubleOrNull()

                if (height != null && weight != null && height > 0 && weight > 0) {
                    val summary = ScoringEngine.generateSummary(height, weight)
                    onResult(summary)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isCalculating.value = false
            }
        }
    }
}