# ✅ INFO CARD - SESUAIKAN DENGAN RIWAYAT TERAKHIR

## 🎯 What Was Changed

Info Card di HomeScreen sekarang **dinamis** berdasarkan BMI terakhir user!

## 📝 Changes Made

### 1. Add History State
```kotlin
val history by resultViewModel?.history?.collectAsState() ?: remember { mutableStateOf(emptyList()) }
val lastBmi = history.firstOrNull()  // Get latest BMI
```

### 2. Update Info Card

#### Before: Static Tip
```
Always show: "Pertahankan BMI normal antara 18.5-24.9..."
```

#### After: Dynamic Content
```
If user has history:
  - Show: "📊 Hasil Terakhir"
  - Show: Latest BMI value
  - Show: BMI category
  - Show: Personalized advice based on category
  - Color changes based on BMI category:
    * Blue for UNDERWEIGHT
    * Green for NORMAL
    * Orange for OVERWEIGHT
    * Red for OBESE

If no history:
  - Show: "💡 Tip Kesehatan"
  - Show: General health tip
```

## 🎨 Visual Changes

### Card Title
- No history: "💡 Tip Kesehatan" (Green)
- Has history: "📊 Hasil Terakhir" (Colored by BMI category)

### Card Content
- No history: General health advice
- Has history:
  - BMI value (e.g., "BMI Terakhir: 22.5 (Normal Weight)")
  - Personalized advice from BMI category
  
### Card Background Color
- Dynamically colored based on latest BMI category
- Blue for underweight
- Green for normal
- Orange for overweight
- Red for obese

## 🧪 How It Works

1. User opens HomeScreen
2. History loaded from database
3. Latest BMI extracted: `lastBmi = history.firstOrNull()`
4. Info card checks if `lastBmi != null`
5. Shows appropriate content + colors

## 📊 Example Scenarios

### Scenario 1: No History Yet
```
Card Title: 💡 Tip Kesehatan (Green)
Card Color: Green (0.1 alpha)
Content: "Pertahankan BMI normal antara 18.5-24.9..."
```

### Scenario 2: BMI = 22.5 (Normal)
```
Card Title: 📊 Hasil Terakhir (Green)
Card Color: Green (0.1 alpha)
Content: 
  "BMI Terakhir: 22.5 (Normal Weight)"
  "Pertahankan pola makan sehat dan olahraga teratur"
```

### Scenario 3: BMI = 31.0 (Obese)
```
Card Title: 📊 Hasil Terakhir (Red)
Card Color: Red (0.1 alpha)
Content:
  "BMI Terakhir: 31.0 (Obese)"
  "Konsultasi dengan dokter untuk program penurunan berat badan"
```

### Scenario 4: BMI = 16.5 (Underweight)
```
Card Title: 📊 Hasil Terakhir (Blue)
Card Color: Blue (0.1 alpha)
Content:
  "BMI Terakhir: 16.5 (Underweight)"
  "Tingkatkan asupan kalori dengan makanan bergizi seimbang"
```

## ✅ Benefits

✅ More personalized experience
✅ Shows latest BMI at a glance
✅ Provides category-specific advice
✅ Visual feedback via colors
✅ Motivates users to maintain/improve health
✅ Updates automatically as new BMI calculated

## 🚀 BUILD & TEST

```
1. Build: Build → Build Project
2. Run: Run → Run 'app'
3. Test:
   - Open app (no history) → See green "Tip Kesehatan" card
   - Calculate BMI (22.5) → See green "Hasil Terakhir" card with advice
   - Calculate BMI (31.0) → See red "Hasil Terakhir" card with different advice
   - Colors should change based on BMI category
```

---

**STATUS**: ✅ IMPLEMENTED & READY TO TEST


