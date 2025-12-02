package af.mobile.mybmi.util

import af.mobile.mybmi.model.BMICheckSummary
import java.util.Calendar

object StreakUtils {

    fun calculateMonthlyStreak(history: List<BMICheckSummary>): Int {
        if (history.isEmpty()) return 0

        // 1. Urutkan dari TERLAMA ke TERBARU
        val sortedHistory = history.sortedBy { it.timestamp }

        var streak = 1
        // Anchor adalah "Jadwal" patokan saat ini
        var anchorDate = Calendar.getInstance().apply { time = sortedHistory[0].timestamp }

        // Loop dari data kedua sampai terakhir
        for (i in 1 until sortedHistory.size) {
            val checkDate = Calendar.getInstance().apply { time = sortedHistory[i].timestamp }

            // Window Valid: Mulai dari (Anchor + 1 Bulan) sampai (Anchor + 2 Bulan)
            val validStart = (anchorDate.clone() as Calendar).apply { add(Calendar.MONTH, 1) }
            val validEnd = (anchorDate.clone() as Calendar).apply { add(Calendar.MONTH, 2) }

            if (checkDate.before(validStart)) {
                // KASUS: UPDATE BIASA (Terlalu Cepat)
                continue
            } else if (checkDate.after(validEnd)) {
                // KASUS: TELAT (Streak Putus)
                streak = 1
                anchorDate = checkDate
            } else {
                // KASUS: TEPAT WAKTU (Valid Streak)
                streak++
                anchorDate = checkDate
            }
        }

        // 2. VALIDASI TERAKHIR (Cek Status Hari Ini)
        val today = Calendar.getInstance()

        // Batas akhir streak saat ini adalah Anchor Terakhir + 2 Bulan
        val expiryDate = (anchorDate.clone() as Calendar).apply { add(Calendar.MONTH, 2) }

        // Jika hari ini sudah lewat dari batas akhir, berarti user sudah bolos > 1 bulan.
        if (today.after(expiryDate)) {
            return 0
        }

        return streak
    }
}