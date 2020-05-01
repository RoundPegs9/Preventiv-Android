package com.example.quarantine.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.quarantine.R
import com.example.quarantine.activities.SymptomsSurvey
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.BaseOnTabSelectedListener

class OnBoardingActivity:AppCompatActivity() {
    var screenPager: ViewPager? = null
    private var onBoardViewPagerAdapter: OnBoardingAdapter? = null
    var tabIndicator: TabLayout? = null
    var btnGetStarted: Button? = null
    var certifyInformation: TextView? = null
    var btnAnim: Animation? = null
    var tvSkip: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // make the activity on full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )


        // when this activity is about to be launch we need to check if its openened before or not
        if (restorePrefData()) {
            val mainActivity = Intent(applicationContext, SymptomsSurvey::class.java)
            startActivity(mainActivity)
            finish()
        }
        setContentView(R.layout.activity_onboard)

        // hide the action bar
        supportActionBar?.hide()

        // ini views
//        btnNext = findViewById(R.id.btn_next)
        btnGetStarted = findViewById(R.id.btn_get_started)
        certifyInformation = findViewById(R.id.certifyButton)
        tabIndicator = findViewById(R.id.tab_indicator)
        btnAnim = AnimationUtils.loadAnimation(applicationContext, R.anim.button_animation)
        tvSkip = findViewById(R.id.tv_skip)

        // fill list screen
        val mList: MutableList<ScreenItem> = ArrayList()
        mList.add(
            ScreenItem(
                "Preventiv",
                "Accurate and intelligent contact-tracing system using Bluetooth LE technology. Perfect companion to keep you safe when social distancing is not possible.",
                R.drawable.cross_overlay
            )
        )
        mList.add(
            ScreenItem(
                "Notifications",
                "Exposure notifications to keep your ass alerted!",
                R.drawable.cross_overlay
            )
        )
        mList.add(
            ScreenItem(
                "Connections",
                "ya know, everyone out there is trying to get ya, because apparently you're so fucking important!",
                R.drawable.cross_overlay
            )
        )
        mList.add(
            ScreenItem(
                "Heat Map",
                "Oh yeah, we do that too",
                R.drawable.heatmap_onboarding
            )
        )
        mList.add(
            ScreenItem(
                "Privacy",
                "How could I forget, we are 'experts' at privacy and what it means for your Selfie to stay locally.",
                R.drawable.cross_overlay
            )
        )

        // setup viewpager
        screenPager = findViewById(R.id.screen_viewpager)
        onBoardViewPagerAdapter = OnBoardingAdapter(this, mList)
        screenPager?.adapter = onBoardViewPagerAdapter


        // setup tablayout with viewpager
        tabIndicator?.setupWithViewPager(screenPager)


        // tablayout add change listener
        tabIndicator?.addOnTabSelectedListener(object : BaseOnTabSelectedListener<TabLayout.Tab?> {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab!!.position == mList.size - 1) {
                    loadLastScreen()
                }
            }
            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })


        // Get Started button click listener
        btnGetStarted?.setOnClickListener(View.OnClickListener { //open main activity
            val mainActivity = Intent(applicationContext, SymptomsSurvey::class.java)
            startActivity(mainActivity)
            savePrefsData()
            finish()
        })

        // skip button click listener
        tvSkip?.setOnClickListener {
            screenPager?.currentItem = mList.size
        }
    }

    private fun restorePrefData(): Boolean {
        val pref = applicationContext.getSharedPreferences(
            "myPrefs",
            Context.MODE_PRIVATE
        )
        return pref.getBoolean("isIntroOpnend", false)
    }

    private fun savePrefsData() {
        val pref = applicationContext.getSharedPreferences(
            "myPrefs",
            Context.MODE_PRIVATE
        )
        val editor = pref.edit()
        editor.putBoolean("isIntroOpnend", true)
        editor.apply()
    }

    // show the GETSTARTED Button and hide the indicator and the next button
    private fun loadLastScreen() {
        if(btnGetStarted!!.visibility == View.INVISIBLE)
        {
            btnGetStarted!!.visibility = View.VISIBLE
            btnGetStarted!!.animation = btnAnim
            certifyInformation!!.visibility = View.VISIBLE
            certifyInformation!!.animation = btnAnim
        }
        tvSkip!!.visibility = View.INVISIBLE
    }
}