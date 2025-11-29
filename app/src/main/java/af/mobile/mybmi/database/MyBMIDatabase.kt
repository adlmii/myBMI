package af.mobile.mybmi.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [UserEntity::class, BMIHistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class MyBMIDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun bmiDao(): BMIDao

    companion object {
        @Volatile
        private var INSTANCE: MyBMIDatabase? = null

        fun getDatabase(context: Context): MyBMIDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MyBMIDatabase::class.java,
                    "mybmi_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

