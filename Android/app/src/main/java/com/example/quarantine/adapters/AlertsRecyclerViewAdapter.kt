package com.example.quarantine.adapters

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.quarantine.R
import com.example.quarantine.Utils
import com.example.quarantine.activities.MapsMarkerActivity
import com.example.quarantine.models.alerts.AlertsItem
import kotlinx.android.synthetic.main.list_view_alerts.view.*


class AlertsRecyclerViewAdapter(val context: Context, private val alerts: List<AlertsItem>) : RecyclerView.Adapter<AlertsRecyclerViewAdapter.AlertViewHolder>() {
    inner class AlertViewHolder(alertView: View) : RecyclerView.ViewHolder(alertView)
    {
        fun setData(alert: AlertsItem, position:Int) {
            alert.let {

                itemView.alertAddress.text = alert.address!!.split(",").subList(0, 2).toString().removeSuffix("]").removePrefix("[")
                itemView.alertDate.text = alert.date
                this.currentAlertItem = alert
                this.currentPosition = position
            }

        }
        var currentAlertItem: AlertsItem? = null
        var currentPosition : Int = 0
        init {
            itemView.setOnClickListener{
                currentAlertItem?.let {
                    /**
                     * Launch Map Activity. But first check for wifi.
                     */
                    if(Utils.isInternetAvailable(context))
                    {
                        val intent = Intent(context, MapsMarkerActivity::class.java)
                        intent.putExtra("REVERSE_GEOLOCATION", this.currentAlertItem!!.address)
                        context.startActivity(intent)
                    }
                    else
                    {
                        Toast.makeText(context, "Connect to Internet to locate this address on a Map.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_view_alerts, parent, false)
        return AlertViewHolder(view)
    }

    override fun getItemCount(): Int {
        return alerts.size
    }

    override fun onBindViewHolder(holder: AlertViewHolder, position: Int) {
        val alert = alerts[position]
        holder.setData(alert, position)
    }
}