package rx.dagger.mlunushortages.infrastructure

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PowerOffAlarmReceiver() : BroadcastReceiver(), KoinComponent {

    private val notifier: Notifier by inject()

    override fun onReceive(context: Context?, intent: Intent?) {
        notifier.notifyTenMinutesBeforePowerOff()
    }
}