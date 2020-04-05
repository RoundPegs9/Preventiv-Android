package com.example.quarantine.fragments.symptoms

import com.example.quarantine.adapters.SymptomsRecyclerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quarantine.R
import com.example.quarantine.models.symptoms.SymptomsItem



class MainSymptoms: Fragment() {
    private var arrayList:ArrayList<SymptomsItem> ?= null
    private var recyclerView:RecyclerView? = null

    private var symptomsAdapters: SymptomsRecyclerAdapter? = null
    private lateinit var linearLayoutManager:LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        recyclerView?.layoutManager = linearLayoutManager
        val root = inflater.inflate(R.layout.fragment_symptoms_2, container, false)
        recyclerView = root.findViewById(R.id.recycler_symptoms_main)
        arrayList = ArrayList()
        arrayList = setDataList()

        val llm = GridLayoutManager(this.context, 2)
        recyclerView?.layoutManager = llm
        symptomsAdapters = activity?.applicationContext?.let {
            SymptomsRecyclerAdapter(this.activity!!, this.context!!, arrayList!!)
        }
        recyclerView?.adapter = symptomsAdapters
        return root
    }

    private fun setDataList() : ArrayList<SymptomsItem>{
        var arrayList:ArrayList<SymptomsItem> = ArrayList();
        arrayList.add(SymptomsItem(R.drawable.healthy, "No Symptoms", 0.0))
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
         * Dynamically Creates a new instance of [MainSymptoms] class
         */

        fun newInstance() = MainSymptoms()
    }

}
