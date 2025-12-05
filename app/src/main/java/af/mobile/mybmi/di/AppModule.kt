package af.mobile.mybmi.di

import af.mobile.mybmi.database.*
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import af.mobile.mybmi.database.StreakRepository
import af.mobile.mybmi.util.BadgeManager
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

// Extension untuk DataStore (jika kamu sudah menerapkan langkah DataStore)
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mybmi_settings")

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    // --- 1. PROVIDER DATABASE (INI YANG HILANG DI ERROR KAMU) ---
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MyBMIDatabase {
        return Room.databaseBuilder(
            context,
            MyBMIDatabase::class.java,
            "mybmi_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // --- 2. PROVIDER DAO ---
    @Provides
    fun provideUserDao(db: MyBMIDatabase): UserDao = db.userDao()

    @Provides
    fun provideBMIDao(db: MyBMIDatabase): BMIDao = db.bmiDao()

    @Provides
    fun provideBadgeDao(db: MyBMIDatabase): BadgeDao = db.badgeDao()

    // --- 3. PROVIDER REPOSITORY ---
    @Provides
    @Singleton
    fun provideUserRepository(userDao: UserDao): UserRepository {
        return UserRepository(userDao)
    }

    @Provides
    @Singleton
    fun provideBMIRepository(bmiDao: BMIDao): BMIRepository {
        return BMIRepository(bmiDao)
    }

    // --- 4. PROVIDER DATASTORE (Jika kamu mengikuti langkah "Level Up") ---
    @Provides
    @Singleton
    fun provideDataStore(@ApplicationContext context: Context): DataStore<Preferences> {
        return context.dataStore
    }

    // --- 5. PROVIDER STREAK REPOSITORY ---
    @Provides
    @Singleton
    fun provideStreakRepository(dataStore: DataStore<Preferences>): StreakRepository {
        return StreakRepository(dataStore)
    }

    // --- 6. PROVIDER BADGE MANAGER ---
    @Provides
    fun provideBadgeManager(badgeDao: BadgeDao, bmiDao: BMIDao): BadgeManager {
        return BadgeManager(badgeDao, bmiDao)
    }
}