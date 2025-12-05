package af.mobile.mybmi.database

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject

class StreakRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    // Key untuk menyimpan data di memori
    private val CURRENT_STREAK_KEY = intPreferencesKey("current_streak")
    private val LAST_UPDATE_DATE_KEY = stringPreferencesKey("last_streak_date")

    // Flow ini akan dipantau oleh Home Screen secara Realtime
    val currentStreak: Flow<Int> = dataStore.data.map { prefs ->
        prefs[CURRENT_STREAK_KEY] ?: 0
    }

    // Fungsi logic utama: Menentukan apakah streak nambah, tetap, atau reset
    suspend fun updateStreak() {
        val today = LocalDate.now()
        val todayStr = today.toString() // Format: "2023-12-05"

        dataStore.edit { prefs ->
            val lastDateStr = prefs[LAST_UPDATE_DATE_KEY]
            val currentStreak = prefs[CURRENT_STREAK_KEY] ?: 0

            if (lastDateStr == null) {
                // KASUS 1: User baru pertama kali pakai app
                prefs[CURRENT_STREAK_KEY] = 1
                prefs[LAST_UPDATE_DATE_KEY] = todayStr
            } else {
                try {
                    val lastDate = LocalDate.parse(lastDateStr)

                    // Hitung selisih bulan (Logic: Tahun x 12 + Bulan)
                    val monthsDiff = (today.year - lastDate.year) * 12 + (today.monthValue - lastDate.monthValue)

                    if (monthsDiff == 0) {
                        // KASUS 2: Masih di bulan yang sama
                        // Streak TIDAK berubah, hanya update tanggal terakhir aktivitas
                        prefs[LAST_UPDATE_DATE_KEY] = todayStr
                    } else if (monthsDiff == 1) {
                        // KASUS 3: Bulan berikutnya (Berurutan/Rajin)
                        // Streak NAMBAH +1
                        prefs[CURRENT_STREAK_KEY] = currentStreak + 1
                        prefs[LAST_UPDATE_DATE_KEY] = todayStr
                    } else {
                        // KASUS 4: Bolong lebih dari 1 bulan (Malas)
                        // Streak RESET jadi 1 (karena hari ini dia mulai lagi)
                        prefs[CURRENT_STREAK_KEY] = 1
                        prefs[LAST_UPDATE_DATE_KEY] = todayStr
                    }
                } catch (e: Exception) {
                    // Safety net jika format tanggal error, anggap input baru
                    prefs[CURRENT_STREAK_KEY] = 1
                    prefs[LAST_UPDATE_DATE_KEY] = todayStr
                }
            }
        }
    }

    // Opsional: Jika user ingin reset total via menu settings
    suspend fun resetStreak() {
        dataStore.edit {
            it.remove(CURRENT_STREAK_KEY)
            it.remove(LAST_UPDATE_DATE_KEY)
        }
    }
}