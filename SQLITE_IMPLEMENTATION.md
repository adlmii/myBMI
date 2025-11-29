# SQLite Implementation untuk myBMI App

## 📋 Overview
Aplikasi myBMI sekarang menggunakan **SQLite dengan Room Database** untuk menyimpan data lokal. Implementasi ini memenuhi semua requirement yang diminta:

✅ **Requirement 1**: Pada pertama kali app dijalankan, minta input nama user (data disimpan)
✅ **Requirement 2**: Jika sudah ada data nama, tidak perlu input lagi (hanya 1x)
✅ **Requirement 3**: Gunakan SQLite untuk menyimpan riwayat dan data user secara lokal

---

## 🏗️ Arsitektur Database

### Database Classes
1. **MyBMIDatabase.kt** - Singleton Room Database
   - Mengelola koneksi database
   - Menyediakan DAO interfaces
   - Automatic database creation

```kotlin
@Database(
    entities = [UserEntity::class, BMIHistoryEntity::class],
    version = 1,
    exportSchema = false
)
```

### Entity Classes

#### 1. **UserEntity** - Tabel `users`
```kotlin
@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

**Fields:**
- `id` - Primary key (auto-increment)
- `name` - Nama user
- `createdAt` - Timestamp saat dibuat
- `updatedAt` - Timestamp saat diupdate

#### 2. **BMIHistoryEntity** - Tabel `bmi_history`
```kotlin
@Entity(tableName = "bmi_history")
data class BMIHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val uniqueId: String,
    val userId: Int,
    val timestamp: Long,
    val height: Double,
    val weight: Double,
    val bmi: Double,
    val category: String,
    val idealWeightMin: Double,
    val idealWeightMax: Double,
    val createdAt: Long = System.currentTimeMillis()
)
```

**Fields:**
- `id` - Primary key (auto-increment)
- `uniqueId` - Unique identifier untuk setiap BMI record
- `userId` - Foreign key ke table users
- `timestamp` - Waktu pemeriksaan BMI
- `height` - Tinggi badan (cm)
- `weight` - Berat badan (kg)
- `bmi` - Nilai BMI hasil perhitungan
- `category` - Kategori BMI (Underweight, Normal, Overweight, Obese)
- `idealWeightMin` - Berat ideal minimum
- `idealWeightMax` - Berat ideal maximum

---

## 🔄 Data Access Layer (DAO)

### UserDao
```kotlin
@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity): Long
    
    @Query("SELECT * FROM users ORDER BY createdAt DESC LIMIT 1")
    suspend fun getLatestUser(): UserEntity?
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): UserEntity?
    
    @Update
    suspend fun updateUser(user: UserEntity)
    
    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUserCount(): Int
}
```

### BMIDao
```kotlin
@Dao
interface BMIDao {
    @Insert
    suspend fun insertBMI(bmi: BMIHistoryEntity): Long
    
