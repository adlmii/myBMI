package af.mobile.mybmi.database

import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(name: String): Long {
        val user = UserEntity(name = name)
        return userDao.insertUser(user)
    }

    suspend fun getLatestUser(): UserEntity? {
        return userDao.getLatestUser()
    }

    suspend fun getUserById(userId: Int): UserEntity? {
        return userDao.getUserById(userId)
    }

    fun getAllUsers(): Flow<List<UserEntity>> {
        return userDao.getAllUsers()
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    suspend fun deleteUser(userId: Int) {
        userDao.deleteUser(userId)
    }

    suspend fun getUserCount(): Int {
        return userDao.getUserCount()
    }

    suspend fun hasUser(): Boolean {
        return getUserCount() > 0
    }

    suspend fun getOrCreateUser(): UserEntity {
        return userDao.getLatestUser() ?: run {
            val newUserId = userDao.insertUser(UserEntity(name = ""))
            userDao.getUserById(newUserId.toInt())!!
        }
    }

}

