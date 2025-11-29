# 🚀 Quick Start Guide - SQLite Testing

## ✅ Implementasi Selesai!

Aplikasi myBMI sekarang sudah menggunakan **SQLite** untuk menyimpan data lokal. Semua requirement sudah terpenuhi:

1. ✅ **First Launch** - Aplikasi minta input nama user
2. ✅ **Persistent** - Nama user disimpan di database
3. ✅ **No Re-prompt** - Jika user sudah ada, dialog tidak muncul lagi
4. ✅ **History** - Semua BMI calculation disimpan per user

---

## 🧪 Cara Testing

### Test 1: First Launch (Tanpa User)

**Step-by-step:**

1. Buka Android Studio
2. Clear app data:
   ```
   adb shell pm clear af.mobile.mybmi
   ```
   Atau di emulator: Settings → Apps → myBMI → Storage → Clear All Data

3. Run/Debug aplikasi
4. **EXPECTED**: Dialog muncul bertanya "Siapa Nama Kamu?"
5. Input nama: `Ahmad` atau nama apapun
6. Klik `Lanjut`
7. **EXPECTED**: Dialog hilang, user greeting menampilkan `Halo, Ahmad! 👋`

**✅ Hasil**: Nama user tersimpan di database `users` table

---

### Test 2: Second Launch (User Sudah Ada)

**Step-by-step:**

1. Close app
2. Buka lagi (atau tap notification jika ada)
3. **EXPECTED**: Dialog input nama TIDAK muncul
4. **EXPECTED**: Langsung ke Home dengan greeting `Halo, Ahmad! 👋`

**✅ Hasil**: Data user di-load dari database, hanya ditanya 1x

---

### Test 3: BMI Calculation & History Save

**Step-by-step:**

1. Di HomeScreen, input:
   - Tinggi Badan: `170` cm
   - Berat Badan: `65` kg
2. Klik tombol `Hitung BMI`
3. **EXPECTED**: Navigate ke Result Screen, tampilkan hasil BMI
4. Klik `← Kembali` atau navigate ke History
5. **EXPECTED**: Di History Screen, hasil BMI muncul di list

**✅ Hasil**: Hasil BMI disimpan ke database `bmi_history` table

---

### Test 4: Multiple History Entries

**Step-by-step:**

1. Kembali ke Home Screen
2. Input data BMI yang berbeda (mis: 175 cm, 70 kg)
3. Klik `Hitung BMI`
4. Ulangi 2-3x dengan data berbeda
5. Navigate ke History Screen
6. **EXPECTED**: Semua entries muncul dalam list (newest first)

**✅ Hasil**: Multiple records disimpan per user

---

### Test 5: Data Persistence (Most Important!)

**Step-by-step:**

1. Setelah calculate beberapa BMI
2. Force stop app:
   ```
   adb shell am force-stop af.mobile.mybmi
   ```
3. Open app lagi
4. **EXPECTED**: 
   - User greeting masih menampilkan nama (tidak perlu input lagi)
   - History screen masih menampilkan semua record
5. Close dan open multiple times
6. **EXPECTED**: Semua data masih persisten

**✅ Hasil**: Data tersimpan permanen di database

---

### Test 6: Delete History (Optional)

**Step-by-step:**

1. Di History Screen, tap salah satu history
2. **EXPECTED**: Detail screen muncul
3. Cari tombol delete (jika ada)
4. Tap delete
5. **EXPECTED**: Record hilang dari list
6. Close app dan buka lagi
7. **EXPECTED**: Record tetap hilang (perubahan persisten)

---

## 📱 Database Inspection (Debug)

Jika ingin lihat data langsung di database (emulator):

### Via Android Studio
1. View → Tool Windows → Device File Explorer
2. Navigate: `/data/data/af.mobile.mybmi/databases/`
3. Download `mybmi_database` file
4. Open dengan SQLite Browser

