package no.nordicsemi.android.blinky.viewmodels

import android.os.Bundle
import no.nordicsemi.android.blinky.R

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /* databaseInstance = LogDatabase(this)
         databaseInstance.addToDatabase("In")
         databaseInstance.addToDatabase("In")
         databaseInstance.addToDatabase("Out")
         println(databaseInstance.getEverything())
         databaseInstance.deleteEntireDB()
         println(databaseInstance.getEverything()) */
        counterReference = findViewById(R.id.count)
    }

}