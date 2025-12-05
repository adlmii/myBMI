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

    // [BARU] State untuk menampung pesan error
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    fun updateHeight(value: String) {
        // Cek format saat ngetik (cegah karakter aneh)
        if (isValidFormat(value)) {
            _input.value = _input.value.copy(height = value)
            _errorMessage.value = null // Hapus error kalau user mulai ngetik lagi
        }
    }

    fun updateWeight(value: String) {
        if (isValidFormat(value)) {
            _input.value = _input.value.copy(weight = value)
            _errorMessage.value = null // Hapus error kalau user mulai ngetik lagi
        }
    }

    // 1. Validasi Format (Dijalankan SETIAP KETIK)
    private fun isValidFormat(value: String): Boolean {
        // Kosong boleh (user menghapus)
        if (value.isEmpty()) return true

        // Batasi panjang karakter (misal max 6 digit agar tidak overflow UI)
        if (value.length > 6) return false

        // Cek hanya angka dan titik
        if (value.any { !it.isDigit() && it != '.' }) return false

        // Cek titik tidak boleh lebih dari satu
        if (value.count { it == '.' } > 1) return false

        // Cek titik tidak boleh di awal (misal ".5")
        if (value.startsWith('.')) return false

        return true
    }

    // 2. Validasi Logika (Dijalankan SAAT KLIK HITUNG)
    private fun isRealisticInput(height: Double, weight: Double): Boolean {
        // Range Tinggi: 50 cm - 300 cm
        if (height < 50 || height > 300) {
            _errorMessage.value = "Tinggi badan tidak wajar (50 - 300 cm)"
            return false
        }
        // Range Berat: 2 kg - 600 kg
        if (weight < 2 || weight > 600) {
            _errorMessage.value = "Berat badan tidak wajar (2 - 600 kg)"
            return false
        }
        return true
    }

    fun clearInput() {
        _input.value = BMICheckInput()
        _errorMessage.value = null
    }

    fun canCalculate(): Boolean {
        // Tombol aktif jika field tidak kosong
        return _input.value.height.isNotEmpty() && _input.value.weight.isNotEmpty()
    }

    fun calculateBMI(onResult: (BMICheckSummary) -> Unit) {
        val hStr = _input.value.height
        val wStr = _input.value.weight

        val h = hStr.toDoubleOrNull()
        val w = wStr.toDoubleOrNull()

        // Cek 1: Apakah angka valid?
        if (h == null || w == null) {
            _errorMessage.value = "Mohon masukkan angka yang valid"
            return
        }

        // Cek 2: Apakah angka masuk akal?
        if (!isRealisticInput(h, w)) {
            return // Stop, jangan hitung
        }

        // Jika lolos semua cek, baru hitung
        viewModelScope.launch {
            _isCalculating.value = true

            try {
                val summary = ScoringEngine.generateSummary(h, w)
                onResult(summary)
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Terjadi kesalahan: ${e.message}"
            } finally {
                _isCalculating.value = false
            }
        }
    }
}