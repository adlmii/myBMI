package af.mobile.mybmi.model

import af.mobile.mybmi.R
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.DirectionsRun
import androidx.compose.material.icons.automirrored.rounded.TrendingUp
import androidx.compose.material.icons.rounded.*
import androidx.compose.ui.graphics.vector.ImageVector

enum class Badge(
    val id: String,
    val titleRes: Int,
    val descriptionRes: Int,
    val icon: ImageVector,
    val requirementRes: Int
) {
    FIRST_STEP(
        id = "first_step",
        titleRes = R.string.badge_first_step_title,
        descriptionRes = R.string.badge_first_step_desc,
        // [PERBAIKAN] Gunakan AutoMirrored
        icon = Icons.AutoMirrored.Rounded.DirectionsRun,
        requirementRes = R.string.badge_first_step_req
    ),

    STREAK_1(
        id = "streak_1",
        titleRes = R.string.badge_streak_1_title,
        descriptionRes = R.string.badge_streak_1_desc,
        icon = Icons.Rounded.LocalFireDepartment,
        requirementRes = R.string.badge_streak_1_req
    ),
    STREAK_3(
        id = "streak_3",
        titleRes = R.string.badge_streak_3_title,
        descriptionRes = R.string.badge_streak_3_desc,
        // [PERBAIKAN] Gunakan AutoMirrored
        icon = Icons.AutoMirrored.Rounded.TrendingUp,
        requirementRes = R.string.badge_streak_3_req
    ),
    STREAK_6(
        id = "streak_6",
        titleRes = R.string.badge_streak_6_title,
        descriptionRes = R.string.badge_streak_6_desc,
        icon = Icons.Rounded.WorkspacePremium,
        requirementRes = R.string.badge_streak_6_req
    ),
    STREAK_12(
        id = "streak_12",
        titleRes = R.string.badge_streak_12_title,
        descriptionRes = R.string.badge_streak_12_desc,
        icon = Icons.Rounded.EmojiEvents,
        requirementRes = R.string.badge_streak_12_req
    ),

    CONSISTENCY_3(
        id = "consistency_3",
        titleRes = R.string.badge_consistency_3_title,
        descriptionRes = R.string.badge_consistency_3_desc,
        icon = Icons.Rounded.Repeat,
        requirementRes = R.string.badge_consistency_3_req
    ),
    IDEAL_GOAL(
        id = "ideal_goal",
        titleRes = R.string.badge_ideal_goal_title,
        descriptionRes = R.string.badge_ideal_goal_desc,
        icon = Icons.Rounded.Star,
        requirementRes = R.string.badge_ideal_goal_req
    ),
    PROFILE_MASTER(
        id = "profile_master",
        titleRes = R.string.badge_profile_master_title,
        descriptionRes = R.string.badge_profile_master_desc,
        icon = Icons.Rounded.AccountCircle,
        requirementRes = R.string.badge_profile_master_req
    ),
    NIGHT_OWL(
        id = "night_owl",
        titleRes = R.string.badge_night_owl_title,
        descriptionRes = R.string.badge_night_owl_desc,
        icon = Icons.Rounded.DarkMode,
        requirementRes = R.string.badge_night_owl_req
    );

    companion object {
        fun fromId(id: String): Badge? {
            return entries.find { it.id == id }
        }
    }
}