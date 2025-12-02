package af.mobile.mybmi.util

import af.mobile.mybmi.receiver.ReminderReceiver
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.util.Calendar

object ReminderScheduler {

    // Fungsi untuk menyalakan pengingat
    fun scheduleMonthlyReminder(context: Context, dayOfMonth: Int, hour: Int = 9, minute: Int = 0) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)

        // Gunakan FLAG_IMMUTABLE agar aman di Android 12+
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val now = System.currentTimeMillis()

        // Setup Kalender Target
        val targetCalendar = Calendar.getInstance().apply {
            // Reset dulu ke waktu sekarang
            timeInMillis = now

            // Set ke tanggal & jam yang diinginkan (di bulan ini dulu)
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // --- LOGIKA UTAMA ---
        if (targetCalendar.timeInMillis <= now) {
            // Jika sudah lewat, jadwalkan untuk BULAN DEPAN
            targetCalendar.add(Calendar.MONTH, 1)
        }

        // Set Alarm
        try {
            // Menggunakan setExactAndAllowWhileIdle agar alarm tetap bunyi meski HP dalam mode Doze (tidur)
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                targetCalendar.timeInMillis,
                pendingIntent
            )
            Log.d("ReminderScheduler", "Alarm diset untuk: ${targetCalendar.time}")
        } catch (e: SecurityException) {
            e.printStackTrace()
            Log.e("ReminderScheduler", "Gagal set alarm: Izin tidak diberikan")
        }
    }

    // Fungsi untuk mematikan pengingat
    fun cancelReminder(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.cancel(pendingIntent)
        Log.d("ReminderScheduler", "Alarm dibatalkan")
    }
}