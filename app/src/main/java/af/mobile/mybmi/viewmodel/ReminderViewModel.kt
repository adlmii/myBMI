package af.mobile.mybmi.viewmodel

import af.mobile.mybmi.receiver.ReminderReceiver
import af.mobile.mybmi.util.ReminderScheduler
import android.app.Application
import android.content.Intent
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReminderViewModel @Inject constructor(
    application: Application,
    private val dataStore: DataStore<Preferences>
) : AndroidViewModel(application) {

    // Kunci DataStore
    private val KEY_IS_ENABLED = booleanPreferencesKey("is_reminder_enabled")
    private val KEY_DAY_OF_MONTH = intPreferencesKey("reminder_day")

    private val _isReminderEnabled = MutableStateFlow(false)
    val isReminderEnabled: StateFlow<Boolean> = _isReminderEnabled

    private val _reminderDay = MutableStateFlow(1)
    val reminderDay: StateFlow<Int> = _reminderDay

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            dataStore.data.collect { preferences ->
                _isReminderEnabled.value = preferences[KEY_IS_ENABLED] ?: false
                _reminderDay.value = preferences[KEY_DAY_OF_MONTH] ?: 1
            }
        }
    }

    fun toggleReminder(isEnabled: Boolean) {
        viewModelScope.launch {
            // 1. Simpan ke DataStore
            dataStore.edit { it[KEY_IS_ENABLED] = isEnabled }

            // 2. Atur Alarm
            if (isEnabled) {
                ReminderScheduler.scheduleMonthlyReminder(getApplication(), _reminderDay.value)
            } else {
                ReminderScheduler.cancelReminder(getApplication())
            }
        }
    }

    fun updateReminderDay(day: Int) {
        viewModelScope.launch {
            // 1. Simpan ke DataStore
            dataStore.edit { it[KEY_DAY_OF_MONTH] = day }

            // 2. Reset Alarm jika sedang aktif
            if (_isReminderEnabled.value) {
                ReminderScheduler.cancelReminder(getApplication())
                ReminderScheduler.scheduleMonthlyReminder(getApplication(), day)
            }
        }
    }

    fun testNotificationInstant() {
        val intent = Intent(getApplication(), ReminderReceiver::class.java)
        getApplication<Application>().sendBroadcast(intent)
    }
}