package ml.preventiv.fragments.symptoms

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import ml.preventiv.AppPreference
import ml.preventiv.R
import ml.preventiv.Utils
import ml.preventiv.activities.SymptomsSurvey
import ml.preventiv.location.NetworkManager
import com.google.android.material.floatingactionbutton.FloatingActionButton

class SymptomsFragment: Fragment() {
    private val TAG = "SymptomsFragment"
    private lateinit var networkManager: NetworkManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_symptoms_main, container, false)
        val button = root.findViewById<CardView>(R.id.symptoms_main_launcher)
        val appPreference = AppPreference(activity!!.applicationContext)

        networkManager = NetworkManager.newInstance(activity!!.applicationContext)

        Log.d(TAG, appPreference.getConfidence().toString())

        button.setOnClickListener{
            val intent = Intent(context, SymptomsSurvey::class.java)
            appPreference.setPreviousConnection(appPreference.getConfidence())
            startActivity(intent)
        }
        val isNotify = sendNotifications(appPreference)
        if (isNotify && appPreference.getPreviousConnection() != -9F)
        {
            if(Utils.isInternetAvailable(activity!!.applicationContext))
            {
                appPreference.setNotificationQueue(true)
                networkManager.sendAlert(0, 0)
            }
        }

        /**
         * Implicit Intent for sharing the app with others
         */
        val fab: FloatingActionButton = root.findViewById(R.id.fabShare)

        fab.setOnClickListener {
            val intent = Intent()
            intent.action = Intent.ACTION_SEND
            intent.putExtra(Intent.EXTRA_TEXT, "Preventiv - Contact Tracing App for COVID-19. Become a superhero by installing Preventiv at: http://preventiv.ml/")
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_TITLE, "Share message: ")
            startActivity(Intent.createChooser(intent, "Share Preventiv with people you love and care"))
        }

        /**
         * Coffee cup toggle for showing links
         */
        val fabCoffee = root.findViewById<FloatingActionButton>(R.id.fabCoffee)
        fabCoffee.setOnClickListener {
            val how = root.findViewById<TextView>(R.id.how_does_it_work)
            val privacy = root.findViewById<TextView>(R.id.privacy)
            val faq = root.findViewById<TextView>(R.id.faq)
            toggleVisibility(how, "https://www.youtube.com/watch?v=VffokuToaPI&list=PLGSbYGhGpgY4WSVJoe6sACOEOr9q8eXbF")
            toggleVisibility(privacy, "https://docs.google.com/document/d/1uOELBfaPc0aFA9LJ6nT0eVy9KTDqssUHFdFhTsNa_kA/edit")
            toggleVisibility(faq, "https://docs.google.com/document/d/1kdHyFon6nyloNoUQX1D1T8Kz6e2-72hjqePatxY-duY/edit?usp=sharing")
        }
        val statusUserFriendlyText = root.findViewById<TextView>(R.id.type_user)
        statusUserFriendlyText.text = setTextCOVID(appPreference.getConfidence())

        updateStats(appPreference, root)
        updateConnections(appPreference, root)

        /**
         * Main functions for getting and setting N-th Degree stuff
         * 1. getter : getAlerts()
         * 2. setter: sendAlert(level, time)
         */

        return root
    }
    private fun toggleVisibility(textView: TextView, linkOnClickListener:String)
    {

        if (textView.visibility == View.INVISIBLE)
        {
            val animation = AnimationUtils.loadAnimation(activity!!.applicationContext, android.R.anim.fade_in)
            animation.duration = 1000
            textView.startAnimation(animation)
            textView.visibility = View.VISIBLE
            textView.setOnClickListener {
                val i = Intent(Intent.ACTION_VIEW)
                i.data = Uri.parse(linkOnClickListener)
                startActivity(i)
            }
        }else
        {
            val animation = AnimationUtils.loadAnimation(activity!!.applicationContext, android.R.anim.fade_out)
            animation.duration = 500
            textView.startAnimation(animation)
            textView.visibility = View.INVISIBLE
        }


    }
    private fun updateStats(appPreference: AppPreference, root: View)
    {
        val peopleContactedText = root.findViewById<TextView>(R.id.connection_count)
        peopleContactedText.text = appPreference.getConnectionIterator().toString()

        val infectedContactedText = root.findViewById<TextView>(R.id.infection_count)
        infectedContactedText.text = appPreference.getInfectedIterator().toString()
    }
    private fun updateConnections(appPreference: AppPreference, root: View)
    {
        val handler = Handler()
        val delay = 10000 //10-second Update
        handler.postDelayed(object : Runnable {
            override fun run() {
                //do something
                updateStats(appPreference, root)
                networkManager.getAlerts()
                handler.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())
    }




    private fun sendNotifications(appPreference: AppPreference):Boolean
    {
        val prev = appPreference.getPreviousConnection()
        val curr = appPreference.getConfidence()
        val isInfected = (prev == 1F || prev == 0.75F)
        val currAsBool = (curr == 1F || curr == 0.75F)
        Log.i(TAG, "This is CURRENT value: $curr")
        Log.i(TAG, "This is PREVIOUS value: $prev")

        return (isInfected.xor(currAsBool) && currAsBool && !appPreference.hasSentNotification())
    }
    private fun setTextCOVID(confidence:Float):String
    {
        val default = "Identified as "
        return when(confidence)
        {
            1F-> "Self " + default + "COVID Positive"
            -1F -> "Self " + default + "COVID Negative"
            0.75F -> default + "showing COVID Symptoms"
            0.25F -> default + "not showing COVID Symptoms"
            else -> "COVID Screening Information not added"
        }
    }
    companion object {
        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        fun newInstance() = SymptomsFragment()
    }


}