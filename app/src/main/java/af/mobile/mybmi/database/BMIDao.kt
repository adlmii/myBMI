package af.mobile.mybmi.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Delete
import kotlinx.coroutines.flow.Flow

@Dao
interface BMIDao {
    @Insert
    suspend fun insertBMI(bmi: BMIHistoryEntity): Long

    @Query("SELECT * FROM bmi_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getBMIHistoryByUser(userId: Int): Flow<List<BMIHistoryEntity>>

    @Query("SELECT * FROM bmi_history WHERE uniqueId = :uniqueId")
    suspend fun getBMIById(uniqueId: String): BMIHistoryEntity?

    @Delete
    suspend fun deleteBMI(bmi: BMIHistoryEntity)

    @Query("DELETE FROM bmi_history WHERE userId = :userId")
    suspend fun deleteBMIByUserId(userId: Int)

    @Query("SELECT * FROM bmi_history ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentBMI(limit: Int = 10): List<BMIHistoryEntity>

    @Query("SELECT COUNT(*) FROM bmi_history WHERE userId = :userId")
    suspend fun getBMICountByUser(userId: Int): Int
}

