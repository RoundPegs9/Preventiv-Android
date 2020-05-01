package ml.preventiv.bluetooth.foreground

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build


class AutoStartUpBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {

        if (intent.action == "android.intent.action.GLOBAL_ACTION") {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(Intent(context, FService::class.java))
            } else {
                context.startService(Intent(context, FService::class.java))
            }
        }
    }
}