package af.mobile.mybmi.util

import af.mobile.mybmi.database.BMIDao
import af.mobile.mybmi.database.BadgeDao
import af.mobile.mybmi.database.UserBadgeEntity
import af.mobile.mybmi.model.BMICategory
import af.mobile.mybmi.model.BMICheckSummary
import af.mobile.mybmi.model.Badge
import java.util.Calendar
import javax.inject.Inject

class BadgeManager @Inject constructor(
    private val badgeDao: BadgeDao,
    private val bmiDao: BMIDao
) {

    suspend fun checkNewBadges(userId: Int, summary: BMICheckSummary, currentStreak: Int): List<Badge> {
        val unlockedBadges = mutableListOf<Badge>()

        // 1. Cek Badge: FIRST_STEP
        if (!hasBadge(userId, Badge.FIRST_STEP)) {
            val count = bmiDao.getBMICountByUser(userId)
            if (count >= 1) {
                unlockBadge(userId, Badge.FIRST_STEP)
                unlockedBadges.add(Badge.FIRST_STEP)
            }
        }

        // 2. Cek Badge: IDEAL_GOAL
        if (!hasBadge(userId, Badge.IDEAL_GOAL)) {
            if (summary.category == BMICategory.NORMAL) {
                unlockBadge(userId, Badge.IDEAL_GOAL)
                unlockedBadges.add(Badge.IDEAL_GOAL)
            }
        }

        // 3. Cek Badge: CONSISTENCY_3 (Total cek 3x)
        if (!hasBadge(userId, Badge.CONSISTENCY_3)) {
            val count = bmiDao.getBMICountByUser(userId)
            if (count >= 3) {
                unlockBadge(userId, Badge.CONSISTENCY_3)
                unlockedBadges.add(Badge.CONSISTENCY_3)
            }
        }

        // 4. Cek Badge: NIGHT_OWL
        if (!hasBadge(userId, Badge.NIGHT_OWL)) {
            val calendar = Calendar.getInstance().apply { time = summary.timestamp }
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            if (hour >= 21 || hour < 4) {
                unlockBadge(userId, Badge.NIGHT_OWL)
                unlockedBadges.add(Badge.NIGHT_OWL)
            }
        }

        // 5. --- CEK STREAK (LOGIC BARU) ---
        if (currentStreak >= 1 && !hasBadge(userId, Badge.STREAK_1)) {
            unlockBadge(userId, Badge.STREAK_1)
            unlockedBadges.add(Badge.STREAK_1)
        }
        if (currentStreak >= 3 && !hasBadge(userId, Badge.STREAK_3)) {
            unlockBadge(userId, Badge.STREAK_3)
            unlockedBadges.add(Badge.STREAK_3)
        }
        if (currentStreak >= 6 && !hasBadge(userId, Badge.STREAK_6)) {
            unlockBadge(userId, Badge.STREAK_6)
            unlockedBadges.add(Badge.STREAK_6)
        }
        if (currentStreak >= 12 && !hasBadge(userId, Badge.STREAK_12)) {
            unlockBadge(userId, Badge.STREAK_12)
            unlockedBadges.add(Badge.STREAK_12)
        }

        return unlockedBadges
    }

    private suspend fun hasBadge(userId: Int, badge: Badge): Boolean {
        return badgeDao.hasBadge(userId, badge.id)
    }

    private suspend fun unlockBadge(userId: Int, badge: Badge) {
        badgeDao.insertBadge(
            UserBadgeEntity(
                userId = userId,
                badgeId = badge.id,
                unlockedAt = System.currentTimeMillis()
            )
        )
    }
}