package com.example.quarantine.ui.main

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.GridView
import android.widget.Toast

import com.example.quarantine.R
import com.example.quarantine.adapters.SymptomsAdapters
import com.example.quarantine.models.symptoms.SymptomsItem

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TestFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TestFragment : Fragment(), AdapterView.OnItemClickListener {
    // TODO: Rename and change types of parameters
    private var param1: Int? = null
    private var param2: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getInt(ARG_PARAM1)
            param2 = it.getInt(ARG_PARAM2)
        }
    }
    private var arrayList:ArrayList<SymptomsItem> ?= null;
    private var gridView: GridView ? = null;
    private var symptomsAdapters:SymptomsAdapters ? = null;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_symptoms, container, false)
        gridView = root.findViewById(R.id.grid_symptoms_pre_questionnaire)
        arrayList = ArrayList()
        arrayList = setDataList()
        symptomsAdapters = activity?.applicationContext?.let { SymptomsAdapters(it, arrayList!!) }
        gridView?.adapter = symptomsAdapters
        gridView?.onItemClickListener = this
        return root
    }

    private fun setDataList() : ArrayList<SymptomsItem>{
        var arrayList:ArrayList<SymptomsItem> = ArrayList();
        arrayList.add(SymptomsItem(R.drawable.day1_reflection, "Test 1"))
        arrayList.add(SymptomsItem(R.drawable.day2_reflection, "Test 2"))
        arrayList.add(SymptomsItem(R.drawable.day4_reflection, "Test 3"))
        return arrayList
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment TestFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: Int, param2: Int) =
            TestFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                    putInt(ARG_PARAM2, param2)
                }
            }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var items:SymptomsItem = arrayList!!.get(position)
        Toast.makeText(activity?.applicationContext, items.caption, Toast.LENGTH_SHORT).show()
    }
}
