package ml.preventiv

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.lang.Exception
import java.util.*
import kotlin.collections.HashMap

const val PREFERENCE_NAME = "Preventiv"
const val PREFERENCE_APP_LAUNCH_CHOICE = "MuxOnLaunch"
const val CONFIDENCE = "CONFIDENCE"
const val UUID_TOKEN = "UUID"
const val UUID_CONNECTIONS = "CONNECTIONS"
const val PERMISSION_GRANTED = "PERMISSION_GRANTED"
const val GEOLOCATION = "GEOLOCATION"
const val REVERSE_GEOLOCATION = "REVERSE_GEOLOCATION"
const val IS_ALERT_ADDED = "BOOL_ALERT_TAB_ADD"
const val CONNECTION_ITERATOR = "TOTAL_CONNECTION_COUNT_ITERATOR"
const val INFECTED_ITERATOR = "TOTAL_INFECTION_COUNT_ITERATOR"
const val N_DEGREE_CONNECTION = "Nth_DEGREE_CONNECTION"
const val SENT_NOTIFICATION = "HAS_SENT_NOTIFICATION"
const val N_DEGREE_CONNECTION_FIREBASE = "N_DEGREE_CONNECTION_FIREBASE"
const val ONBOARDING = "ONBOARDING"

class AppPreference(context:Context) {
    private var preference: SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun setOnAppLaunchInfo(mux:Int){
        val editor = preference.edit()
        editor.putInt(PREFERENCE_APP_LAUNCH_CHOICE, mux)

        editor.apply()
    }
    fun getOnAppLaunchInfo():Int{
//        clearStorage()
        return preference.getInt(PREFERENCE_APP_LAUNCH_CHOICE,0)
    }
    fun setConfidence(set_confidence:Float){
        val editor = preference.edit()
        editor.putFloat(CONFIDENCE, set_confidence)
        editor.apply()
    }
    fun getConfidence():Float{
        return preference.getFloat(CONFIDENCE, -9F)
    }

    /**
     * Sets once in a lifetime random generated UUID
     */
    fun setUUID()
    {
        val editor = preference.edit()
        if(getUUID() == null || getUUID() == "NIL")
        {
            editor.putString(UUID_TOKEN, UUID.randomUUID().toString())
            editor.apply()
        }
    }
    fun getUUID(): String? {
        return preference.getString(UUID_TOKEN, "NIL")
    }

    /**
     * Danger!
     * Use only for debugging purposes.
     * Clear all connections. Hard Reset on Connections local storage.
     */
    fun clearConnections()
    {
        val editor = preference.edit()
        editor.remove(UUID_CONNECTIONS).apply()
    }
    /**
     * Clears EVERYTHING.
     * MEGA DANGER. Use only when cache storage causes new install problems
     */
    fun clearStorage()
    {
        preference.edit().clear().apply()
    }

    /**
     * Stores list of UUID (last 10 digits only)
     */
    fun addUUID(uuid: String?):Boolean
    {
        if(!isConnection(uuid))
        {
            val editor = preference.edit()
            val getConnections = getConnections()
            getConnections?.add("$uuid-${Utils.getTimeStamp()}")
            editor.putStringSet(UUID_CONNECTIONS, getConnections)

            editor.apply()

            addConnectionIterator() //add total number of connections made

            return true
        }
        return false //connection probably already exists

    }

    /**
     * Checks if the following UUID is part of the connection.
     * Helper Function for addUUID.
     */
    fun isConnection(uuid: String?):Boolean
    {
        val connections = getConnections()
        val result = connections?.find {
            uuid!! in it
        }
        if(result != null)
        {
            return true
        }
        return false
    }

    /**
     * Finds the time of interaction (in milliseconds)
     * Uses isConnection(uuid:String?):Boolean for filtering purposes
     */
    fun findTimeOfInteraction(uuid: String?):Long
    {
        if (isConnection(uuid))
        {
            val connections = getConnections()
            val result = connections?.find {
                uuid!! in it
            }
            return result!!.split("-")[1].toLong()
        }
        return (-1).toLong() //if nothing found
    }

    /**
     * Gets list of all connections, delimiter = '|' Type = String.
     * Format: ...|UUID-UNIX|UUID-UNIX|... (starts with UUID, and not '|', MutableSet)
     */
    fun getConnections():MutableSet<String>?
    {
        return preference.getStringSet(UUID_CONNECTIONS, mutableSetOf())
    }

    /**
     * Stores permission on whether can use Coarse Location for BLE scans
     */
    fun getPermission():Boolean
    {
        return preference.getBoolean(PERMISSION_GRANTED, false)
    }

    /**
     * Sets the permission.
     */
    fun setPermission(mPermission : Boolean)
    {
        val editor = preference.edit()
        editor.putBoolean(PERMISSION_GRANTED, mPermission)
        editor.apply()
    }


    /**
     * Adds a Geolocation to local storage
     * To be guaranteed unique UUID association
     * Loose connection association.
     * @param: Lat : long, Lng : long
     */
    fun addLatLng(lat: Double, lng: Double)
    {
        val editor = preference.edit()
        var geoAddressString = getStringGeolocation()
        geoAddressString += "${lat}_$lng|"//format = lat_lng|

        editor.putString(GEOLOCATION, geoAddressString)
        editor.apply()
    }

    /**
     * Gets a list of all LatLng pairs
     * Format: lat_lng|lat_lng|...
     * @return string of latLng pair
     */
    fun getStringGeolocation():String {
        return preference.getString(GEOLOCATION, "")!!
    }

