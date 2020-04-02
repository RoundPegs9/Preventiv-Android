package com.example.quarantine

import android.content.Context
import android.content.SharedPreferences

const val PREFERENCE_NAME = "Quarantine"
const val PREFERENCE_APP_LAUNCH_CHOICE = "MuxOnLaunch"
const val CONFIDENCE = "CONFIDENCE"

class AppPreference(context:Context) {
    private var preference: SharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)

    fun setOnAppLaunchInfo(mux:Int){
        val editor = preference.edit()
        editor.putInt(PREFERENCE_APP_LAUNCH_CHOICE, mux)
        editor.apply()
    }
    fun getOnAppLaunchInfo():Int{
        return preference.getInt(PREFERENCE_APP_LAUNCH_CHOICE,0)
    }
    fun setConfidence(set_confidence:Int){
        val editor = preference.edit()
        editor.putInt(CONFIDENCE, set_confidence)
        editor.apply()
    }
    fun getConfidence():Int{
        return preference.getInt(CONFIDENCE, 0)
    }


}