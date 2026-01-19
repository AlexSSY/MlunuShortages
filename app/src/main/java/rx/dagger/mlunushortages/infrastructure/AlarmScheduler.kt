package rx.dagger.mlunushortages.infrastructure

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import rx.dagger.mlunushortages.domain.PeriodWithoutElectricity
import java.time.ZoneId

class AlarmScheduler(
    private val context: Context
) {
    private val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun schedule(periods: List<PeriodWithoutElectricity>) {
        cancelAll()

        periods.forEachIndexed { index, period ->
            val triggerAt =
                period.from
                    .minusMinutes(10)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

            if (triggerAt > System.currentTimeMillis()) {
                val canExact =
                    Build.VERSION.SDK_INT < Build.VERSION_CODES.S ||
                            alarmManager.canScheduleExactAlarms()

                if (canExact) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAt,
                        pendingIntent(index)
                    )
                    Log.d("AlarmScheduler", "scheduled: $index - $triggerAt")
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAt,
                        pendingIntent(index)
                    )
                    Log.d("AlarmScheduler", "scheduled INEXACT alarm")
                }
            }
        }
    }

    private fun pendingIntent(
        id: Int
    ): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            id,
            alarmIntent(),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun cancelAll() {
        for (i in 0 until 48) {
            val pi = PendingIntent.getBroadcast(
                context,
                i,
                alarmIntent(),
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            ) ?: continue

            alarmManager.cancel(pi)
            pi.cancel()
            Log.d("AlarmScheduler", "cancelled alarm $i")
        }
    }

    private fun alarmIntent(): Intent =
        Intent(context, PowerOffAlarmReceiver::class.java)
            .apply { action = "rx.dagger.mlunushortages.POWER_OFF_ALARM" }
}
