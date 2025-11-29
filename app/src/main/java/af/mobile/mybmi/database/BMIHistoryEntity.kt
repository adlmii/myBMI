package af.mobile.mybmi.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bmi_history")
data class BMIHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uniqueId: String,
    val userId: Int,
    val timestamp: Long,
    val height: Double,
    val weight: Double,
    val bmi: Double,
    val category: String,
    val idealWeightMin: Double,
    val idealWeightMax: Double,
    val createdAt: Long = System.currentTimeMillis()
)

