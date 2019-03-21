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
import org.jetbrains.anko.doAsync
import java.text.SimpleDateFormat
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATED_IDENTITY_EQUALS", "unused")

/**
 * The purpose of this activity is to reduce code duplication
 * It can be inherited from any activity, and it inherits AppCompatActivity
 */

open class BaseActivity : AppCompatActivity(), ComponentCallbacks2, TextToSpeech.OnInitListener {
    lateinit var databaseInstance: UserDatabase //Lateinit instance of the database
    private var currentCount = 0 //Keeping track of the currentCount
    private lateinit var tts: TextToSpeech //Lateinit instance of the tts
    private lateinit var db: FirebaseFirestore //FIrestore global instance within base activity

    @Synchronized
    fun boardConnectedSpeak() {
        tts.language = Locale.US
        tts.speak("The board is connected", TextToSpeech.QUEUE_ADD, null)
    }

    @Synchronized
    fun boardDisconnectedSpeak() {
        tts.language = Locale.US
        tts.speak("The board is disconnected, please reconnect", TextToSpeech.QUEUE_ADD, null)
    }

    /**
     * Get timestamp down to ms level
     */

    @SuppressLint("SimpleDateFormat")
    fun getTimeStamp(): String {
        val date = Date()
        return date.time.toString()
    }


    /**
     * This function will update the database accordingly, all that is needed is to pass which sensor to update for
     */
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


    /**
     * This function takes in intArrays, then updates the server accordingly based on timestamps
     */
    private fun removeZeros(list1: IntArray?): MutableList<Int> {
        val mutableList: MutableList<Int> = mutableListOf()
        for (i in list1!!) {
            if (i != 0) {
                mutableList.add(i)
            }
        }
        return mutableList
    }

    fun sendArraysToServer(list1: IntArray?, list2: IntArray?) {

        //convert the array to a mutableList
        val list1IntArray = removeZeros(list1)
        val list2IntArray = removeZeros(list2)

        //sort the arrrays
        list1IntArray.sorted()
        list2IntArray.sorted()

        if(list1IntArray.size==0 || list2IntArray.size==0){
            println(">>>>>>>>>>IN this catch")
            return
        }

        val orderedList: MutableList<String> = mutableListOf()
        val maxSize = list1IntArray.size + list2IntArray.size

        for (i in 0..maxSize) {

            try {
                if (list1IntArray.isNotEmpty() && list2IntArray.isNotEmpty()) {
                    when {
                        list1IntArray[0] < list2IntArray[0] -> {
                            orderedList.add("D1")
                            list1IntArray.removeAt(0)
                        }
                        list1IntArray[0] > list2IntArray[0] -> {
                            orderedList.add("D2")
                            list2IntArray.removeAt(0)
                        }
                        list1IntArray[0] == list2IntArray[0] -> {
                            orderedList.add("D1")
                            list2IntArray.removeAt(0)
                        }
                    }

                }
            } catch (e: Exception) {

            }
        }

        try {
            if (list2IntArray.isEmpty()) {
                orderedList.add("DISTANCE1")
            } else if (list1IntArray.isEmpty()) {
                orderedList.add("DISTANCE2")
            }
        } catch (e: Exception) {

        }

        println("Data   $orderedList")

        doAsync {

            for (i in orderedList) {
                if (i == "D1") {
                    sensorTriggerred("D1")
                } else if (i == "D2") {
                    sensorTriggerred("D2")
                }
            }

        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        tts = TextToSpeech(this, this) //Instantiate tts
        db = FirebaseFirestore.getInstance() //Instantiate firestore
        super.onCreate(savedInstanceState)
    }

    override fun onInit(status: Int) {

    }


    /**
     * This function will get the current timestamp in this format
     * "hh/mm/ss/a"
     */

    @Suppress("unused")
    @SuppressLint("SimpleDateFormat")
    fun getCurrentTimeUsingDate(): String {
        val date = Date()
        val strDateFormat = "hh/mm/ss/a"
        val dateFormat = SimpleDateFormat(strDateFormat)
        return dateFormat.format(date)
    }


    /**
     * This function will get the current date in yyyymmdd format
     */
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
