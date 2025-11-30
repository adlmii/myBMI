package af.mobile.mybmi.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_badges")
data class UserBadgeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val badgeId: String,
    val unlockedAt: Long = System.currentTimeMillis()
)