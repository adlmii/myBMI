package af.mobile.mybmi.model

import java.util.Date

// Data class untuk input BMI
data class BMICheckInput(
    val height: String = "", // dalam cm (String biar bisa handle input kosong)
    val weight: String = ""  // dalam kg
) {
    fun isValid(): Boolean {
        val h = height.toDoubleOrNull()
        val w = weight.toDoubleOrNull()
        return h != null && w != null && h > 0 && w > 0
    }
}

