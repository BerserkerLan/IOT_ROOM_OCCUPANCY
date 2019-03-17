package no.nordicsemi.android.blinky.Activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentCallbacks2
import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore
import no.nordicsemi.android.blinky.utils.UserDatabase
import java.text.SimpleDateFormat
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATED_IDENTITY_EQUALS")


open class BaseActivity : AppCompatActivity(), ComponentCallbacks2, TextToSpeech.OnInitListener {
    lateinit var databaseInstance: UserDatabase //Lateinit instance of the database
    var currentCount = 0 //Keeping track of the currentCount
    lateinit var tts: TextToSpeech //Lateinit instance of the tts
    lateinit var db: FirebaseFirestore

    @SuppressLint("SimpleDateFormat")
    fun getTimeStamp(): String {
        val date = Date()
        return date.time.toString()
    }

    @Synchronized
    fun sensorTriggerred(sensorType: String) {
        val user = HashMap<String, Any>()
        user[getTimeStamp()] = 1
        // Add a new document
        when (sensorType) {
            "DISTANCE1" -> {
                db.collection("D1").document(getDate())
                        .update(user)
                        .addOnSuccessListener {}
                        .addOnFailureListener {}
            }
            "DISTANCE2" -> {
                db.collection("D2").document(getDate())
                        .update(user)
                        .addOnSuccessListener {}
                        .addOnFailureListener {}
            }
        }
    }

    fun hexToInt(a:String): Int{
        return 0
    }

    fun convertArray(a: String):List<String> {
        val listToReturn: MutableList<String> = ((a.split(" ")).toString()).split("-") as MutableList<String>
        listToReturn.removeAt(0)
        return listToReturn
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        tts = TextToSpeech(this, this)
        db = FirebaseFirestore.getInstance()
        super.onCreate(savedInstanceState)
    }

    override fun onInit(status: Int) {

    }

    @Suppress("unused")
    @SuppressLint("SimpleDateFormat")
    fun getCurrentTimeUsingDate(): String {
        val date = Date()
        val strDateFormat = "hh/mm/ss/a"
        val dateFormat = SimpleDateFormat(strDateFormat)
        return dateFormat.format(date)
    }


    @SuppressLint("SimpleDateFormat")
    private fun getDate(): String {
        val cal = Calendar.getInstance()
        val format1 = SimpleDateFormat("yyyyMMdd")
        val formatted = format1.format(cal.time)
        println(formatted.length)
        return formatted.toString()
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

    /**
     * Function to make switching activities a little easier
     */
    @Suppress("unused")
    fun switchActivity(activityName: Activity) {
        val myIntent = Intent(this, activityName::class.java)
        startActivity(myIntent)
    }

}
