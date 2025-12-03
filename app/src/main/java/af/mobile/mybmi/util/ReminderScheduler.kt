package af.mobile.mybmi.util

import af.mobile.mybmi.receiver.ReminderReceiver
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId

object ReminderScheduler {

    fun scheduleMonthlyReminder(context: Context, dayOfMonth: Int, hour: Int = 9, minute: Int = 0) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, ReminderReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            101,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // --- LOGIKA MODERN (java.time) ---
        val now = LocalDateTime.now()

        // Coba buat jadwal di bulan & tahun INI dulu
        // Gunakan .coerceIn agar tanggal tidak crash (misal tgl 31 di Februari -> jadi tgl 28/29)
        var targetDate = LocalDate.now()
            .withDayOfMonth(dayOfMonth.coerceIn(1, LocalDate.now().lengthOfMonth()))

        var targetDateTime = LocalDateTime.of(targetDate, LocalTime.of(hour, minute))

        // Jika jadwal bulan ini sudah lewat, majukan ke BULAN DEPAN
        if (targetDateTime.isBefore(now)) {
            val nextMonth = now.plusMonths(1)
            // Handle lagi validasi tanggal untuk bulan depan
            val maxDayNextMonth = nextMonth.toLocalDate().lengthOfMonth()
            val validDay = dayOfMonth.coerceIn(1, maxDayNextMonth)

            targetDateTime = nextMonth.withDayOfMonth(validDay)
                .withHour(hour).withMinute(minute).withSecond(0)
        }

        // Konversi ke Millis untuk AlarmManager
        val triggerAtMillis = targetDateTime
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        try {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
            Log.d("ReminderScheduler", "Alarm diset untuk: $targetDateTime")
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

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
    }
}