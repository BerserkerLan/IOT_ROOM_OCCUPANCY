package no.nordicsemi.android.blinky.viewmodels

import android.app.Activity
import android.content.ComponentCallbacks2
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import no.nordicsemi.android.blinky.utils.LogDatabase


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATED_IDENTITY_EQUALS")

open class BaseActivity : AppCompatActivity(), ComponentCallbacks2, TextToSpeech.OnInitListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        tts = TextToSpeech(this, this)


        /*val layout = findViewById<RelativeLayout>(R.id.layout)

        layout.setOnClickListener {
            incrementCounter()
            speakOutWlcome()
        }

        layout.setOnLongClickListener {
            decrementCounter()
            speakOutGoodBye()
            true
        } */
        super.onCreate(savedInstanceState)
    }

    override fun onInit(status: Int) {

    }

    lateinit var databaseInstance: LogDatabase
    lateinit var counterReference: TextView
    var currentCount = 0
    lateinit var tts: TextToSpeech

    public override fun onDestroy() {
        try {
            tts.stop()
        } catch (e: Exception) {

        }
        try {
            tts.shutdown()
        } catch (e: Exception) {

        }
        super.onDestroy()
    }


    fun resetCounter() {
        currentCount = 0
    }

    fun speakOutWlcome() {
        val text = "Welcome"
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }


    fun speakOutGoodBye() {
        val text = "GoodBye"
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }


    fun incrementCounter() {
        currentCount++
        try {
            counterReference.text = currentCount.toString()
        } catch (e: Exception) {

        }
    }

    fun decrementCounter() {
        currentCount--
        if (currentCount >= 0) {
            try {
                counterReference.text = currentCount.toString()
            } catch (e: Exception) {

            }
        } else {
            //Not allowing counter to go negative basically
            currentCount = 0
            try {
                counterReference.text = currentCount.toString()
            } catch (e: Exception) {

            }
        }
    }

    @Suppress("unused")
    fun switchActivity(activityName: Activity) {
        val myIntent = Intent(this, activityName::class.java)
        startActivity(myIntent)
    }

}
