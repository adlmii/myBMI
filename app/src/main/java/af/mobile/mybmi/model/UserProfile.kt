package af.mobile.mybmi.model

import af.mobile.mybmi.database.UserEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class UserProfile(
    val id: Int = 0,
    val name: String,
    val gender: String,
    val birthDate: Long, // 0L adalah marker untuk belum di-set
    val profileImagePath: String?
) {
    // Menghitung umur dalam tahun (dipakai internal)
    private fun getAgeInYears(): Int {
        if (birthDate == 0L) return 0

        val dob = Calendar.getInstance().apply { timeInMillis = birthDate }
        val today = Calendar.getInstance()

        var age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR)
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--
        }
        return age
    }

    // FUNGSI BARU: Logic Display untuk ProfileScreen
    fun getAgeDisplayString(): String {
        return if (birthDate == 0L) {
            "Atur Tanggal Lahir" // Teks jika belum disetting
        } else {
            "${getAgeInYears()} Tahun"
        }
    }

    // Fungsi helper lainnya (disederhanakan untuk keperluan ini)
    fun getFormattedBirthDate(): String {
        if (birthDate == 0L) return ""
        val localeID = Locale.forLanguageTag("id-ID")
        val sdf = SimpleDateFormat("dd MMMM yyyy", localeID)
        return sdf.format(Date(birthDate))
    }

    fun isValid(): Boolean {
        return name.isNotBlank() && birthDate != 0L
    }
}

// --- Extension Functions (Mappers) ---

fun UserEntity.toModel(): UserProfile {
    return UserProfile(
        id = this.id,
        name = this.name,
        gender = this.gender,
        birthDate = this.birthDate,
        profileImagePath = this.profileImagePath
    )
}

fun UserProfile.toEntity(): UserEntity {
    return UserEntity(
        id = this.id,
        name = this.name,
        gender = this.gender,
        birthDate = this.birthDate,
        profileImagePath = this.profileImagePath,
        updatedAt = System.currentTimeMillis()
    )
}