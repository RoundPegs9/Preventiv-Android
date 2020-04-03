package com.example.quarantine.fragments.symptoms

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.GridView
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.quarantine.AppPreference
import com.example.quarantine.R
import com.example.quarantine.activities.MainActivity
import com.example.quarantine.adapters.SymptomsAdapters
import com.example.quarantine.models.symptoms.SymptomsItem
import kotlinx.android.synthetic.main.activity_symptoms.*


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

        val nxt = activity?.next_button
        nxt?.visibility = View.INVISIBLE

        nxt?.setOnClickListener{
            if (score == 1.0 || score == -1.0)
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
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer_symptoms, MainQuestions.newInstance())?.addToBackStack("pre_questions")?.commit()
        }
        else
        {

            appPreference.setConfidence(state!!.toFloat())
            val intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
    }
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun changeUI(gridView: GridView?, position: Long)
    {
        if (tempId != position.toInt())
        {
            val cardAnimation = AnimationUtils.loadAnimation(context!!, R.anim.fragment_close_enter)
            for (i in 0..3) {
                gridView?.get(i)?.background?.setTint(Color.WHITE)
            }
            cardAnimation.duration= 500
            gridView?.get(position.toInt())?.animation = cardAnimation
            gridView?.get(position.toInt())?.background?.setTint(resources.getColor(R.color.onClickCard))
        }

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val appPreferences = AppPreference(context!!)
        score = arrayList!![position].score


        val nxt = activity?.next_button
        val buttonAnimation = AnimationUtils.loadAnimation(context!!, R.anim.fragment_close_enter)
        buttonAnimation.duration = 689
        nxt?.animation = buttonAnimation!!
        nxt?.visibility = View.VISIBLE

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            changeUI(this.gridView, id)
        }
        tempId = id.toInt()


    }
}
