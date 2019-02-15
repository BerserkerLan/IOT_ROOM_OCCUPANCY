package no.nordicsemi.android.blinky.viewmodels

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.RelativeLayout
import android.widget.TextView
import no.nordicsemi.android.blinky.R
import no.nordicsemi.android.blinky.utils.LogDatabase

class MainActivity : BaseActivity(), TextToSpeech.OnInitListener {

    override fun onInit(status: Int) {

    }

    lateinit var databaseInstance: LogDatabase
    lateinit var counterReference: TextView
    var currentCount = 0
    lateinit var tts: TextToSpeech

    public override fun onDestroy() {
        tts.stop()
        tts.shutdown()
        super.onDestroy()
    }


    private fun speakOutWlcome() {
        val text = "Welcome"
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }


    private fun speakOutGoodBye() {
        val text = "GoodBye"
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        databaseInstance = LogDatabase(this)
        databaseInstance.addToDatabase("In")
        databaseInstance.addToDatabase("In")
        databaseInstance.addToDatabase("Out")
        println(databaseInstance.getEverything())
        databaseInstance.deleteEntireDB()
        println(databaseInstance.getEverything())
        counterReference = findViewById(R.id.count)
        tts = TextToSpeech(this, this)

        val layout = findViewById<RelativeLayout>(R.id.layout)
        layout.setOnClickListener {
            incrementCounter()
            speakOutWlcome()
        }

        layout.setOnLongClickListener {
            decrementCounter()
            speakOutGoodBye()
            true
        }
    }


    private fun incrementCounter() {
        currentCount++
        try {
            counterReference.text = currentCount.toString()
        } catch (e: Exception) {

        }
    }

    private fun decrementCounter() {
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
}
