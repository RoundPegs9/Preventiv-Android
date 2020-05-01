package com.example.quarantine.fragments.alerts

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quarantine.AppPreference
import com.example.quarantine.R
import com.example.quarantine.adapters.AlertsRecyclerViewAdapter
import com.example.quarantine.models.alerts.AlertsItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.collections.ArrayList

/**
 * Loads the list of all previously contact Positive COVID-19 connections you made so far.
 * Puts it in a recycler view of the following format (dir = ltr):
 * 1. Address (csv[0])
 * 2. Date
 * 3. Map Icon (when clicked, activity showing that address on the map (marker = infection icon)
 */
class AlertFragment:Fragment() {

    private lateinit var arrayList:ArrayList<AlertsItem>

    private var recyclerView:RecyclerView? = null

    private var alertsAdapters: AlertsRecyclerViewAdapter? = null
    private lateinit var linearLayoutManager:LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        linearLayoutManager = LinearLayoutManager(context)

        val root = inflater.inflate(R.layout.fragment_recycler, container, false)
        root.setBackgroundColor(Color.parseColor("#5EE4BFEF"))
        recyclerView = root.findViewById(R.id.recycler_symptoms_main)
        recyclerView?.layoutManager = linearLayoutManager
        arrayList = setDataList()

        val llm = GridLayoutManager(this.context, 1)
        recyclerView?.layoutManager = llm

        alertsAdapters = activity?.applicationContext?.let {
            AlertsRecyclerViewAdapter(this.context!!, arrayList)
        }
        recyclerView?.adapter = alertsAdapters

        return root
    }


    private fun setDataList() : ArrayList<AlertsItem>{
        val arrayList:ArrayList<AlertsItem> = ArrayList();
        AppPreference(activity!!.applicationContext).getReverseGeoLocation()!!.reversed().forEach {
            arrayList.add(AlertsItem(it.split("|")[0], it.split("|")[1]))
        }
        return arrayList
    }

    companion object
    {
        fun newInstance() = AlertFragment()
    }
}