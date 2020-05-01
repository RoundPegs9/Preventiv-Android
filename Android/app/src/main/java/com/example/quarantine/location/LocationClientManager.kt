package com.example.quarantine.location

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.example.quarantine.AppPreference
import com.example.quarantine.Utils
import com.example.quarantine.bluetooth.foreground.AutoStartUpBootReceiver
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import java.text.DateFormatSymbols
import java.util.*


object LocationClientManager {
    private lateinit var locationRequest:LocationRequest
    private lateinit var fusedLocationProviderClient:FusedLocationProviderClient

    private const val TAG = "LOCATION_MANAGER"
    fun updateLocation(context: Context):FusedLocationProviderClient
    {
        buildLocationRequest()
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context)
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntent(context))
        return fusedLocationProviderClient
    }

    /**
     * Check for wifi connection
     */
    fun getAddress(context: Context, latitude:Double, longitude:Double): String? {
        if(Utils.isInternetAvailable(context))
        {
            val geocoder = Geocoder(context, Locale.getDefault())
            return geocoder.getFromLocation(latitude, longitude, 1)[0].getAddressLine(0)
        }
        return null
    }

    private fun getPendingIntent(context: Context): PendingIntent? {
        val intent = Intent(context, AutoStartUpBootReceiver::class.java)
        Log.i(TAG, intent.toString())

        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun buildLocationRequest()
    {
        locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 60000
        locationRequest.smallestDisplacement = 1000F
    }

    /**
     * Get Current Location
     * Steps: 1. If current location not available, get last known location.
     * @param applicationContext
     * @param isThisMe : Boolean indicating whether the GPS coordinates to store
     * come from the connection or from updating 0-th Degree Profile Connection
     */
    fun getLocation(applicationContext:Context, isThisMe: Boolean): Task<Location>? {
        val mFusedLocationProviderClient = LocationClientManager.updateLocation(applicationContext)
        val appPreference = AppPreference(applicationContext)

        return mFusedLocationProviderClient.lastLocation?.addOnCompleteListener{
            if(it.isSuccessful)
            {
                appPreference.addLatLng(it.result?.latitude as Double, it.result?.longitude as Double)
                val reverseAddress = getAddress(applicationContext, it.result?.latitude as Double, it.result?.longitude as Double)
                Log.i(TAG, "Within Callback: $reverseAddress")
                if(!isThisMe)
                {
                    appPreference.addReverseGeoLocation(reverseAddress, getNowDateAsMonthAndDate())//store only other people's geolocation
                }
            }

        }
    }
    fun getNowDateAsMonthAndDate(): String {
        val date = Date()
        return DateFormatSymbols.getInstance().shortMonths[date.month] + " ${date.date}"
    }
}