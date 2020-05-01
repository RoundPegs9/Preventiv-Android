package com.example.quarantine.fragments.heatmap

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.example.quarantine.R
import com.example.quarantine.Utils
import com.example.quarantine.firebase.Firestore
import com.example.quarantine.location.LocationClientManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import java.util.*
import kotlin.collections.HashMap

class HeatMapFragment : Fragment(), OnMapReadyCallback {

    private val TAG = "HeatMapFragment"
    private lateinit var mMap: GoogleMap
    private lateinit var mOverlay:TileOverlay
    private var mProvider: HeatmapTileProvider?= null

    private var isStableNetworkCalled = false
    private lateinit var rootView:View

    private val firestore = Firestore.newInstance()
    private val gradientStartColors = floatArrayOf(
        0.0f, 0.10f, 0.20f, 0.60f, 1.0f
    )
    private val gradientColors = intArrayOf(
        Color.argb(0, 0, 255, 255),  // transparent
        Color.argb(255 / 3 * 2, 0, 255, 255),
        Color.rgb(0, 191, 255),
        Color.rgb(0, 0, 127),
        Color.rgb(255, 0, 0)
    )

    val heatmapGradientSettings =
        Gradient(
            gradientColors,
            gradientStartColors
        )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val root = inflater.inflate(R.layout.fragment_maps, container, false)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        rootView = root

         return root
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)

        if(isVisible && !isStableNetworkCalled)
        {
            isNetworkAvailableMapDisplayOfflineOverlay()
            fabSetFunctionality()
        }
    }

    private fun fabSetFunctionality()
    {
        /**
         * 1. Update SnackBar
         * 2. make Refresh Button Visible
         * 3. add clickListener to fab & fab_refresh_main
         */
        val fab: FloatingActionButton = rootView!!.findViewById(R.id.fabMapInfo)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Real time COVID-19 cases. Note: This data has been auto-generated from users of Preventiv.", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
        val fabRefresh: FloatingActionButton = rootView.findViewById(R.id.fab_refresh)

        fabRefresh.setOnClickListener {
            rotateFabForward(fabRefresh)
            rotateFabBackward(fabRefresh)
            var text = "Map not updated. Requires a network connection to update map."
            if(Utils.isInternetAvailable(activity!!.applicationContext))
            {
                loadMap()
                text = "Map Successfully Updated!"
            }
            Toast.makeText(activity, text, Toast.LENGTH_LONG).show()
        }
    }


    private fun isNetworkAvailableMapDisplayOfflineOverlay()
    {
        /**
         * Check for Wifi
         */
        val isConnectedToWifi = Utils.isInternetAvailable(activity!!.applicationContext)
        if(!isConnectedToWifi && !isStableNetworkCalled)
        {
            AlertDialog.Builder(context)
                .setIcon(R.drawable.no_wifi) //set title
                .setTitle("Real-time updates not possible without WiFi") //set message
                .setMessage("Connect to the Internet to view live real-time COVID-19 updates near you.") //set positive button
                .setPositiveButton("Click to turn on Wifi",
                    DialogInterface.OnClickListener { dialogInterface, i -> //set what would happen when positive button is clicked
                        val mLocationIntent = Intent(Settings.ACTION_WIFI_SETTINGS)
                        startActivity(mLocationIntent)
                        return@OnClickListener
                    })
                .setNegativeButton("Cancel Dialog",
                    DialogInterface.OnClickListener { dialogInterface, i -> //set what would happen when positive button is clicked
                        dialogInterface.dismiss()
                        return@OnClickListener
                    })
                .show()
        }
        isStableNetworkCalled = true
    }

    private fun rotateFabForward(fabRefresh: FloatingActionButton) {
        ViewCompat.animate(fabRefresh)
            .rotation(135.0f)
            .withLayer()
            .setDuration(300L)
            .setInterpolator(OvershootInterpolator(10.0f))
            .start()
    }

    private fun rotateFabBackward(fabRefresh:FloatingActionButton) {
        ViewCompat.animate(fabRefresh)
            .setStartDelay(1000L)
            .rotation(-135.0f)
            .withLayer()
            .setDuration(300L)
            .setInterpolator(OvershootInterpolator(10.0f))
            .start()
    }

    private fun isNight():Boolean
    {
        val hour: Int = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        return hour < 6 || hour > 18
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setMaxZoomPreference(15F)

        /**
         * Add all geolocation onto the map.
         */
        loadMap()

        /**
         * Set Style of the map between day and night depending upon the time of the day.
         */
        if (isNight())
        {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.style_night))
        }
        else
        {
            mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(activity, R.raw.style_day))
        }

        /**
         * Add Market to current place
         */
        if(Utils.isLocationTurnedOn(activity!!.applicationContext) && (
            ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_DENIED
                || ContextCompat.checkSelfPermission(activity!!.applicationContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED))
        {
            LocationClientManager.updateLocation(activity!!.applicationContext)
                .lastLocation.addOnCompleteListener {
                    val currentPosition = LatLng(it.result!!.latitude, it.result!!.longitude)
                    var defaultPosition = LocationClientManager.getAddress(activity!!.applicationContext, currentPosition.latitude, currentPosition.longitude)
                    defaultPosition = if(defaultPosition != null) {
                        defaultPosition.split(",")[0]
                    }else {
                        "You are here"
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 12F))
                    mMap.addMarker(
                        MarkerOptions()
                            .position(currentPosition)
                            .draggable(false)
                            .title(defaultPosition))
                }
        }
        else
        {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.09024,-95.712891), 1F)) //default set to U.S
        }
    }

    private fun loadMap()
    {
        val gpsDataSet: MutableCollection<LatLng> = mutableSetOf()

        firestore.get("gps").addOnCompleteListener {
            if (it.isSuccessful) {
                try {
                    for (document in it.result!!) {
                        val data = document.data as HashMap<*, *>
                        val sliceStr = data.values.toString()
                        Log.d(TAG, sliceStr)
                        for (coordinates in sliceStr.split("{").subList(1, sliceStr.split("{").size)) {
                            val first =
                                coordinates.split("latitude=")[1].split(",")[0].toDoubleOrNull()
                            val second =
                                coordinates.split("longitude=")[1].split("}")[0].toDoubleOrNull()
                            if (first != null && second != null) {
                                val elem = LatLng(first, second)
                                gpsDataSet.add(elem)
                            }
                        }
                    }
                    Log.d(TAG, gpsDataSet.toString())


                } catch (ex: Exception) {
                    Log.w(TAG, "Exception thrown: ${ex.message}")
                }

            } else {
                Log.w(TAG, "Error getting documents, with Exception: ${it.exception}")
            }

            /**
             * Integrate with Maps
             */
            if(gpsDataSet.isNotEmpty())
            {
                if(mProvider == null)
                {
                    Log.d(TAG, "Provide = NULL")
                    mProvider = HeatmapTileProvider.Builder().data(gpsDataSet).build()
                }
                else
                {
                    Log.d(TAG, "Provide != NULL")
                    mOverlay.clearTileCache() //clear cache in case the provider isn't null
                    mProvider!!.setData(gpsDataSet)
                }
                mProvider!!.setGradient(heatmapGradientSettings)
                mProvider!!.setRadius(30)
                mOverlay = mMap.addTileOverlay(TileOverlayOptions().tileProvider(mProvider))
            }

        }
    }

    companion object {
        /**
         * Dynamically Creates a new instance of [HeatMapFragment] class
         */
        fun newInstance() = HeatMapFragment()
    }
}
