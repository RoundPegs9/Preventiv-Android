package ml.preventiv.onboarding

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
import ml.preventiv.AppPreference
import ml.preventiv.R
import ml.preventiv.activities.SymptomsSurvey
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

        val appPreferences = AppPreference(applicationContext)
        // when this activity is about to be launch we need to check if its openened before or not
        if (appPreferences.hasFinishedOnBoarding()) {
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
                R.drawable.preventiv_logo
            )
        )
        mList.add(
            ScreenItem(
                "Exposure Notifications",
                "Enable Bluetooth Scanning to check if someone near you been tested positive for COVID-19.",
                R.drawable.notifications
            )
        )
        mList.add(
            ScreenItem(
                "Connections",
                "You'll be notified via in-app notifications if someone you interacted with in the past 14 days has been infected with COVID-19.",
                R.drawable.network
            )
        )
        mList.add(
            ScreenItem(
                "Heat Map",
                "Find out which areas in real-time in your neighborhood are infected with COVID-19 so you can better plan your day.",
                R.drawable.heatmap_onboarding
            )
        )
        mList.add(
            ScreenItem(
                "Privacy",
                "Preventiv uses Bluetooth LE and Differential Privacy algorithms to increase user privacy. All data is stored locally on your phone.",
                R.drawable.privacy_onboarding
            )
        )
        mList.add(
            ScreenItem(
                "Preventiv Basics : 1",
                "To start using Preventiv, select the card that best describes how you've felt over the past 2 weeks.",
                R.drawable.tutorial_1
            )
        )
        mList.add(
            ScreenItem(
                "Preventiv Basics : 2",
                "If you haven't been tested for COVID-19 or are waiting for your test results, select all symptoms you've been experiencing in the past 14 days.",
                R.drawable.tutorial_2
            )
        )
        mList.add(
            ScreenItem(
                "Preventiv Basics : 3",
                "Enable Bluetooth scanning to identify who near you is infected from the virus. Preventiv scans in the background without draining your phone's battery.",
                R.drawable.tutorial_3
            )
        )
        mList.add(
            ScreenItem(
                "WE CAN DO IT",
                "Let's fight this virus together by being proactive on how we distance ourselves so we can return back to normal. It takes change to make change!",
                R.drawable.we_can_do_it
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
            appPreferences.finishOnBoarding()
            finish()
        })

        // skip button click listener
        tvSkip?.setOnClickListener {
            screenPager?.currentItem = mList.size
        }
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
        else
        {

        }
        tvSkip!!.visibility = View.INVISIBLE
    }
}