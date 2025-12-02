package af.mobile.mybmi.viewmodel

import af.mobile.mybmi.receiver.ReminderReceiver
import af.mobile.mybmi.util.ReminderScheduler
import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ReminderViewModel(application: Application) : AndroidViewModel(application) {

    private val PREFS_NAME = "bmi_reminder_prefs"
    private val KEY_IS_ENABLED = "is_reminder_enabled"
    private val KEY_DAY_OF_MONTH = "reminder_day" // Tanggal 1-28

    private val prefs = application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _isReminderEnabled = MutableStateFlow(false)
    val isReminderEnabled: StateFlow<Boolean> = _isReminderEnabled

    private val _reminderDay = MutableStateFlow(1) // Default tanggal 1
    val reminderDay: StateFlow<Int> = _reminderDay

    init {
        loadSettings()
    }

    private fun loadSettings() {
        _isReminderEnabled.value = prefs.getBoolean(KEY_IS_ENABLED, false)
        _reminderDay.value = prefs.getInt(KEY_DAY_OF_MONTH, 1)
    }

    fun toggleReminder(isEnabled: Boolean) {
        _isReminderEnabled.value = isEnabled
        prefs.edit { putBoolean(KEY_IS_ENABLED, isEnabled) }

        if (isEnabled) {
            // Nyalakan alarm sesuai tanggal yang tersimpan
            ReminderScheduler.scheduleMonthlyReminder(getApplication(), _reminderDay.value)
        } else {
            // Matikan alarm
            ReminderScheduler.cancelReminder(getApplication())
        }
    }

    fun updateReminderDay(day: Int) {
        _reminderDay.value = day
        prefs.edit { putInt(KEY_DAY_OF_MONTH, day) }

        // Jika pengingat sedang aktif, reset alarmnya agar mengikuti tanggal baru
        if (_isReminderEnabled.value) {
            ReminderScheduler.cancelReminder(getApplication())
            ReminderScheduler.scheduleMonthlyReminder(getApplication(), day)
        }
    }

    // --- FUNGSI BARU: Tes Notifikasi Langsung ---
    fun testNotificationInstant() {
        // Kirim broadcast langsung tanpa menunggu AlarmManager
        val intent = Intent(getApplication(), ReminderReceiver::class.java)
        getApplication<Application>().sendBroadcast(intent)
    }
}