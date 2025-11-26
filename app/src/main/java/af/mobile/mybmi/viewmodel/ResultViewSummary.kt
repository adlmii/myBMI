package af.mobile.mybmi.viewmodel

import af.mobile.mybmi.model.BMICheckSummary
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultViewModel : ViewModel() {

    // Current result yang sedang ditampilkan
    private val _currentResult = MutableStateFlow<BMICheckSummary?>(null)
    val currentResult: StateFlow<BMICheckSummary?> = _currentResult.asStateFlow()

    // History list (in-memory for now, nanti bisa diganti database)
    private val _history = MutableStateFlow<List<BMICheckSummary>>(emptyList())
    val history: StateFlow<List<BMICheckSummary>> = _history.asStateFlow()

    // Selected history untuk detail dialog
    private val _selectedHistory = MutableStateFlow<BMICheckSummary?>(null)
    val selectedHistory: StateFlow<BMICheckSummary?> = _selectedHistory.asStateFlow()

    fun setCurrentResult(summary: BMICheckSummary) {
        _currentResult.value = summary
    }

    fun clearCurrentResult() {
        _currentResult.value = null
    }

    fun saveToHistory(summary: BMICheckSummary) {
        viewModelScope.launch {
            val currentList = _history.value.toMutableList()
            // Add ke awal list (newest first)
            currentList.add(0, summary)
            _history.value = currentList
        }
    }

    fun selectHistory(summary: BMICheckSummary) {
        _selectedHistory.value = summary
    }

    fun clearSelectedHistory() {
        _selectedHistory.value = null
    }

    fun deleteHistory(id: String) {
        viewModelScope.launch {
            _history.value = _history.value.filter { it.id != id }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            _history.value = emptyList()
        }
    }
}