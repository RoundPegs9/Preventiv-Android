package ml.preventiv.activities


import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import ml.preventiv.AppPreference
import ml.preventiv.Utils
import ml.preventiv.adapters.SectionsPagerAdapter
import ml.preventiv.bluetooth.foreground.FService
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import ml.preventiv.R


class MainActivity : AppCompatActivity() {

    private val TAG = "MAIN_ACTIVITY"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter =
            SectionsPagerAdapter(
                this,
                this.supportFragmentManager
            )
        val appPreferences = AppPreference(this)
        Log.i(TAG, appPreferences.getConfidence().toString())

        /**
         * Debug for Timestamp feature...
         */
        //appPreferences.clearConnections() //debugging - removes all connections. make sure to delete it later!

        Log.d(TAG, "Connections:" + appPreferences.getConnections())
        Log.d(TAG, "My UUID: " + appPreferences.getUUID()?.substring(appPreferences.getUUID()!!.length - 10))
        Log.d(TAG, "Current time for reference: ${Utils.getTimeStamp()}")


        val viewPager: ViewPager = findViewById(R.id.view_pager)

        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        tabs.setSelectedTabIndicatorColor(Color.parseColor("#c8e8ff"))
        tabs.setTabTextColors(Color.parseColor("#c8e8ff"), Color.parseColor("#FFFFFF"))
        if(appPreferences.isAlertAdded())
        {
            viewPager.setCurrentItem(1, true)
        }

        /**
         * Main Activity BLE Text semantics
         */
        if(!appPreferences.getPermission()) //permission not set
        {
            BLE_scanner_service.text = "Grant Access"
        }
        else
        {
            if (!FService.IS_RUNNING)
            {
                BLE_scanner_service.text = "Start Saving Lives"
            }
            else
            {
                BLE_scanner_service.text = "Disable Superhero mode"

            }
        }


        /**
         * Need to change the location of when to seek permission.
         * Works for now.
         */

        Log.i(TAG, appPreferences.getPermission().toString())
    }

    private fun checkPermission(): Boolean {
        val isBluetoothOn = Utils.isBluetoothAvailable()
        if(!isBluetoothOn)
        {
            AlertDialog.Builder(this)
                .setIcon(R.drawable.no_bluetooth) //set title
                .setTitle("Bluetooth not turned on") //set message
                .setMessage("Turn on your Bluetooth Connection to enable Contact Tracing.") //set positive button
                .setPositiveButton("Click to enable bluetooth",
                    DialogInterface.OnClickListener { _, _ -> //set what would happen when positive button is clicked
                        val mLocationIntent = Intent(Settings.ACTION_BLUETOOTH_SETTINGS)
                        startActivity(mLocationIntent)
                        return@OnClickListener
                    }) //set negative button
                .show()
            return false
        }


        if(!Utils.isLocationTurnedOn(this))
        {
            AlertDialog.Builder(this)
                .setIcon(R.drawable.no_gps) //set title
                .setTitle("Location not turned on") //set message
                .setMessage("Turn on your Location to get instant COVID-19 updates near you.") //set positive button
                .setPositiveButton("Click to enable location",
                    DialogInterface.OnClickListener { _, _ ->
                        val mLocationIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivity(mLocationIntent)
                        return@OnClickListener
                    }) //set negative button
                .show()

            return false
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 200)
                return false
            }
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        for (i in grantResults.indices) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                /**
                 * Launch an activity instead saying permission required to 'save'
                 * the world or something like that.
                 */
                BLE_scanner_service.text = "Grant Access"
                AppPreference(this).setPermission(false)
                this.finish()
                return
            }

        }
        BLE_scanner_service.text = "Start Saving Lives"
        AppPreference(this).setPermission(true)
    }

    fun launchService(view:View) {
        checkPermission()
        Log.i(TAG, "Button Clicked: ${checkPermission()}")
        if(!checkPermission()) //permission not set
        {
            BLE_scanner_service.text = "Grant Access"
        }
        else
        {
            conditionalService()
        }

    }
    private fun conditionalService()
    {
        if (!FService.IS_RUNNING) {
            BLE_scanner_service.text = "Disable Superhero Mode"

            /**
             * Load Intent.
             * Send fusedLocationProviderClient
             */

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(Intent(applicationContext, FService::class.java))
            } else {
                startService(Intent(applicationContext, FService::class.java))
            }
            Toast.makeText(this, "Bluetooth Contact Tracing Activated", Toast.LENGTH_SHORT).show()
        }
        else
        {
            BLE_scanner_service.text = "Start Saving Lives"
            stopService(Intent(applicationContext, FService::class.java))
            Toast.makeText(this, "Bluetooth Contact Tracing Deactivated", Toast.LENGTH_SHORT).show()
        }
    }
}