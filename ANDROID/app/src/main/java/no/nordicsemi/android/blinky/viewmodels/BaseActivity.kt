package no.nordicsemi.android.blinky.viewmodels

import android.app.Activity
import android.content.ComponentCallbacks2
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import no.nordicsemi.android.blinky.utils.UserDatabase


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATED_IDENTITY_EQUALS")

open class BaseActivity : AppCompatActivity(), ComponentCallbacks2, TextToSpeech.OnInitListener {
    lateinit var databaseInstance: UserDatabase //Lateinit instance of the database
    lateinit var counterReference: TextView //Lateinit counter reference
    var currentCount = 0 //Keeping track of the currentCount
    lateinit var tts: TextToSpeech //Lateinit instance of the tts
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        tts = TextToSpeech(this, this)
        db = FirebaseFirestore.getInstance()

        super.onCreate(savedInstanceState)
    }

    override fun onInit(status: Int) {

    }


    /**
     * Overriding onDestroy, to safely disable the TTS
     */
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


    /**
     * This will increment the counter
     */
    fun incrementCounter() {
        currentCount++
        try {
            counterReference.text = currentCount.toString()
        } catch (e: Exception) {

        }
    }


    /**
     * This will decrement the counter, but not allow non zero
     */
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

    /**
     * Function to make switching activities a little easier
     */
    @Suppress("unused")
    fun switchActivity(activityName: Activity) {
        val myIntent = Intent(this, activityName::class.java)
        startActivity(myIntent)
    }

}
