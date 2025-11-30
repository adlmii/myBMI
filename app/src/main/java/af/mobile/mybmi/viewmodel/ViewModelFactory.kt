package af.mobile.mybmi.viewmodel

import af.mobile.mybmi.database.BMIDao
import af.mobile.mybmi.database.BMIRepository
import af.mobile.mybmi.database.BadgeDao
import af.mobile.mybmi.database.UserRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ResultViewModelFactory(
    private val bmiRepository: BMIRepository,
    private val badgeDao: BadgeDao,
    private val bmiDao: BMIDao,
    private val userId: Int
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Pass parameter baru ke ViewModel
        return ResultViewModel(bmiRepository, badgeDao, bmiDao, userId) as T
    }
}

class UserViewModelFactory(
    private val userRepository: UserRepository,
    private val badgeDao: BadgeDao? = null
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(userRepository, badgeDao) as T
    }
}

