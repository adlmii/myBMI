package af.mobile.mybmi.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(
    entities = [UserEntity::class, BMIHistoryEntity::class, UserBadgeEntity::class],
    version = 4,
    exportSchema = false
)
abstract class MyBMIDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bmiDao(): BMIDao
    abstract fun badgeDao(): BadgeDao // Tambahkan ini

    companion object {
        @Volatile
        private var INSTANCE: MyBMIDatabase? = null

        fun getDatabase(context: Context): MyBMIDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyBMIDatabase::class.java,
                    "mybmi_database"
                )
                    .fallbackToDestructiveMigration() // Hapus data lama jika versi naik (aman untuk dev)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}