    /**
     * Returns a list of geolocation
     * Runtime - O(n) {Amortized Analysis}
     * @return List of LatLng pair (double)
     */
    fun getListGeolocation(): HashMap<String, LatLng>
    {
        val mutableSetAddress = hashMapOf<String,LatLng>()
        var iterator = 0
        val geolocationString = getStringGeolocation()
        geolocationString.split("|").forEach {
            try {
                val lat = it.split("_")[0].toDouble()
                val lng = it.split("_")[1].toDouble()
                mutableSetAddress[iterator.toString()] = LatLng(lat,lng)
                iterator++
            }catch (ex:Exception)
            {
                Log.e("GEO_LOCATION_STORAGE", ex.localizedMessage)
            }
        }
        return  mutableSetAddress
    }

    fun addReverseGeoLocation(reverseAddress: String?, date: String) {
        if (reverseAddress != null)
        {
            val editor = preference.edit()
            val addressMutableString = getReverseGeoLocation()
            addressMutableString?.add("$reverseAddress|$date")
            editor.putStringSet(REVERSE_GEOLOCATION, addressMutableString)
            editor.apply()
        }
    }

    fun getReverseGeoLocation():MutableSet<String>?
    {
        return preference.getStringSet(REVERSE_GEOLOCATION, mutableSetOf())
    }

    fun clearGPS() {
        val editor = preference.edit()
        editor.remove(GEOLOCATION).apply()
    }

    fun isAlertAdded(): Boolean {
        return preference.getBoolean(IS_ALERT_ADDED, false)
    }

    fun setAlertAdded()
    {
        val editor = preference.edit()
        var isNotDef = false
        if(getReverseGeoLocation()!!.isNotEmpty()) {
            isNotDef = true
        }
        editor.putBoolean(IS_ALERT_ADDED, isNotDef)
        editor.apply()
    }

    /**
     * Some brief User statistics
     */
    private fun addConnectionIterator()
    {
        val editor = preference.edit()
        val currVal = getConnectionIterator() + 1
        editor.putInt(CONNECTION_ITERATOR, currVal)
        editor.apply()
    }

    fun getConnectionIterator():Int
    {
        return preference.getInt(CONNECTION_ITERATOR, 0)
    }

    fun addInfectedIterator()
    {
        val editor = preference.edit()
        val currVal = getInfectedIterator() + 1
        editor.putInt(INFECTED_ITERATOR, currVal)
        editor.apply()
    }

    fun getInfectedIterator():Int
    {
        return preference.getInt(INFECTED_ITERATOR, 0)

    }

    /**
     * n-th degree connections
     */
    fun getPreviousConnection():Float
    {
        return preference.getFloat(N_DEGREE_CONNECTION, -9F)
    }
    fun setPreviousConnection(set_confidence:Float){
        val editor = preference.edit()
        editor.putFloat(N_DEGREE_CONNECTION, set_confidence)
        editor.apply()

    }

    fun addOnlineConnection(uuid: String?):Boolean
    {
        if(!isOnlineConnection(uuid))
        {
            val editor = preference.edit()
            val getConnections = getOnlineConnections()
            getConnections?.add(uuid!! + "-${Utils.getTimeStamp()}")

            editor.putStringSet(N_DEGREE_CONNECTION_FIREBASE, getConnections)
            editor.apply()
            //addInfectedIterator() //add an online case to total number of infections
            //addConnectionIterator() //add an online case to total number of cases
            return true
        }
        return false //connection probably already exists
    }

    fun getOnlineConnections():MutableSet<String>?
    {
        return preference.getStringSet(N_DEGREE_CONNECTION_FIREBASE, mutableSetOf())
    }

    /**
     * Checks if the following UUID is part of the connection.
     * Helper Function for addOnlineConnection.
     */
    fun isOnlineConnection(uuid: String?):Boolean
    {
        val connections = getOnlineConnections()
        val result = connections?.find{
            uuid!! in it
        }
        if(result != null)
        {
            return true
        }
        return false
    }

    fun setNotificationQueue(bool:Boolean)
    {
        val editor = preference.edit()
        editor.putBoolean(SENT_NOTIFICATION, bool)
        editor.apply()
    }

    fun hasSentNotification():Boolean
    {
        return preference.getBoolean(SENT_NOTIFICATION, false)
    }

    /**
     * 2 Week period storage periodic clearer function for online and direct connections
     */
    fun clear2WeekPeriodONLINE()
    {
        val currentOnlineConnections = getOnlineConnections()
        if(currentOnlineConnections != null)
        {
            val newSetConnections = mutableSetOf<String>()
            for (connection in currentOnlineConnections)
            {
                val uuidTimestamp = connection.split("-")[1].toLong()
                if (Utils.getTimeStamp() - uuidTimestamp < 1209600) //2-week time period has passed
                {
                    newSetConnections.add(connection)
                }
            }
            val editor = preference.edit()
            editor.putStringSet(N_DEGREE_CONNECTION_FIREBASE, newSetConnections)
            editor.apply()
        }
    }
    fun clear2WeekPeriodDIRECT()
    {
        val currentConnections = getConnections()
        if(currentConnections != null)
        {
            val newSetConnections = mutableSetOf<String>()
            for (connection in currentConnections)
            {
                val uuidTimestamp = connection.split("-")[1].toLong()
                if (Utils.getTimeStamp() - uuidTimestamp < 1209600) //2-week time period has passed
                {
                    newSetConnections.add(connection)
                }
            }
            val editor = preference.edit()
            editor.putStringSet(UUID_CONNECTIONS, newSetConnections)
            editor.apply()
        }
    }

    fun finishOnBoarding() {
        val editor = preference.edit()
        editor.putBoolean(ONBOARDING, true)
        editor.apply()
    }

    fun hasFinishedOnBoarding():Boolean
    {
        return preference.getBoolean(ONBOARDING, false)
    }
}