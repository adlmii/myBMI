package af.mobile.mybmi.database

import af.mobile.mybmi.model.BMICheckSummary
import af.mobile.mybmi.model.BMICategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date

class BMIRepository(private val bmiDao: BMIDao) {

    suspend fun saveBMI(userId: Int, summary: BMICheckSummary) {
        val entity = BMIHistoryEntity(
            uniqueId = summary.id,
            userId = userId,
            timestamp = summary.timestamp.time,
            height = summary.height,
            weight = summary.weight,
            bmi = summary.bmi,
            category = summary.category.name,
            idealWeightMin = summary.idealWeightRange.first,
            idealWeightMax = summary.idealWeightRange.second
        )
        bmiDao.insertBMI(entity)
    }

    fun getBMIHistoryByUser(userId: Int): Flow<List<BMICheckSummary>> {
        return bmiDao.getBMIHistoryByUser(userId).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getBMIById(uniqueId: String): BMICheckSummary? {
        return bmiDao.getBMIById(uniqueId)?.toDomain()
    }

    // --- UPDATE LOGIKA HAPUS ---
    // Sekarang menerima ID String langsung agar lebih efisien
    suspend fun deleteBMI(uniqueId: String) {
        bmiDao.deleteBMIByUniqueId(uniqueId)
    }

    suspend fun deleteBMIByUserId(userId: Int) {
        bmiDao.deleteBMIByUserId(userId)
    }

    suspend fun getRecentBMI(limit: Int = 10): List<BMICheckSummary> {
        return bmiDao.getRecentBMI(limit).map { it.toDomain() }
    }

    suspend fun getBMICountByUser(userId: Int): Int {
        return bmiDao.getBMICountByUser(userId)
    }

    private fun BMIHistoryEntity.toDomain(): BMICheckSummary {
        return BMICheckSummary(
            id = this.uniqueId,
            timestamp = Date(this.timestamp),
            height = this.height,
            weight = this.weight,
            bmi = this.bmi,
            category = BMICategory.valueOf(this.category),
            idealWeightRange = Pair(this.idealWeightMin, this.idealWeightMax)
        )
    }
}