package af.mobile.mybmi.viewmodel

import af.mobile.mybmi.database.BMIRepository
import af.mobile.mybmi.database.StreakRepository
import af.mobile.mybmi.model.BMICheckSummary
import af.mobile.mybmi.model.Badge
import af.mobile.mybmi.util.BadgeManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: BMIRepository,
    private val streakRepository: StreakRepository,
    private val badgeManager: BadgeManager
) : ViewModel() {

    // --- STATE FLOW HISTORY ---
    private val _history = MutableStateFlow<List<BMICheckSummary>>(emptyList())
    val history: StateFlow<List<BMICheckSummary>> = _history.asStateFlow()

    // --- STATE FLOW BADGE ---
    private val _newlyUnlockedBadges = MutableStateFlow<List<Badge>>(emptyList())
    val newlyUnlockedBadges: StateFlow<List<Badge>> = _newlyUnlockedBadges.asStateFlow()

    // --- STATE FLOW STREAK ---
    val streakCount: StateFlow<Int> = streakRepository.currentStreak
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    // --- STATE FLOW SELECTED RESULT ---
    private val _selectedResult = MutableStateFlow<BMICheckSummary?>(null)
    val selectedResult: StateFlow<BMICheckSummary?> = _selectedResult.asStateFlow()

    // --- FUNGSI LOAD HISTORY ---
    fun loadHistory(userId: Int) {
        viewModelScope.launch {
            repository.getBMIHistoryByUser(userId).collect { list ->
                _history.value = list
            }
        }
    }

    // --- FUNGSI SAVE ---
    fun saveResult(userId: Int, summary: BMICheckSummary) {
        viewModelScope.launch {
            // 1. Simpan ke Database
            repository.saveBMI(userId, summary)

            // 2. Update Streak
            streakRepository.updateStreak()

            // 3. Cek Badge (Logic Diperbaiki)
            val currentStreakValue = streakCount.value

            val newBadges = badgeManager.checkNewBadges(
                userId = userId,
                summary = summary,
                currentStreak = currentStreakValue
            )

            if (newBadges.isNotEmpty()) {
                _newlyUnlockedBadges.value = newBadges
            }
        }
    }

    // --- FUNGSI DELETE HISTORY ---
    fun deleteHistory(uniqueId: String) {
        viewModelScope.launch {
            repository.deleteBMI(uniqueId)
        }
    }

    // --- FUNGSI SELECT HISTORY ---
    fun selectHistory(summary: BMICheckSummary) {
        _selectedResult.value = summary
    }

    // --- FUNGSI DISMISS BADGE ---
    fun dismissBadge() {
        _newlyUnlockedBadges.value = emptyList()
    }
}