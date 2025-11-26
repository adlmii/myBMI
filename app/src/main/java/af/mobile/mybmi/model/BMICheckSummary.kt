package af.mobile.mybmi.model

import java.util.Date

// Data class untuk hasil BMI dan history
data class BMICheckSummary(
    val id: String = java.util.UUID.randomUUID().toString(),
    val timestamp: Date = Date(),
    val height: Double, // cm
    val weight: Double, // kg
    val bmi: Double,
    val category: BMICategory,
    val idealWeightRange: Pair<Double, Double> // min-max berat ideal
) {
    // Format display
    fun getDateFormatted(): String {
        val sdf = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale("id", "ID"))
        return sdf.format(timestamp)
    }

    fun getTimeFormatted(): String {
        val sdf = java.text.SimpleDateFormat("HH:mm", java.util.Locale("id", "ID"))
        return sdf.format(timestamp)
    }
}

// Enum untuk kategori BMI
enum class BMICategory(
    val displayName: String,
    val colorHex: String,
    val description: String,
    val advice: String
) {
    UNDERWEIGHT(
        displayName = "Underweight",
        colorHex = "#3B82F6", // Blue
        description = "Berat badan kurang",
        advice = "Tingkatkan asupan kalori dengan makanan bergizi seimbang"
    ),
    NORMAL(
        displayName = "Normal Weight",
        colorHex = "#10B981", // Green
        description = "Berat badan ideal",
        advice = "Pertahankan pola makan sehat dan olahraga teratur"
    ),
    OVERWEIGHT(
        displayName = "Overweight",
        colorHex = "#F59E0B", // Orange
        description = "Berat badan berlebih",
        advice = "Kurangi asupan kalori dan tingkatkan aktivitas fisik"
    ),
    OBESE(
        displayName = "Obese",
        colorHex = "#EF4444", // Red
        description = "Obesitas",
        advice = "Konsultasi dengan dokter untuk program penurunan berat badan"
    );

    companion object {
        fun fromBMI(bmi: Double): BMICategory {
            return when {
                bmi < 18.5 -> UNDERWEIGHT
                bmi < 25.0 -> NORMAL
                bmi < 30.0 -> OVERWEIGHT
                else -> OBESE
            }
        }
    }
}