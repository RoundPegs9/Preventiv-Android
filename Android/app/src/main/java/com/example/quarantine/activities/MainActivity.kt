package com.example.quarantine.activities

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import androidx.viewpager.widget.ViewPager
import androidx.appcompat.app.AppCompatActivity
import com.example.quarantine.AppPreference
import com.example.quarantine.R
import com.example.quarantine.adapters.SectionsPagerAdapter
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sectionsPagerAdapter =
            SectionsPagerAdapter(
                this,
                this.supportFragmentManager
            )
        val appPreferences = AppPreference(this)
        val confidence = appPreferences.getConfidence()

        Toast.makeText(this.applicationContext, confidence.toString(), Toast.LENGTH_SHORT).show()

        val viewPager: ViewPager = findViewById(R.id.view_pager)

        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        tabs.setupWithViewPager(viewPager)
        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Tap the box you most strongly agree with", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }


    }
}