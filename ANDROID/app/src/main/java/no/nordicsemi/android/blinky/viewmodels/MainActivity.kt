package no.nordicsemi.android.blinky.viewmodels

import android.os.Bundle
import no.nordicsemi.android.blinky.R
import no.nordicsemi.android.blinky.utils.LogDatabase

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

                val a = LogDatabase(this)
                a.addToDatabase("In")
                a.addToDatabase("In")
                a.addToDatabase("Out")
                println(a.getEverything())
                a.deleteEntireDB()
                println(a.getEverything())

    }
}
