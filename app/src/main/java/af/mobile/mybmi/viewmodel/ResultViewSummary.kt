package af.mobile.mybmi.viewmodel

import af.mobile.mybmi.database.BMIDao
import af.mobile.mybmi.database.BMIRepository
import af.mobile.mybmi.database.BadgeDao
import af.mobile.mybmi.model.BMICheckSummary
import af.mobile.mybmi.model.Badge
import af.mobile.mybmi.util.BadgeManager
import af.mobile.mybmi.util.StreakUtils
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val bmiRepository: BMIRepository,
    private val badgeDao: BadgeDao,
    private val bmiDao: BMIDao
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

    // Badge State
    private val _newlyUnlockedBadges = MutableStateFlow<List<Badge>>(emptyList())
    val newlyUnlockedBadges: StateFlow<List<Badge>> = _newlyUnlockedBadges.asStateFlow()

    // --- STREAK STATE ---
    val streakCount: StateFlow<Int> = _history.map { list ->
        StreakUtils.calculateMonthlyStreak(list)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun dismissBadge() {
        val currentList = _newlyUnlockedBadges.value
        if (currentList.isNotEmpty()) {
            _newlyUnlockedBadges.value = currentList.drop(1)
        }
    }

    fun setCurrentResult(summary: BMICheckSummary) {
        _currentResult.value = summary
    }

    fun clearCurrentResult() {
        _currentResult.value = null
    }

    fun saveToHistory(summary: BMICheckSummary, userId: Int) {
        if (userId <= 0) return

        viewModelScope.launch {
            // 1. Simpan Data BMI
            bmiRepository.saveBMI(userId, summary)

            // 2. CEK BADGE
            val badgeManager = BadgeManager(badgeDao, bmiDao)
            val newBadges = badgeManager.checkNewBadges(userId, summary)

            if (newBadges.isNotEmpty()) {
                _newlyUnlockedBadges.value = newBadges
            }

            // Refresh list manual (Optimistic Update)
            val currentList = _history.value.toMutableList()
            currentList.add(0, summary)
            _history.value = currentList
        }
    }

    fun loadHistory(userId: Int) {
        viewModelScope.launch {
            bmiRepository.getBMIHistoryByUser(userId).collect { historyList ->
                _history.value = historyList
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
            bmiRepository.deleteBMI(id)
            // Filter list lokal agar UI update instan
            _history.value = _history.value.filter { it.id != id }
        }
    }
}