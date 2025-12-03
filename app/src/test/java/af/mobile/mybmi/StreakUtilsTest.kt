package af.mobile.mybmi.util

import af.mobile.mybmi.model.BMICategory
import af.mobile.mybmi.model.BMICheckSummary
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date

class StreakUtilsTest {

    // Helper function untuk membuat data dummy dengan tanggal tertentu
    private fun createSummary(dateStr: String): BMICheckSummary {
        // Format input: "2023-01-01"
        val localDate = LocalDate.parse(dateStr)
        val instant = localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
        val date = Date.from(instant)

        return BMICheckSummary(
            id = "test",
            timestamp = date,
            height = 170.0, weight = 70.0, bmi = 24.2,
            category = BMICategory.NORMAL,
            idealWeightRange = Pair(50.0, 70.0)
        )
    }

    @Test
    fun `calculateMonthlyStreak returns 0 for empty list`() {
        val streak = StreakUtils.calculateMonthlyStreak(emptyList())
        assertEquals(0, streak)
    }

    @Test
    fun `calculateMonthlyStreak calculates consecutive months correctly`() {
        // Skenario: User cek di Jan, Feb, Maret (Tahun ini)
        // Asumsi tes ini dijalankan di tahun yang sama atau logika streak kamu
        // bergantung pada "jarak" antar data, bukan tanggal hari ini secara ketat
        // (Kecuali validasi terakhir di StreakUtils).

        // Agar tes ini valid kapanpun, kita buat data "Mundur" dari hari ini
        val today = LocalDate.now()
        val lastMonth = today.minusMonths(1)
        val twoMonthsAgo = today.minusMonths(2)

        val history = listOf(
            createSummary(twoMonthsAgo.toString()), // 2 bulan lalu
            createSummary(lastMonth.toString()),    // 1 bulan lalu
            createSummary(today.toString())         // Hari ini
        )

        val streak = StreakUtils.calculateMonthlyStreak(history)
        assertEquals(3, streak)
    }

    @Test
    fun `calculateMonthlyStreak resets if month skipped`() {
        val today = LocalDate.now()
        val threeMonthsAgo = today.minusMonths(3) // Skip 2 bulan lalu & 1 bulan lalu

        val history = listOf(
            createSummary(threeMonthsAgo.toString()),
            createSummary(today.toString())
        )

        // Seharusnya reset jadi 1 (hanya menghitung yang hari ini)
        val streak = StreakUtils.calculateMonthlyStreak(history)
        assertEquals(1, streak)
    }

    @Test
    fun `calculateMonthlyStreak ignores multiple checks in same month`() {
        val today = LocalDate.now()

        val history = listOf(
            createSummary(today.withDayOfMonth(1).toString()),  // Tgl 1
            createSummary(today.withDayOfMonth(15).toString()), // Tgl 15
            createSummary(today.withDayOfMonth(20).toString())  // Tgl 20
        )

        // Cek 3x di bulan yang sama tetap dihitung streak 1
        val streak = StreakUtils.calculateMonthlyStreak(history)
        assertEquals(1, streak)
    }
}