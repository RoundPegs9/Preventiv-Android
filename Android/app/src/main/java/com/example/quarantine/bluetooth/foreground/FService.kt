package com.example.quarantine.bluetooth.foreground

import android.app.*
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.quarantine.AppPreference
import com.example.quarantine.R
import com.example.quarantine.Utils
import com.example.quarantine.activities.MainActivity
import com.example.quarantine.bluetooth.gatt.GattClientManager
import com.example.quarantine.bluetooth.gatt.GattServerManager
import com.example.quarantine.firebase.Firestore
import com.example.quarantine.location.LocationClientManager
import com.example.quarantine.location.NetworkManager
import no.nordicsemi.android.support.v18.scanner.ScanRecord
import java.nio.charset.Charset
import java.util.*
import kotlin.math.pow


class FService: Service(),
    GattServerManager.GattServerCallbacks,
    GattClientManager.GattServerClientCallback{
    private val mHandler: Handler? = Handler()
    private var clientManager: GattClientManager? = null
    private lateinit var mGattServerManager: GattServerManager
    private lateinit var networkManager:NetworkManager


    private val firebaseFirestore = Firestore.newInstance()

    override fun onCreate() {
        super.onCreate()
        mGattServerManager = GattServerManager(this, this)
        startForegroundService()
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null;
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        Log.d(TAG, "ON START COMMAND")
        if (intent != null) {

            when (intent.action) {

                ACTION_STOP_FOREGROUND_SERVICE -> {
                    stopService()
                }

                ACTION_OPEN_APP -> openAppHomePage()
            }
        }
        return START_STICKY;
    }

    private fun checkPermission(): Boolean {
        val getPermission =  AppPreference(this).getPermission()
        Log.i(TAG, getPermission.toString())
        return getPermission
    }
    private fun openAppHomePage() {

        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val chan = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
            )

            chan.lightColor = Color.BLUE
            chan.lockscreenVisibility = NotificationCompat.VISIBILITY_PRIVATE
            val service = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            service.createNotificationChannel(chan)

        }
    }


    /* Used to build and start foreground service. */
    private fun startForegroundService() {
        /**
         * Network Periodic update/stuff.
         * Current version: 0.9.0
         * Next release should convert into a standalone class and implement main functions in here.
         */
        networkManager = NetworkManager.newInstance(applicationContext)
        networkThread()

        /**
         * Have the BLE stuff here... Both Activity stuff...
         */
        //Create Notification channel for all the notifications sent from this app.
        createNotificationChannel()

        // Start foreground service.
        startFService()

    }

    private fun networkThread()
    {
        val handler = Handler()
        val delay = 60000 //5-minutes (debug = 1-minute)
        val appPreference = AppPreference(applicationContext)

        handler.postDelayed(object : Runnable {
            override fun run() {
                //do something
                if(Utils.isInternetAvailable(applicationContext))
                {
                    val cacheGeoAddress = appPreference.getListGeolocation()

                    val cacheReverseGeoAddress = appPreference.getReverseGeoLocation()

                    Log.i(TAG, "LatLng: ${cacheGeoAddress.size}, $cacheGeoAddress")
                    Log.i(TAG, "HashMap: ${cacheReverseGeoAddress!!.size}, $cacheReverseGeoAddress")

                    if(cacheGeoAddress.isNotEmpty() && cacheGeoAddress.size != cacheReverseGeoAddress.size)
                    {
                        cacheGeoAddress.forEach {
                            val reverseAddress = LocationClientManager.getAddress(applicationContext, it.value.latitude, it.value.longitude)
                            appPreference.addReverseGeoLocation(reverseAddress, LocationClientManager.getNowDateAsMonthAndDate())
                            Log.i(TAG, "Added new GeoLocation from Wifi: $reverseAddress")
                            Log.i(TAG, "Added new GeoLocation from Wifi: ${appPreference.getReverseGeoLocation()}")
                        }
                        //at this point, all coordinates should be converted to reverse geoLocations.
                        //send them over to firebase and flush the database. (DONE)
                    }
                    /**
                     * Firebase to send data.
                     */
                    if(appPreference.getListGeolocation().isNotEmpty())
                    {
                        Log.d(TAG, "Data Debugger: ${appPreference.getListGeolocation()}")
                        //write

                        firebaseFirestore.add("gps", appPreference.getListGeolocation())
                            .addOnCompleteListener {
                                Log.i(TAG, "Added with Document id: ${it.result!!}")
                                appPreference.clearGPS() //clears all the GPS coordinates
                            }
                            .addOnFailureListener {
                                Log.e(TAG, "Error Writing to collection = GPS, with exception: ${it.message}")
                            }
                    }

                    /**
                     * Manage update and sending of Alerts
                     */
                    networkManager.getAlerts()

                    /**
                     * Filtering direction and n-th degree based connections indexed on timestamps
                     */
                    appPreference.clear2WeekPeriodDIRECT()
                    appPreference.clear2WeekPeriodONLINE()

                }
                handler.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())
    }



    private fun startFService() {

        val description = getString(R.string.msg_notification_service_desc)
        val title = String.format(
            getString(R.string.title_foreground_service_notification),
            getString(R.string.app_name)
        )

        startForeground(SERVICE_ID, getStickyNotification(title, description))
        IS_RUNNING = true
        /**
         * BLE
         */
        mGattServerManager.startServer()
        startScan()

    }


    private fun getStickyNotification(title: String, message: String): Notification? {

        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, Intent(), 0)

        // Create notification builder.
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
        // Make notification show big text.
        val bigTextStyle = NotificationCompat.BigTextStyle()
        bigTextStyle.setBigContentTitle(title)
        bigTextStyle.bigText(message)
        // Set big text style.
        builder.setStyle(bigTextStyle)
        builder.setWhen(System.currentTimeMillis())
        builder.setSmallIcon(R.drawable.ic_launcher_background)
        //val largeIconBitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_alarm_on)
        //builder.setLargeIcon(largeIconBitmap)
        // Make the notification max priority.
        builder.priority = NotificationCompat.PRIORITY_DEFAULT

        // Make head-up notification.
        builder.setFullScreenIntent(pendingIntent, true)


        // Add Open App button in notification.
        val openAppIntent = Intent(applicationContext, FService::class.java)
        openAppIntent.action = ACTION_OPEN_APP
        val pendingPlayIntent = PendingIntent.getService(applicationContext, 0, openAppIntent, 0)
        val openAppAction = NotificationCompat.Action(
            android.R.drawable.ic_menu_view,
            getString(R.string.lbl_btn_sticky_notification_open_app),
            pendingPlayIntent
        )

        builder.addAction(openAppAction)



        // Build the notification.
        return builder.build()

    }



    private fun notifyNextEvent(distance:Double) {

        NotificationHelper.onHandleEvent(
            getString(R.string.title_gen_notification),
            "Someone near you, approximately $distance meters apart has been tested positive with COVID-19. Distance yourself!",
            applicationContext
        )
    }


    private fun stopService() {
        // Stop foreground service and remove the notification.
        stopForeground(true)
        // Stop the foreground service.
        stopSelf()

        IS_RUNNING = false
    }


    override fun onDestroy() {
        IS_RUNNING = false
        mHandler?.removeCallbacks(null)
        /**
         * BLE
         */
        mGattServerManager.shutdownServer()
        //Stop any active scans
        clientManager?.stopScan()
        clientManager?.disconnect()
        super.onDestroy()
    }
    /**
     * Call to Start Scanning
     */
    private fun startScan()
    {
        if (checkPermission()) {
            clientManager = GattClientManager(this, this)
            clientManager?.startScan()
            Log.i(TAG, "Scan started")
        }
    }
    override fun onNewDeviceFound(device: BluetoothDevice, rssi: Int, scanRecord: ScanRecord) {
        val appPreference = AppPreference(applicationContext)

        val data = scanRecord.getManufacturerSpecificData(314)?.let { String(it, Charsets.UTF_8) }

        val uuid = data?.substring(0, endIndex = data.length - 2)//gets the uuid from data
        val isInfected = data?.substring(data.length - 1, data.length)?.toInt() //gets the infection bool

        val code = AppPreference(this).addUUID(uuid.toString())

        Log.i(TAG, appPreference.getStringGeolocation())

        if(code && isInfected == 1) //success. notify them.
        {
            /**
             * Geolocation auto add
             */
            LocationClientManager.getLocation(applicationContext, false)
            /**
             * Set notification on POSITIVE
             */
            if(!appPreference.isOnlineConnection(uuid))
            {
                notifyNextEvent(getProximity(rssi))
            }
            appPreference.addInfectedIterator() //add total number of infected connections made
            Log.i(TAG, "New UUID: $data")
        }
        else
        {
            Log.i(TAG, "UUID exists: $data")//already exists. log it.
        }

    }

    private fun getProximity(rssi: Int):Double
    {
        /**
         * For now, assume TxPowerLevel = 2 (up to 10m)
         */
        return 10.0.pow((-84 - rssi) / (20) )
    }
    override fun onDeviceConnected(device: BluetoothDevice) {
//        supportActionBar?.subtitle = "Connected to " + device.address
    }

    override fun onDeviceDisconnected(device: BluetoothDevice) {
//        supportActionBar?.subtitle = "Disconnected"
    }

    override fun onCharacteristicRead(uuid: UUID, bytes: ByteArray) {
        val value = String(bytes, Charset.forName("UTF-8"))
        Log.i(TAG, "onCharacteristicRead: $value")
    }


    override fun serverIsReady(ready: Boolean, errorCode: Int, errorMessage: String?) {
//        if (ready)
//            title = "GATT Server Ready"
//        else
//            title = "GATT Server Error: " + errorMessage
    }

    companion object {

        const val TAG = "FOREGROUND_SERVICE"
        const val ACTION_STOP_FOREGROUND_SERVICE = "ACTION_STOP_FOREGROUND_SERVICE"
        const val ACTION_OPEN_APP = "ACTION_OPEN_APP"

        private const val CHANNEL_ID: String = "1001"
        private const val CHANNEL_NAME: String = "Event Tracker"
        private const val SERVICE_ID: Int = 1

        var IS_RUNNING: Boolean = false
    }
}