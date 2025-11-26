package af.mobile.mybmi.util

import af.mobile.mybmi.model.BMICategory
import af.mobile.mybmi.model.BMICheckSummary
import kotlin.math.pow

object ScoringEngine {

    fun calculateBMI(heightCm: Double, weightKg: Double): Double {
        require(heightCm > 0) { "Height must be greater than 0" }
        require(weightKg > 0) { "Weight must be greater than 0" }

        val heightM = heightCm / 100.0
        val bmi = weightKg / heightM.pow(2)

        // Round ke 2 desimal
        return (bmi * 100).toLong() / 100.0
    }

    fun calculateIdealWeightRange(heightCm: Double): Pair<Double, Double> {
        require(heightCm > 0) { "Height must be greater than 0" }

        val heightM = heightCm / 100.0
        val minWeight = 18.5 * heightM.pow(2)
        val maxWeight = 24.9 * heightM.pow(2)

        // Round ke 1 desimal
        return Pair(
            (minWeight * 10).toLong() / 10.0,
            (maxWeight * 10).toLong() / 10.0
        )
    }

    fun generateSummary(heightCm: Double, weightKg: Double): BMICheckSummary {
        val bmi = calculateBMI(heightCm, weightKg)
        val category = BMICategory.fromBMI(bmi)
        val idealRange = calculateIdealWeightRange(heightCm)

        return BMICheckSummary(
            height = heightCm,
            weight = weightKg,
            bmi = bmi,
            category = category,
            idealWeightRange = idealRange
        )
    }
}