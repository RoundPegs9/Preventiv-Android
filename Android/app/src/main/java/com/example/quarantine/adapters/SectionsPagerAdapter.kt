package com.example.quarantine.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.quarantine.R
import com.example.quarantine.fragments.symptoms.MainQuestions
import com.example.quarantine.fragments.symptoms.PreQuestionnaire
import com.example.quarantine.ui.main.PlaceholderFragment

private val TAB_TITLES = arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2,
        R.string.tab_text_3
)


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, private val fm: FragmentManager)
    : FragmentPagerAdapter(fm) {
    override fun getItem(position: Int): Fragment {
//        fm.beginTransaction()
//        val addFragment = fm.beginTransaction().add(R.id.fragmentContainer, PreQuestionnaire.newInstance())
        return PlaceholderFragment.newInstance(position+1)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 3 total pages.
        return TAB_TITLES.size
    }
}