    @Query("SELECT * FROM bmi_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getBMIHistoryByUser(userId: Int): Flow<List<BMIHistoryEntity>>
    
    @Delete
    suspend fun deleteBMI(bmi: BMIHistoryEntity)
    
    @Query("DELETE FROM bmi_history WHERE userId = :userId")
    suspend fun deleteBMIByUserId(userId: Int)
}
```

---

## 📦 Repository Pattern

### UserRepository
```kotlin
class UserRepository(private val userDao: UserDao) {
    suspend fun insertUser(name: String): Long
    suspend fun getLatestUser(): UserEntity?
    suspend fun hasUser(): Boolean
    suspend fun updateUser(user: UserEntity)
}
```

**Functions:**
- `insertUser(name)` - Simpan user baru
- `getLatestUser()` - Ambil user terakhir yang dibuat
- `hasUser()` - Cek apakah ada user di database
- `updateUser()` - Update data user

### BMIRepository
```kotlin
class BMIRepository(private val bmiDao: BMIDao) {
    suspend fun saveBMI(userId: Int, summary: BMICheckSummary)
    fun getBMIHistoryByUser(userId: Int): Flow<List<BMICheckSummary>>
    suspend fun deleteBMIByUserId(userId: Int)
    suspend fun getRecentBMI(limit: Int = 10): List<BMICheckSummary>
}
```

---

## 🎯 Flow Aplikasi

### 1. **Splash Screen - First Launch**
```
App Start
    ↓
UserViewModel.loadCurrentUser()
    ↓
    ├─→ Database.getLatestUser() → Null
    │   └─→ showNameInput = true (Tampilkan dialog input nama)
    │
    └─→ Database.getLatestUser() → User Found
        └─→ currentUser = user (Skip dialog, langsung ke home)
```

**Kode di SplashScreen.kt:**
```kotlin
LaunchedEffect(showNameInput) {
    if (!showNameInput && userViewModel?.currentUser?.value != null) {
        onNavigateToHome()
    }
}
```

### 2. **User Name Input Dialog**
```
Dialog muncul (hanya jika tidak ada user)
    ↓
User input nama
    ↓
saveUserName()
    ↓
Insert ke table users
    ↓
Set currentUser
    ↓
Navigasi ke Home Screen
```

**Kode di UserViewModel.kt:**
```kotlin
fun saveUserName(name: String) {
    viewModelScope.launch {
        userRepository.insertUser(name)
        val user = userRepository.getLatestUser()
        _currentUser.value = user
        _showNameInput.value = false
    }
}
```

### 3. **Home Screen - BMI Calculation**
```
User input height & weight
    ↓
Click "Hitung BMI"
    ↓
InputViewModel.calculateBMI()
    ↓
Generate BMICheckSummary
    ↓
resultViewModel.saveToHistory()
    ↓
BMIRepository.saveBMI(userId, summary)
    ↓
Insert ke table bmi_history
    ↓
Navigasi ke Result Screen
```

**Kode di MainActivity.kt:**
```kotlin
HomeScreen(
    onNavigateToResult = {
        inputViewModel.calculateBMI { summary ->
            resultViewModel.setCurrentResult(summary)
            if (userId > 0) {
                resultViewModel.saveToHistory(summary)  // ← SAVE TO DB
            }
            navController.navigate(Screen.Result.route)
        }
    }
)
```

### 4. **History Screen - Load Data**
```
Screen muncul
    ↓
LaunchedEffect(currentUser?.id)
    ↓
resultViewModel.loadHistory(userId)
    ↓
BMIRepository.getBMIHistoryByUser(userId)
    ↓
Query database bmi_history WHERE userId = ?
    ↓
Convert BMIHistoryEntity → BMICheckSummary
    ↓
Tampilkan di LazyColumn
```

**Kode di HistoryScreen.kt:**
```kotlin
LaunchedEffect(currentUser?.id) {
    if (currentUser != null && currentUser!!.id > 0) {
        resultViewModel.loadHistory(currentUser!!.id)  // ← LOAD FROM DB
    }
}
```

---

## 📊 Database Schema

### Tabel: `users`
```
Column          Type            Constraint
─────────────────────────────────────────
id              INTEGER         PRIMARY KEY AUTO INCREMENT
name            TEXT            NOT NULL
createdAt       INTEGER         DEFAULT CURRENT_TIMESTAMP
updatedAt       INTEGER         DEFAULT CURRENT_TIMESTAMP
```

### Tabel: `bmi_history`
```
Column          Type            Constraint
─────────────────────────────────────────
id              INTEGER         PRIMARY KEY AUTO INCREMENT
uniqueId        TEXT            NOT NULL
userId          INTEGER         NOT NULL (FK → users.id)
timestamp       INTEGER         NOT NULL
height          REAL            NOT NULL
weight          REAL            NOT NULL
bmi             REAL            NOT NULL
category        TEXT            NOT NULL
idealWeightMin  REAL            NOT NULL
idealWeightMax  REAL            NOT NULL
createdAt       INTEGER         DEFAULT CURRENT_TIMESTAMP
```

---

## 🔧 Setup & Dependencies

### build.gradle.kts
```kotlin
plugins {
    id("org.jetbrains.kotlin.kapt")  // ← PENTING untuk Room
}

dependencies {
    val roomVersion = "2.6.1"
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

---

## 📱 Screen Flow

```
┌─────────────┐
│  SplashScreen │
└─────────────┘
      │
      ├─→ [First Launch] Dialog Input Nama
      │        └─→ Save ke Database
      │
      └─→ [Existing User] Skip Dialog
           
┌─────────────────────────────────────┐
│  HomeScreen (with user greeting)    │
│  Input: Height, Weight              │
│  Button: Hitung BMI                 │
└─────────────────────────────────────┘
      │
      ├─→ ResultScreen (tampilkan hasil)
      │        └─→ Save history ke database
      │
      └─→ HistoryScreen (load dari database)
           └─→ HistoryDetailScreen
           
┌─────────────────────────────────────┐
│  ProfileScreen                      │
│  Tampilkan nama user dari database  │
└─────────────────────────────────────┘
```

---

## 💾 Data Persistence

### Saat App Launch
1. **Load User Data**
   ```kotlin
   // Di UserViewModel.loadCurrentUser()
   val user = userRepository.getLatestUser()
   ```

2. **Check if User Exists**
   ```kotlin
   if (user != null) {
       // User sudah ada, skip dialog
       _currentUser.value = user
   } else {
       // User baru, tampilkan dialog input
       _showNameInput.value = true
   }
   ```

### Saat User Input Nama
1. **Insert ke Database**
   ```kotlin
   val insertedId = userRepository.insertUser(name)
   ```

2. **Load dari Database**
   ```kotlin
   val savedUser = userRepository.getLatestUser()
   ```

### Saat BMI Calculation
1. **Save to Database**
   ```kotlin
   bmiRepository.saveBMI(userId, summary)
   ```

2. **Load History dari Database**
   ```kotlin
   val history = bmiRepository.getBMIHistoryByUser(userId)
   ```

---

## 🔍 Testing Data Persistence

### Test 1: First Launch (No User)
1. Uninstall app / Clear app data
2. Launch app
3. ✅ Dialog input nama harus muncul
4. Input nama & klik "Lanjut"
5. ✅ Nama tersimpan di database `users`

### Test 2: Second Launch (User Exists)
1. Close app
2. Reopen app
3. ✅ Dialog input nama TIDAK muncul
4. ✅ User greeting menampilkan nama yang tersimpan

### Test 3: BMI History
1. Input height & weight di HomeScreen
2. Click "Hitung BMI"
3. ✅ Result tersimpan di database `bmi_history`
4. Navigate to HistoryScreen
5. ✅ History muncul dari database

---

## 🚀 Key Features

✅ **Automatic User Detection** - Cek database saat launch
✅ **One-time Name Input** - Dialog hanya muncul first launch
✅ **Persistent History** - Semua BMI calculation tersimpan per user
✅ **Coroutine-based** - Async database operations
✅ **Flow-based** - Real-time history updates
✅ **Auto Mapping** - Entity ↔ Domain model conversion

---

## 📝 Important Notes

1. **Database Location**
   - File: `/data/data/af.mobile.mybmi/databases/mybmi_database`
   - Only accessible in debug/emulator (not from PC)

2. **Room Features Used**
   - `@Database` - Define database schema
   - `@Entity` - Define tables
   - `@Dao` - Database operations
   - `suspend` - Coroutine support
   - `Flow<T>` - Reactive queries

3. **Coroutines**
   - Semua database operations di `viewModelScope`
   - Non-blocking UI updates
   - Proper cancellation handling

4. **Data Flow**
   ```
   UI (Composable)
      ↓
   ViewModel (State Management)
      ↓
   Repository (Business Logic)
      ↓
   DAO (Database Access)
      ↓
   Room/SQLite (Persistence)
   ```

---

## 🎓 Architecture Pattern

### MVVM + Repository Pattern
```
┌──────────────────┐
│   Composable     │ (UI Layer)
│   (Screen)       │
└────────┬─────────┘
         │
┌────────▼──────────┐
│   ViewModel       │ (Presentation Logic)
│   + State Flow    │
└────────┬──────────┘
         │
┌────────▼──────────┐
│   Repository      │ (Business Logic)
└────────┬──────────┘
         │
┌────────▼──────────┐
│   DAO/Database    │ (Data Access)
└────────┬──────────┘
         │
┌────────▼──────────┐
│   Room/SQLite     │ (Local Storage)
└──────────────────┘
```

---

## ✨ Next Steps (Optional Enhancements)

1. **Add Migration** - Database version upgrade support
2. **Backup/Restore** - Export user data
3. **Delete User** - Clear all user data
4. **Search History** - Filter BMI records by date
5. **Statistics** - Calculate average BMI, trends
6. **Sync to Cloud** - Firebase Firestore integration

---

**Status: ✅ IMPLEMENTED**
- SQLite database dengan Room ORM
- User management (insert, load, update)
- BMI history tracking per user
- Persistent storage confirmed
- Production-ready architecture


