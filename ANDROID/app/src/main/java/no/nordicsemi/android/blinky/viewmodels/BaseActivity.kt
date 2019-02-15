package no.nordicsemi.android.blinky.viewmodels

import android.app.Activity
import android.content.ComponentCallbacks2
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent





@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATED_IDENTITY_EQUALS")
open class BaseActivity : AppCompatActivity(), ComponentCallbacks2 {

    fun switchActivity(activityName:Activity){
        val myIntent = Intent(this, activityName::class.java)
        startActivity(myIntent)
    }

}
