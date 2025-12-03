package af.mobile.mybmi.util

import af.mobile.mybmi.model.BMICategory
import org.junit.Assert.*
import org.junit.Test

class ScoringEngineTest {

    // 1. Tes Perhitungan Normal
    @Test
    fun `calculateBMI returns correct value for normal input`() {
        val height = 170.0 // cm
        val weight = 70.0  // kg

        // Manual: 70 / (1.7 * 1.7) = 24.221... -> Dibulatkan jadi 24.22
        val result = ScoringEngine.calculateBMI(height, weight)

        assertEquals(24.22, result, 0.0)
    }

    // 2. Tes Kategori (Generate Summary)
    @Test
    fun `generateSummary returns correct category`() {
        // Kasus: Tinggi 170, Berat 50 (BMI = 17.3 -> Underweight)
        val summary = ScoringEngine.generateSummary(170.0, 50.0)
        assertEquals(BMICategory.UNDERWEIGHT, summary.category)

        // Kasus: Tinggi 170, Berat 70 (BMI = 24.22 -> Normal)
        val summary2 = ScoringEngine.generateSummary(170.0, 70.0)
        assertEquals(BMICategory.NORMAL, summary2.category)

        // Kasus: Tinggi 170, Berat 90 (BMI = 31.14 -> Obese)
        val summary3 = ScoringEngine.generateSummary(170.0, 90.0)
        assertEquals(BMICategory.OBESE, summary3.category)
    }

    // 3. Tes Input Tidak Valid (Harus Error)
    @Test(expected = IllegalArgumentException::class)
    fun `calculateBMI throws error for zero height`() {
        ScoringEngine.calculateBMI(0.0, 70.0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `calculateBMI throws error for negative weight`() {
        ScoringEngine.calculateBMI(170.0, -5.0)
    }

    // 4. Tes Berat Ideal
    @Test
    fun `calculateIdealWeightRange returns correct range`() {
        val height = 170.0
        // Min: 18.5 * 1.7^2 = 53.465 -> 53.4
        // Max: 24.9 * 1.7^2 = 71.961 -> 71.9

        val range = ScoringEngine.calculateIdealWeightRange(height)

        assertEquals(53.4, range.first, 0.0)
        assertEquals(71.9, range.second, 0.0)
    }
}