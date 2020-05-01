package ml.preventiv

import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.location.LocationManager
import android.net.ConnectivityManager
import androidx.core.location.LocationManagerCompat
import kotlin.math.floor

object Utils {
    fun isBluetoothAvailable():Boolean
    {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if(bluetoothAdapter != null && bluetoothAdapter.isEnabled && bluetoothAdapter.state == BluetoothAdapter.STATE_ON)
        {
            return true
        }
        return false
    }

    fun isLocationTurnedOn(context: Context):Boolean
    {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return LocationManagerCompat.isLocationEnabled(locationManager)
    }

    fun isInternetAvailable(context: Context):Boolean
    {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(connectivityManager.activeNetworkInfo != null)
        {
            return connectivityManager.activeNetworkInfo.isConnected
        }
        return false
    }

    /**
     * Returns current Time in milliseconds
     * @return long
     */
    fun getTimeStamp():Long
    {
        return floor((System.currentTimeMillis() / 1000).toDouble()).toLong()
    }
}