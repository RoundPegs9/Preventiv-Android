package com.example.quarantine.fragments.symptoms

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.GridView
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.quarantine.AppPreference
import com.example.quarantine.R
import com.example.quarantine.activities.MainActivity
import com.example.quarantine.adapters.SymptomsAdapters
import com.example.quarantine.models.symptoms.SymptomsItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_symptoms.*
import kotlin.math.abs


/**
 * A simple [Fragment] subclass.
 * Use the [PreQuestionnaire.newInstance] factory method to
 * create an instance of this fragment.
 */
class PreQuestionnaire : Fragment(), AdapterView.OnItemClickListener {

    private var arrayList:ArrayList<SymptomsItem> ?= null
    private var gridView: GridView ? = null
    private var symptomsAdapters:SymptomsAdapters ? = null
    private var tempId:Int? = -1
    private var score: Double? = 0.0
    private val TAG = "PreQuestionnaire"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_symptoms_1, container, false)
        val appPreference = AppPreference(context!!)
        gridView = root.findViewById(R.id.grid_symptoms_pre_questionnaire)
        arrayList = ArrayList()
        arrayList = setDataList()
        symptomsAdapters = activity?.applicationContext?.let { SymptomsAdapters(it, arrayList!!, R.layout.card_view_symptoms_1, R.id.icons, R.id.caption) }
        gridView?.adapter = symptomsAdapters
        gridView?.onItemClickListener = this
        val fab: FloatingActionButton? = activity?.fab_sym
        fab!!.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E3D3A429"))
        fab.setImageResource(R.drawable.info_icon)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Tap the box you most strongly agree with", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val nxt = activity?.next_button
        nxt?.visibility = View.INVISIBLE

        nxt?.setOnClickListener{
            nxt?.visibility = View.GONE
            if (score?.let { it1 -> abs(it1) } == 1.0)
            {
                //user knows their results. activity launch.
                appPreference.setOnAppLaunchInfo(1)
                nextSteps(false, score)
            }
            else
            {
                //user needs to take the quiz. fragment launch.
                appPreference.setOnAppLaunchInfo(0)
                nextSteps(true, score)
            }
        }

        return root
    }

    private fun setDataList() : ArrayList<SymptomsItem>{
        val arrayList:ArrayList<SymptomsItem> = ArrayList()
        arrayList.add(SymptomsItem(R.drawable.positive, "Tested Positive with COVID-19", 1.0))
        arrayList.add(SymptomsItem(R.drawable.negative, "Tested Negative with COVID-19", -1.0))
        arrayList.add(SymptomsItem(R.drawable.question, "Not been tested yet", 0.0))
        arrayList.add(SymptomsItem(R.drawable.waiting, "Waiting for test results", 0.5))
        return arrayList
    }
    companion object {
        /**
         * Dynamically Creates a new instance of [PreQuestionnaire] class
         */
        fun newInstance() = PreQuestionnaire()
    }

    private fun nextSteps(loadSymptoms:Boolean, state: Double?)
    {
        val appPreference = AppPreference(context!!)
        if(loadSymptoms)
        {
            appPreference.setConfidence(state!!.toFloat())
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer_symptoms, MainSymptoms.newInstance())?.addToBackStack("pre_questions")?.commit()
        }
        else
        {

            appPreference.setConfidence(state!!.toFloat())
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
    }
    private fun changeUI(gridView: GridView?, position: Long)
    {
        if (tempId != position.toInt())
        {
            val cardAnimation = AnimationUtils.loadAnimation(context!!, R.anim.fragment_close_enter)
            for (i in 0 until arrayList?.size!!) {
                gridView?.get(i)?.background?.setTint(Color.WHITE)
            }
            cardAnimation.duration= 500
            gridView?.get(position.toInt())?.animation = cardAnimation
        }
        gridView?.get(position.toInt())?.background?.setTint(resources.getColor(R.color.onClickCard))

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        score = arrayList!![position].score

        Log.i("GridClick", Build.VERSION.SDK_INT.toString())
        Log.i("GridClick", score.toString())

        val nxt = activity?.next_button



        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            val buttonAnimation = AnimationUtils.loadAnimation(context!!, R.anim.fragment_close_enter)
            buttonAnimation.duration = 689
            nxt?.animation = buttonAnimation!!
            nxt?.visibility = View.VISIBLE
        }
        else //convert FAB to next button for versions <= 26
        {
            //change fab...
            val fab: FloatingActionButton? = activity?.fab_sym
            fab!!.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#7AB547"))
            fab!!.setImageResource(R.drawable.next_icon)

            fab?.setOnClickListener{
                if (score?.let { it1 -> abs(it1) } == 1.0)
                {
                    //user knows their results. activity launch.
                    context?.let { AppPreference(it).setOnAppLaunchInfo(1) }
                    nextSteps(false, score)
                }
                else
                {
                    //user needs to take the quiz. fragment launch.
                    context?.let { AppPreference(it).setOnAppLaunchInfo(0) }

                    nextSteps(true, score)
                }
            }

        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            changeUI(this.gridView, id)
        }
        tempId = id.toInt()
    }
}
