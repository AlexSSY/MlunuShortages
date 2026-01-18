package rx.dagger.mlunushortages.infrastructure

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import rx.dagger.mlunushortages.domain.PeriodWithoutElectricity
import java.time.LocalDateTime
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
//                period.from
                LocalDateTime.now().plusSeconds(60)
                    .minusMinutes(3)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli()

            if (triggerAt > System.currentTimeMillis()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (!alarmManager.canScheduleExactAlarms()) {
                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            triggerAt,
                            pendingIntent(index, period)
                        )
                        Log.d("AlarmScheduler", "scheduled INEXACT alarm")
                        return@forEachIndexed
                    }
                }
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAt,
                    pendingIntent(index, period)
                )

                Log.d("AlarmScheduler", "scheduled: $index - $triggerAt")
                return
            }
        }
    }

    private fun pendingIntent(
        id: Int,
        period: PeriodWithoutElectricity
    ): PendingIntent =
        PendingIntent.getBroadcast(
            context,
            id,
            Intent(context, PowerOffAlarmReceiver::class.java)
                .putExtra("start", period.from.toString()),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun cancelAll() {
        // отменить старые алармы
    }
}
