package af.mobile.mybmi.viewmodel

import af.mobile.mybmi.database.BMIDao
import af.mobile.mybmi.database.BMIRepository
import af.mobile.mybmi.database.BadgeDao
import af.mobile.mybmi.model.BMICheckSummary
import af.mobile.mybmi.model.Badge
import af.mobile.mybmi.util.BadgeManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultViewModel(
    private val bmiRepository: BMIRepository? = null,
    private val badgeDao: BadgeDao? = null,
    private val bmiDao: BMIDao? = null,
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

    // --- STATE BARU UNTUK BADGE ---
    private val _newlyUnlockedBadges = MutableStateFlow<List<Badge>>(emptyList())
    val newlyUnlockedBadges: StateFlow<List<Badge>> = _newlyUnlockedBadges.asStateFlow()

    fun clearNewBadges() {
        _newlyUnlockedBadges.value = emptyList()
    }

    fun setCurrentResult(summary: BMICheckSummary) {
        _currentResult.value = summary
    }

    fun clearCurrentResult() {
        _currentResult.value = null
    }

    fun saveToHistory(summary: BMICheckSummary, userId: Int = 0) {
        viewModelScope.launch {
            val idToUse = if (userId > 0) userId else currentUserId

            if (bmiRepository != null && idToUse > 0) {
                // 1. Simpan Data BMI
                bmiRepository.saveBMI(idToUse, summary)

                // 2. CEK BADGE (LOGIKA BARU)
                if (badgeDao != null && bmiDao != null) {
                    val badgeManager = BadgeManager(badgeDao, bmiDao)
                    val newBadges = badgeManager.checkNewBadges(idToUse, summary)

                    if (newBadges.isNotEmpty()) {
                        _newlyUnlockedBadges.value = newBadges
                    }
                }
            }

            // Refresh list manual (Optimistic Update)
            val currentList = _history.value.toMutableList()
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

    // --- FUNGSI HAPUS ---
    fun deleteHistory(id: String) {
        viewModelScope.launch {
            // 1. Hapus dari Database (Permanen)
            if (bmiRepository != null) {
                bmiRepository.deleteBMI(id)
            }

            // 2. Hapus dari List di Layar (Sementara/Optimistic Update)
            _history.value = _history.value.filter { it.id != id }
        }
    }
}