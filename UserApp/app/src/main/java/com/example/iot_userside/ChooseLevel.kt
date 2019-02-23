package com.example.iot_userside

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.webkit.WebView
import org.jetbrains.anko.alert
import org.jetbrains.anko.button
import org.jetbrains.anko.scrollView
import org.jetbrains.anko.sdk27.coroutines.onClick
import org.jetbrains.anko.verticalLayout

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ChooseLevel : AppCompatActivity() {
    private lateinit var myWebView: WebView

    @SuppressLint("SetJavaScriptEnabled")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val faq = mutableMapOf<String, String>() //map to store a list of questions and answers
        faq["If I delete a user from a tile, do the messages get deleted?"] =
            "No, the messages will remain, so if you change your mind you can pick up where you left off!"
        faq["Can I customise the app?"] = "Yes, you can customize many aspects of the app in the settings"
        faq["I keep accidentally sending messages"] =
            "You can turn on 'Confirm before sending' and you will be prompted to double confirm before sending"
        faq["Can I see messages I've sent?"] = "Yes, you can see this in the app 'SMS LOG'"
        faq["I have some suggestions, how can I share them?"] =
            "We're more than happy to hear you suggestions! User the contact table, accessible from 'options'"
        faq["When I send a message, it says 'Sorry, couldn't send SMS'"] =
            "This may be due to many reasons e.g. No reception, out of PAYG credit"

        scrollView {
            verticalLayout {
                /*programmatically adding buttons based on the number of faq's, this method was
                adopted to reduce the amount of code and make adding more faq's easy */
                for (f in faq) {
                    button {
                        text = f.key
                        onClick {
                            if(f.value=="4"){
                                //SwitchActivity
                            }

                        }
                    }
                }
            }
        }
    }
}
