package com.example.quarantine.fragments.symptoms

import android.content.res.ColorStateList
import android.graphics.Color
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_symptoms.*


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
        linearLayoutManager = LinearLayoutManager(context)
        val root = inflater.inflate(R.layout.fragment_recycler, container, false)
        recyclerView = root.findViewById(R.id.recycler_symptoms_main)
        recyclerView?.layoutManager = linearLayoutManager
        arrayList = ArrayList()
        arrayList = setDataList()

        /**
         * On Create Floating Action Button Characteristics
         */
        val fab: FloatingActionButton? = activity?.fab_sym
        fab?.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E3D3A429"))
        fab?.setImageResource(R.drawable.info_icon)
        fab?.setOnClickListener{
            Snackbar.make(it, "Check all symptoms you've had in the past 14 days", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

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
