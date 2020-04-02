package com.example.quarantine.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.quarantine.R
import android.os.Handler
import android.util.Log
import com.example.quarantine.AppPreference

class PreLaunch : AppCompatActivity() {
    /**
     * Purpose of this class:
     * 1. Provide a launch progress, so this is the first thing users see.
     * 2. This will be a baseline transition from preNavigation to either launching the symptoms screen or the actual MainActivity
     * 3. Launch DB here (local storage of course, bc wifi won't work).
     *      i. If user has filled out the symptoms sheet, direct to main activity.
     *      ii. otherwise direct them to Fill the symptoms (within this Activity)
     * 4. Add a splash screen on load.
     */
    private var mDelayHandler : Handler? = null

    companion object {
        private const val SPLASH_DELAY:Long = 1000 //ms delay for splash screen
    }


    private val mRunnable:Runnable = Runnable {
        val appPreferences = AppPreference(this)
        val muxState = appPreferences.getOnAppLaunchInfo()
        if (!isFinishing)
        {
            var intent = Intent(applicationContext, MainActivity::class.java)
            Log.i("getOnAppLaunchInfo", muxState.toString())
            if (muxState == 0){
                intent = Intent(applicationContext, SymptomsSurvey::class.java)
            } //takes to SymptomsActivity
            startActivity(intent)
            finish()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_launch)

        //Initialize the Handler
        mDelayHandler = Handler()

        //Navigate with delay
        mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)

    }

    public override fun onDestroy() {

        if (mDelayHandler != null) {
            mDelayHandler!!.removeCallbacks(mRunnable)
        }

        super.onDestroy()
    }
}