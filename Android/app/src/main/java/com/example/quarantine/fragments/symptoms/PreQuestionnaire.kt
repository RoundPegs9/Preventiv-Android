package com.example.quarantine.fragments.symptoms

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.AdapterView
import android.widget.GridView
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
    private var temp_confidence:Double? = 0.0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_symptoms_1, container, false)
        gridView = root.findViewById(R.id.grid_symptoms_pre_questionnaire)
        arrayList = ArrayList()
        arrayList = setDataList()
        symptomsAdapters = activity?.applicationContext?.let { SymptomsAdapters(it, arrayList!!, R.layout.card_view_symptoms_1, R.id.icons, R.id.caption) }
        gridView?.adapter = symptomsAdapters
        gridView?.onItemClickListener = this

        val nxt = activity?.next_button
        nxt?.visibility = View.INVISIBLE

        nxt?.setOnClickListener{
            var confidence = AppPreference(context!!).getConfidence()
            if (confidence == -9F)
            {

            }

        }
        return root
    }

    private fun setDataList() : ArrayList<SymptomsItem>{
        var arrayList:ArrayList<SymptomsItem> = ArrayList();
        arrayList.add(SymptomsItem(R.drawable.positive, "Tested Positive with COVID-19", 1.0))
        arrayList.add(SymptomsItem(R.drawable.negative, "Tested Negative with COVID-19", -1.0))
        arrayList.add(SymptomsItem(R.drawable.not_tested, "Not been tested yet", 0.0))
        arrayList.add(SymptomsItem(R.drawable.question, "Waiting for test results", 0.5))
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
        var confidence = appPreference.getConfidence()

        if(loadSymptoms)
        {
            appPreference.setConfidence(state!!.toFloat())
            activity?.supportFragmentManager?.beginTransaction()?.replace(R.id.fragmentContainer_symptoms, MainQuestions.newInstance())?.addToBackStack("pre_questions")?.commit()
        }
        else
        {

            appPreference.setConfidence(state!!.toFloat())
            var intent = Intent(context, MainActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val appPreferences = AppPreference(context!!)
        var items:SymptomsItem = arrayList!![position]

        temp_confidence = items.score
        val nxt = activity?.next_button
        val buttonAnimation = AnimationUtils.loadAnimation(context!!, R.anim.fragment_close_enter)
        buttonAnimation.duration = 689
        nxt?.animation = buttonAnimation!!
        nxt?.visibility = View.VISIBLE



//        if (items.score == 1.0 || items.score == -1.0)
//        {
//            //user knows their results. activity launch.
//            appPreferences.setOnAppLaunchInfo(1)
//            nextSteps(false, items.score)
//        }
//        else
//        {
//            //user needs to take the quiz. fragment launch.
//            appPreferences.setOnAppLaunchInfo(0)
//            nextSteps(true, items.score)
//        }

    }
}
