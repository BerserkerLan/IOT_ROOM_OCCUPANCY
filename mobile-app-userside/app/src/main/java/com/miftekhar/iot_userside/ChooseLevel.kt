package com.miftekhar.iot_userside

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ChooseLevel : BaseActivity() {
    private lateinit var myWebView: WebView

    @SuppressLint("SetJavaScriptEnabled")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val faq = mutableMapOf<String, String>() //map to store a list of questions and answers
        faq["Level 3"] = "3"
        faq["Level 4"] = "4"
        faq["Level 5"] = "5"
        faq["Level 6"] = "6"
        faq["Level 7"] = "7"
        faq["Level 8"] = "8"
        faq["Level 9"] = "9"

        scrollView {
            verticalLayout {
                for (f in faq) {
                    button {
                        text = f.key
                        background = if (f.value == "4") {
                            buttonGreen()
                        } else {
                            buttonGrey()
                        }
                        onClick {
                            if (f.value == "4") {
                                switchActivity(ChooseRoom())
                            } else {
                                toast("Data on this room is not available")
                            }
                        }
                    }.lparams(width = matchParent) {
                        topMargin = dip(5)
                    }
                }
            }
        }
    }
}
