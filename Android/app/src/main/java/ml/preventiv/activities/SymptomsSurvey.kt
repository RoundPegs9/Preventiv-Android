package ml.preventiv.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import ml.preventiv.R
import ml.preventiv.fragments.symptoms.PreQuestionnaire
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class SymptomsSurvey: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_symptoms)
        var fragment:Fragment = PreQuestionnaire.newInstance()
        this.setDefaultFragment(fragment)
        val fab: FloatingActionButton = findViewById(R.id.fab_sym)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Click on the card that you most strongly agree with", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun setDefaultFragment(defaultFragment: Fragment) {
        this.replaceFragment(defaultFragment)
    }

    private fun replaceFragment(setFragment: Fragment) {
        val fragmentManager:FragmentManager = this.supportFragmentManager
        //fragment transaction
        val fragmentTransaction:FragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentContainer_symptoms, setFragment).commit()
    }

}