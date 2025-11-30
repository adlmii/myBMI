package af.mobile.mybmi.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BadgeDao {
    // Simpan badge baru. Jika sudah ada (duplikat), abaikan saja.
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertBadge(badge: UserBadgeEntity)

    // Ambil semua badge milik user tertentu (Live Data / Flow)
    @Query("SELECT * FROM user_badges WHERE userId = :userId")
    fun getUserBadges(userId: Int): Flow<List<UserBadgeEntity>>

    // Cek apakah user sudah punya badge tertentu (untuk validasi sebelum insert)
    @Query("SELECT EXISTS(SELECT 1 FROM user_badges WHERE userId = :userId AND badgeId = :badgeId)")
    suspend fun hasBadge(userId: Int, badgeId: String): Boolean
}