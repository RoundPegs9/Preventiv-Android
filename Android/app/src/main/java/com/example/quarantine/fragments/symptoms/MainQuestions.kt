package com.example.quarantine.fragments.symptoms

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import com.example.quarantine.AppPreference
import com.example.quarantine.R
import com.example.quarantine.activities.MainActivity
import com.example.quarantine.adapters.SymptomsAdapters
import com.example.quarantine.models.symptoms.SymptomsItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_symptoms.*


/**
 * A simple [Fragment] subclass.
 * Use the [MainQuestions.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainQuestions : Fragment(), AdapterView.OnItemClickListener {

    private var arrayList:ArrayList<SymptomsItem> ?= null
    private var gridView: GridView? = null
    private var symptomsAdapters: SymptomsAdapters? = null

    private var symptomsArray:ArrayList<Double> = ArrayList()



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_symptoms_2, container, false)
        gridView = root.findViewById(R.id.grid_symptoms_main_questions)
        arrayList = ArrayList()
        arrayList = setDataList()
        symptomsAdapters = activity?.applicationContext?.let { SymptomsAdapters(it, arrayList!!, R.layout.card_view_symptoms_2, R.id.icons_symptoms, R.id.caption_symptoms) }
        gridView?.adapter = symptomsAdapters
        gridView?.onItemClickListener = this

        //hide the next button for now.
        val nxt = activity?.next_button
        nxt?.visibility = View.GONE

        val fab:FloatingActionButton? = activity?.fab_sym

        Log.i("fab", fab.toString())

        fab?.setOnClickListener {
            var confidence:Double = 0.0
            symptomsArray.forEach {
                confidence += it
            }
            val appPreference = AppPreference(context!!)
           if (confidence >= 1.77) //need to redo this algorithm
           {
               //shows signs of the virus
               appPreference.setConfidence(0.75F)
           }
            else
           {
             //mild to no symptoms pertaining to the virus.
               appPreference.setConfidence(0.25F)
           }
        }

        return root
    }

    private fun setDataList() : ArrayList<SymptomsItem>{
        var arrayList:ArrayList<SymptomsItem> = ArrayList();
        arrayList.add(SymptomsItem(R.drawable.dry_cough, "Continuous Dry Cough", 0.879))
        arrayList.add(SymptomsItem(R.drawable.fever, "High Grade Fever", 0.677))
        arrayList.add(SymptomsItem(R.drawable.fatigue, "Fatigue", 0.381))
        arrayList.add(SymptomsItem(R.drawable.sputum, "Sputum Production", 0.334))
        arrayList.add(SymptomsItem(R.drawable.breath, "Shortness of breath", 0.184))
        arrayList.add(SymptomsItem(R.drawable.bodypain, "Muscle/Joint Pain", 0.148))
        arrayList.add(SymptomsItem(R.drawable.sore_throat, "Sore Throat", 0.139))
        arrayList.add(SymptomsItem(R.drawable.headache, "Headache", 0.136))
        arrayList.add(SymptomsItem(R.drawable.chills, "Chills", 0.114))
        arrayList.add(SymptomsItem(R.drawable.nausea, "Nausea/Vomiting", 0.05))
        arrayList.add(SymptomsItem(R.drawable.nasal_congestion, "Nasal Congestion", 0.048))
        arrayList.add(SymptomsItem(R.drawable.diarrehea, "Diarrhea", 0.037))

        return arrayList
    }
    companion object {
        /**
         * Dynamically Creates a new instance of [PreQuestionnaire] class
         */
        fun newInstance() = MainQuestions()
    }
    private fun manipulateSymptomsArray(score:Double) {
        if (symptomsArray.indexOf(score) == -1)
        {
            symptomsArray.add(score)
        }
        else
        {
            symptomsArray.remove(score)
        }
    }
    private fun getAllChildren(v: GridView):String {
        var result:String = "\n"

        for (i in 0 until v.childCount) {
            result += v[i].toString() + "\n"
        }
        return result
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

        var items: SymptomsItem = arrayList!![position]
        val data = gridView?.let { getAllChildren(it) }
        Log.i("test", data)
        Toast.makeText(activity?.applicationContext, items.score.toString(), Toast.LENGTH_SHORT).show()
        manipulateSymptomsArray(items.score!!)
        Log.i("symptomsArray", symptomsArray.toString())
    }
}


