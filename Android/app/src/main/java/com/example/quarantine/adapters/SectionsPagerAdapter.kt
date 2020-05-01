package com.example.quarantine.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.quarantine.AppPreference
import com.example.quarantine.R
import com.example.quarantine.fragments.alerts.AlertFragment
import com.example.quarantine.fragments.heatmap.HeatMapFragment
import com.example.quarantine.fragments.symptoms.SymptomsFragment

private val TAB_TITLES = arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2,
        R.string.tab_text_3
)
private val TAB_TITLES_initial = arrayOf(
    R.string.tab_text_1,
    R.string.tab_text_3
)


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, private val fm: FragmentManager)
    : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
        return when(position) {
            0 -> SymptomsFragment.newInstance()
            1-> if(AppPreference(context).isAlertAdded()){
                AlertFragment.newInstance()
            } else {
                HeatMapFragment.newInstance()
            }
            else -> HeatMapFragment.newInstance()
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        if(AppPreference(context).isAlertAdded())
        {
            return context.resources.getString(TAB_TITLES[position])
        }
        return context.resources.getString(TAB_TITLES_initial[position])
    }

    override fun getCount(): Int {
//         Show 3-4 total pages.
        if(AppPreference(context).isAlertAdded())
        {
            return TAB_TITLES.size
        }
        return TAB_TITLES_initial.size
    }

}
