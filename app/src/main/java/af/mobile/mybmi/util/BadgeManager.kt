package af.mobile.mybmi.util

import af.mobile.mybmi.database.BMIDao
import af.mobile.mybmi.database.BadgeDao
import af.mobile.mybmi.database.UserBadgeEntity
import af.mobile.mybmi.model.BMICategory
import af.mobile.mybmi.model.BMICheckSummary
import af.mobile.mybmi.model.Badge
import java.util.Calendar

class BadgeManager(
    private val badgeDao: BadgeDao,
    private val bmiDao: BMIDao
) {

    // Fungsi utama: Dipanggil setelah user simpan hasil BMI
    suspend fun checkNewBadges(userId: Int, summary: BMICheckSummary): List<Badge> {
        val unlockedBadges = mutableListOf<Badge>()

        // 1. Cek Badge: FIRST_STEP (Cek Pertama Kali)
        if (!hasBadge(userId, Badge.FIRST_STEP)) {
            // Cek jumlah data di history
            val count = bmiDao.getBMICountByUser(userId)
            if (count >= 1) {
                unlockBadge(userId, Badge.FIRST_STEP)
                unlockedBadges.add(Badge.FIRST_STEP)
            }
        }

        // 2. Cek Badge: IDEAL_GOAL (Berat Badan Normal)
        if (!hasBadge(userId, Badge.IDEAL_GOAL)) {
            if (summary.category == BMICategory.NORMAL) {
                unlockBadge(userId, Badge.IDEAL_GOAL)
                unlockedBadges.add(Badge.IDEAL_GOAL)
            }
        }

        // 3. Cek Badge: CONSISTENCY_3 (Rajin Cek 3x)
        if (!hasBadge(userId, Badge.CONSISTENCY_3)) {
            val count = bmiDao.getBMICountByUser(userId)
            if (count >= 3) {
                unlockBadge(userId, Badge.CONSISTENCY_3)
                unlockedBadges.add(Badge.CONSISTENCY_3)
            }
        }

        // 4. Cek Badge: NIGHT_OWL (Cek Malam Hari > Jam 21:00)
        if (!hasBadge(userId, Badge.NIGHT_OWL)) {
            val calendar = Calendar.getInstance().apply { time = summary.timestamp }
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            if (hour >= 21 || hour < 4) { // Antara jam 9 malam - 4 pagi
                unlockBadge(userId, Badge.NIGHT_OWL)
                unlockedBadges.add(Badge.NIGHT_OWL)
            }
        }

        return unlockedBadges // Kembalikan daftar badge yang BARU SAJA didapat
    }

    // Helper: Cek apakah user sudah punya badge ini di database
    private suspend fun hasBadge(userId: Int, badge: Badge): Boolean {
        return badgeDao.hasBadge(userId, badge.id)
    }

    // Helper: Simpan badge ke database
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