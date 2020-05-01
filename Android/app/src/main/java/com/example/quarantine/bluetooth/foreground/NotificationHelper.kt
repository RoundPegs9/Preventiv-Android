package com.example.quarantine.bluetooth.foreground


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.PowerManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.quarantine.R
import com.example.quarantine.activities.SplashActivity
import java.util.*

object NotificationHelper {

    private const val CHANNEL_ID = "com.example.quarantine.foreground.notification_channel"
    private const val CHANNEL_NAME = "Infection Attention"


    private lateinit var mNotification: Notification
    private fun createChannel(context: Context?) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel =
                NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
            notificationChannel.enableVibration(true)
            notificationChannel.setShowBadge(true)
            notificationChannel.setBypassDnd(true)
            notificationChannel.importance = importance

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.parseColor("#0063d8")
            notificationChannel.description =
                "Reminder Notification will be sent through this channel."
            notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }


    fun onHandleEvent(title: String, description: String, context: Context?) {

        //Create Channel
        createChannel(context)


        val id = Random().nextInt( 1001) //generates a random id from 0 - 1000 for Service.


        if (title.isNotEmpty()) {

            val notificationManager: NotificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val countDownIntent = Intent(context, SplashActivity::class.java)

            countDownIntent.putExtra("title", title)
            countDownIntent.putExtra("description", description)  //Important

            countDownIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//            context.startActivity(countDownIntent)


            val pendingIntent = PendingIntent.getActivity(
                context,
                id,
                countDownIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )


            //WAKE SCREEN............................................

//            var wakeLock: PowerManager.WakeLock? = null
//
//            val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
//            val isScreenOn = powerManager.isInteractive
//            if (!isScreenOn) {
//
//                wakeLock =
//                    powerManager.newWakeLock(
//                        PowerManager.PARTIAL_WAKE_LOCK,
//                        "EC:Event_Reminder"
//                    )
//                wakeLock.acquire(10000)
//
//            }


            //END...................................
            mNotification = NotificationCompat.Builder(context, CHANNEL_ID)
//                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(description)
                )
                .setContentText(description).build()


            notificationManager.notify(id, mNotification)

            Log.d("xx_notification_info", "Notified")


//            wakeLock?.release()


        }
    }
}