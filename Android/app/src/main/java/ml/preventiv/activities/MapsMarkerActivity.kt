package ml.preventiv.activities

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ml.preventiv.R
import ml.preventiv.firebase.Firestore
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.heatmaps.HeatmapTileProvider

class MapsMarkerActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val firestore: Firestore = Firestore.newInstance()
    private lateinit var mOverlay:TileOverlay
    private var mProvider: HeatmapTileProvider?= null

    private val TAG = "MapsMarkerActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marker_map)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map_marker) as SupportMapFragment
        mapFragment.getMapAsync(this)
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
                mProvider!!.setRadius(30)
                mOverlay = mMap.addTileOverlay(TileOverlayOptions().tileProvider(mProvider))
            }

        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        loadMap()
        val locationString = intent?.extras?.getString("REVERSE_GEOLOCATION")
        val data = Geocoder(this).getFromLocationName(locationString!!, 1)[0]

        mMap.addMarker(MarkerOptions().position(LatLng(data!!.latitude, data.longitude)).title(locationString.split(",")[0]))
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(data.latitude, data.longitude), 15F))
        mMap.setMaxZoomPreference(15F)
        mMap.addCircle(
            CircleOptions()
                .center(LatLng(data.latitude, data.longitude))
                .radius(50.0)
                .strokeColor(R.color.markerFilledRadiusColor)
                .fillColor(R.color.markerFilledRadiusColor)
        )

    }
}
