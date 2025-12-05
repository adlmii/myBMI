package af.mobile.mybmi.util

import af.mobile.mybmi.model.BMICheckSummary
import java.time.ZoneId

object StreakUtils {

    fun calculateMonthlyStreak(history: List<BMICheckSummary>): Int {
        if (history.isEmpty()) return 0

        // 1. Urutkan dari TERLAMA ke TERBARU
        val sortedHistory = history.sortedBy { it.timestamp }

        // 2. Konversi Date ke LocalDate
        val dates = sortedHistory.map {
            it.timestamp.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate()
        }

        var streak = 1
        var anchorDate = dates[0]

        for (i in 1 until dates.size) {
            val checkDate = dates[i]

            // Hitung selisih bulan secara absolut (tahun * 12 + bulan)
            val monthsDiff = (checkDate.year - anchorDate.year) * 12 + (checkDate.monthValue - anchorDate.monthValue)

            if (monthsDiff == 0) {
                // KASUS: Cek lagi di bulan yang SAMA -> Abaikan (Update biasa)
                continue
            } else if (monthsDiff == 1) {
                // KASUS: Cek di bulan BERIKUTNYA (Berurutan) -> Streak Nambah
                streak++
                anchorDate = checkDate
            } else {
                // KASUS: Bolong lebih dari 1 bulan -> Streak Reset
                streak = 1
                anchorDate = checkDate
            }
        }

        // 3. VALIDASI TERAKHIR (Cek Status Hari Ini)
        // Apakah streak masih aktif atau sudah kadaluwarsa?
        val today = java.time.LocalDate.now()
        val monthsSinceLastCheck = (today.year - anchorDate.year) * 12 + (today.monthValue - anchorDate.monthValue)

        // Jika sudah lewat lebih dari 1 bulan sejak cek terakhir, streak hangus.
        // Contoh: Terakhir cek Januari. Sekarang Maret. (Diff = 2) -> Hangus.
        // Toleransi: Kita anggap streak masih aktif selama belum lewat bulan depannya lagi.
        if (monthsSinceLastCheck > 1) {
            return 0
        }

        return streak
    }
}