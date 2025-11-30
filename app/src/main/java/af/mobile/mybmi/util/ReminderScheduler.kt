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

        // Hitung waktu trigger berikutnya
        val calendar = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, dayOfMonth)
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }

        // Jika tanggal yang dipilih sudah lewat bulan ini (misal hari ini tgl 15, user pilih tgl 10),
        // maka jadwalkan untuk bulan depan.
        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.MONTH, 1)
        }

        // Set Alarm
        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
            Log.d("ReminderScheduler", "Alarm diset untuk: ${calendar.time}")
        } catch (e: SecurityException) {
            e.printStackTrace()
            // Di Android 13+, perlu handle permission SCHEDULE_EXACT_ALARM jika crash
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