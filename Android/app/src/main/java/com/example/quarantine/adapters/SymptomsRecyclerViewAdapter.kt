package com.example.quarantine.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.quarantine.AppPreference
import com.example.quarantine.R
import com.example.quarantine.activities.MainActivity
import com.example.quarantine.models.symptoms.SymptomsItem
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_symptoms.*
import kotlinx.android.synthetic.main.activity_symptoms.view.*
import kotlinx.android.synthetic.main.activity_symptoms.view.fab_sym
import kotlinx.android.synthetic.main.card_view_symptoms_2.view.*

class SymptomsRecyclerAdapter(val activity: Activity, val context:Context, private val symptoms: ArrayList<SymptomsItem>): RecyclerView.Adapter<SymptomsRecyclerAdapter.SymptomsHolder>() {
    var symptomsArrayListTemplate:ArrayList<Double?> = ArrayList()
    var symptomsMainList: ArrayList<Int?> = ArrayList()
    var symptomsView:ArrayList<View?> = ArrayList()

    override fun getItemCount(): Int {
        return symptoms.size
    }

    override fun onBindViewHolder(holder: SymptomsHolder, position: Int) {
        val itemSymptom = symptoms[position]
        holder.bindSymptom(itemSymptom, position)
    }

    override fun getItemId(position: Int): Long {
        symptomsView[position]!!.id = position
        return symptomsView[position]!!.id.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SymptomsHolder {
        val inflatedView = parent.inflate(R.layout.card_view_symptoms_2)

        return SymptomsHolder(inflatedView)
    }

    inner class SymptomsHolder(v: View) : RecyclerView.ViewHolder(v) {

        private var view : View = v
        private var symptom : SymptomsItem? = null
        private var fab:FloatingActionButton? = activity?.fab_sym

        init {
            v.setOnClickListener{
                val score = this.symptom!!.score
                manipulateSymptomsArray(score, this.adapterPosition)
                decorumFab()
            }
            fab?.setOnClickListener{
                fabOnClickListener()
            }
        }
        private fun decorumFab()
        {
            if(symptomsArrayListTemplate!!.size > 0)
            {
                fab!!.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#7AB547"))
                fab!!.setImageResource(R.drawable.next_icon)
            }
            else
            {
                fab!!.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#E3D3A429"))
                fab!!.setImageResource(R.drawable.info_icon)
            }
        }
        private fun fabOnClickListener()
        {
            var confidence = 0.0
            symptomsArrayListTemplate.forEach{
                confidence += it!!
            }
            val appPreference = AppPreference(context!!)
            if (confidence >= 1.33) //need to redo this algorithm
            {
                //shows signs of the virus
                appPreference.setConfidence(0.75F)
            }
            else
            {
                //mild to no symptoms pertaining to the virus.
                appPreference.setConfidence(0.25F)
            }
            if (symptomsArrayListTemplate.size > 0)
            {
                appPreference.setOnAppLaunchInfo(1)
                val intent = Intent(context, MainActivity::class.java)
                activity?.startActivity(intent)
            }
            else
            {
                Snackbar.make(view, "Check all symptoms you've had in the past 14 days", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            }
        }

        private fun changeUI(state: Boolean, isZero:Boolean)
        {
            if (isZero)
            {
                symptomsView.forEach {
                    it?.cross_overlay?.visibility = View.INVISIBLE
                    it?.icons_symptoms?.alpha = 1.0F
                }
            }
            if (state){

                view.cross_overlay?.visibility = View.VISIBLE
                view.icons_symptoms?.alpha = 0.3F
            }
            else
            {
                view.cross_overlay?.visibility = View.INVISIBLE
                view.icons_symptoms?.alpha = 1.0F
            }
        }
        private fun manipulateSymptomsArray(score:Double?, pos: Int) {
            if (symptomsMainList.indexOf(pos) == -1)
            {
                if (pos == 0) //"no symptoms" Card selected
                {
                    symptomsMainList.clear()
                    symptomsArrayListTemplate.clear()
                    changeUI(state = true, isZero = true)
                }
                else
                {
                    if(symptomsMainList.indexOf(0) != -1)
                    {
                        symptomsArrayListTemplate.remove(0.0)
                        symptomsMainList.remove(0)

                        symptomsView[0]?.cross_overlay?.visibility = View.INVISIBLE
                        symptomsView[0]?.icons_symptoms?.alpha = 1.0F
                    }

                    changeUI(state = true, isZero = false)
                }
                symptomsArrayListTemplate.add(score)
                symptomsMainList.add(pos)
            }
            else
            {
                symptomsArrayListTemplate.remove(score)
                symptomsMainList.remove(pos)
                changeUI(false, isZero = false)
            }
        }
        fun bindSymptom(symptom:SymptomsItem, position: Int)
        {
            this.symptom = symptom
            view.caption_symptoms.text = symptom.caption
            symptom.icons?.let { view.icons_symptoms.setImageResource(it) }

            if (symptomsMainList.size > 0)
            {
                if(symptomsMainList.indexOf(position) != -1)
                {
                    changeUI(state = true, isZero = false)
                }
                else
                {
                    changeUI(state = false, isZero = false)
                }
            }
            symptomsView.add(view)
        }
    }
}
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes,this, attachToRoot)
}