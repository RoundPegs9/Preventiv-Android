package com.example.quarantine.ui.main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter
import com.example.quarantine.R
import com.example.quarantine.fragments.symptoms.MainQuestions
import com.example.quarantine.fragments.symptoms.PreQuestionnaire

private val TAB_TITLES = arrayOf(
        R.string.tab_text_1,
        R.string.tab_text_2,
        R.string.tab_text_3
)


/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
class SectionsPagerAdapter(private val context: Context, fm: FragmentManager)
    : FragmentStatePagerAdapter(fm) {
    private var mFragmentManager: FragmentManager? = fm //fragment manager
    private var mFragmentAtPosition0: Fragment? = null // fragment to display once & keep track of changes

    override fun getItem(position: Int): Fragment {
        return (if (position == 0) {
            if (mFragmentAtPosition0 == null) {
                mFragmentAtPosition0 =
                    PreQuestionnaire.newInstance(object : PageFragmentListener {
                        override fun onSwitchToNextFragment() {
                            mFragmentAtPosition0.let {
                                if (it != null) {
                                    mFragmentManager!!.beginTransaction().remove(
                                        it
                                    ).commit()
                                }
                            }
                            mFragmentAtPosition0 = MainQuestions.newInstance()
                            notifyDataSetChanged()
                        }
                    })
            }
            mFragmentAtPosition0
        } else PreQuestionnaire.newInstance())!!
    }


//    override fun getItem(position: Int): Fragment {
//        // getItem is called to instantiate the fragment for the given page.
//        // Return a PlaceholderFragment (defined as a static inner class below).
//        return when(position) {
//            0-> PreQuestionnaire.newInstance()
//            1-> PlaceholderFragment.newInstance(1)
//            else -> PreQuestionnaire.newInstance()
//        }
//    }

    override fun getItemPosition(`object`: Any): Int {
        if (`object` is PreQuestionnaire && mFragmentAtPosition0 is MainQuestions)
        {
            return PagerAdapter.POSITION_NONE
        }
        return PagerAdapter.POSITION_UNCHANGED
    }
    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getString(TAB_TITLES[position])
    }

    override fun getCount(): Int {
        // Show 3 total pages.
        return 3
    }
}
interface PageFragmentListener {
    fun onSwitchToNextFragment()
}