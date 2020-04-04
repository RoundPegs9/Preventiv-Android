package com.example.quarantine.adapters

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.quarantine.R
import com.example.quarantine.models.symptoms.SymptomsItem

class SymptomsAdapters(var context: Context, var arrayList: ArrayList<SymptomsItem>, private val inflater:Int, private val icons:Int, private val caption:Int): BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view:View = View.inflate(context, inflater, null)
        var icons:ImageView = view.findViewById(icons)
        var captions:TextView = view.findViewById(caption)

        var listItem:SymptomsItem = arrayList[position]
        icons.setImageResource(listItem.icons!!)
        captions.text = listItem.caption //setter


        return view
    }

    fun setBackground(view: View?, position: Int, state:Boolean)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (state)
            {
                //change background.
                view?.background?.setTint(Color.argb(100,167,191,167))
            }
            else
            {
                view?.background?.setTint(Color.WHITE)
            }
        }

    }
    override fun getItem(position: Int): Any {
        return arrayList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return arrayList.size
    }
}