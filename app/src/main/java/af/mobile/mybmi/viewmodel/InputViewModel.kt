package af.mobile.mybmi.viewmodel

import af.mobile.mybmi.model.BMICheckInput
import af.mobile.mybmi.model.BMICheckSummary
import af.mobile.mybmi.util.ScoringEngine
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InputViewModel : ViewModel() {

    private val _input = MutableStateFlow(BMICheckInput())
    val input: StateFlow<BMICheckInput> = _input.asStateFlow()

    private val _isCalculating = MutableStateFlow(false)
    val isCalculating: StateFlow<Boolean> = _isCalculating.asStateFlow()

    fun updateHeight(value: String) {
        // Filter hanya angka dan titik
        val filtered = value.filter { it.isDigit() || it == '.' }
        _input.value = _input.value.copy(height = filtered)
    }

    fun updateWeight(value: String) {
        // Filter hanya angka dan titik
        val filtered = value.filter { it.isDigit() || it == '.' }
        _input.value = _input.value.copy(weight = filtered)
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
                val height = _input.value.height.toDouble()
                val weight = _input.value.weight.toDouble()

                val summary = ScoringEngine.generateSummary(height, weight)
                onResult(summary)
            } catch (e: Exception) {
                // Handle error jika perlu
                e.printStackTrace()
            } finally {
                _isCalculating.value = false
            }
        }
    }
}