### Via adb shell
```bash
adb shell sqlite3 /data/data/af.mobile.mybmi/databases/mybmi_database

# Di sqlite3 shell:
.tables                              # Lihat semua tabel
SELECT * FROM users;                 # Lihat user data
SELECT * FROM bmi_history;           # Lihat BMI history
SELECT COUNT(*) FROM bmi_history;    # Count total records
.exit                                # Keluar
```

---

## 🔍 Expected Database Schema

### Tabel: `users`
```
id  │  name      │  createdAt        │  updatedAt
────┼────────────┼───────────────────┼──────────────
1   │  Ahmad     │  1701234567890    │  1701234567890
```

### Tabel: `bmi_history`
```
id  │  uniqueId          │  userId │  height │  weight │  bmi   │  category  │  ...
────┼────────────────────┼─────────┼─────────┼─────────┼────────┼────────────┼─────
1   │  uuid-12345        │  1      │  170    │  65     │  22.5  │  NORMAL    │  ...
2   │  uuid-67890        │  1      │  175    │  70     │  22.9  │  NORMAL    │  ...
3   │  uuid-13579        │  1      │  180    │  75     │  23.1  │  NORMAL    │  ...
```

---

## 🎯 Expected Flow

```
┌─────────────────┐
│  App Start      │
└────────┬────────┘
         ↓
    [Check DB]
         │
    ┌────┴────┐
    │          │
   NO         YES (User exists)
    │          │
    ↓          ↓
[Dialog]   [Skip Dialog]
  Input       │
   Name       ├─→ Load User from DB
    │         │
    ↓         ↓
[Save to DB]  [Show HomeScreen]
    │         with greeting
    ├────────┘
    ↓
[HomeScreen]
  User greeting ✅
    │
    ├─→ Input Height & Weight
    │
    ├─→ Calculate BMI
    │
    ├─→ [Save to DB] ✅
    │
    └─→ [HistoryScreen]
         Load from DB ✅
```

---

## 📋 Checklist Sebelum Production

- [ ] Test first launch (no user)
- [ ] Test second launch (user exists)
- [ ] Test multiple BMI entries
- [ ] Test history loads correctly
- [ ] Test data persists after force stop
- [ ] Test on different devices/emulator versions
- [ ] Verify database file created correctly
- [ ] Check no crashes during data operations
- [ ] Performance test with 100+ history entries

---

## 🐛 Troubleshooting

### Problem: Dialog muncul setiap kali launch
**Solution**: 
- Database might not be persisting
- Check: `adb shell sqlite3 ... SELECT * FROM users;`
- Verify kapt plugin in build.gradle.kts

### Problem: History tidak muncul
**Solution**:
- User ID might be 0 or null
- Check: `userId > 0` condition in code
- Verify userId passed to repository

### Problem: App crashes when saving
**Solution**:
- Check Logcat for SQLite errors
- Verify all suspends are called from viewModelScope
- Check database schema (table names, columns)

### Problem: Can't see database file
**Solution**:
- Use `adb shell sqlite3` command
- Or download via Device File Explorer in Android Studio
- Make sure emulator is running and connected

---

## 📚 References

- **Room Documentation**: https://developer.android.com/training/data-storage/room
- **SQLite**: https://www.sqlite.org/docs.html
- **Kotlin Coroutines**: https://kotlinlang.org/docs/coroutines-overview.html
- **Android Data Storage**: https://developer.android.com/training/data-storage

---

## ✨ Next Features (Optional)

1. **Statistics**
   - Average BMI
   - BMI trend chart
   - Monthly comparisons

2. **Export/Backup**
   - Export history as CSV
   - Backup to cloud

3. **Advanced Search**
   - Filter by date range
   - Filter by category
   - Sort options

4. **User Management**
   - Multiple users support
   - Switch user feature
   - Delete user & history

---

**Status**: ✅ **READY TO TEST**

Semua fitur SQLite sudah implemented dan siap di-test!

Jika ada pertanyaan atau masalah, check:
- `SQLITE_IMPLEMENTATION.md` - Dokumentasi lengkap
- `IMPLEMENTATION_CHECKLIST.md` - Detail implementasi

Selamat testing! 🎉


