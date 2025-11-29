package af.mobile.mybmi.viewmodel

import af.mobile.mybmi.database.BMIRepository
import af.mobile.mybmi.model.BMICheckSummary
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultViewModel(
    private val bmiRepository: BMIRepository? = null,
    private val currentUserId: Int = 0
) : ViewModel() {

    // Current result yang sedang ditampilkan
    private val _currentResult = MutableStateFlow<BMICheckSummary?>(null)
    val currentResult: StateFlow<BMICheckSummary?> = _currentResult.asStateFlow()

    // History list
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

    fun saveToHistory(summary: BMICheckSummary, userId: Int = 0) {
        viewModelScope.launch {
            // Use provided userId, fallback to currentUserId if not provided
            val idToUse = if (userId > 0) userId else currentUserId
            if (bmiRepository != null && idToUse > 0) {
                bmiRepository.saveBMI(idToUse, summary)
            }
            val currentList = _history.value.toMutableList()
            // Add ke awal list (newest first)
            currentList.add(0, summary)
            _history.value = currentList
        }
    }

    fun loadHistory(userId: Int) {
        if (bmiRepository != null) {
            viewModelScope.launch {
                bmiRepository.getBMIHistoryByUser(userId).collect { historyList ->
                    _history.value = historyList
                }
            }
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
            val summaryToDelete = _history.value.find { it.id == id }
            if (summaryToDelete != null && bmiRepository != null) {
                bmiRepository.deleteBMI(summaryToDelete)
            }
            _history.value = _history.value.filter { it.id != id }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            if (bmiRepository != null && currentUserId > 0) {
                bmiRepository.deleteBMIByUserId(currentUserId)
            }
            _history.value = emptyList()
        }
    }
}