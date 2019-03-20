package com.miftekhar.iot_userside

import android.os.Bundle
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class ChooseRoom : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val faq = mutableMapOf<String, String>() //map to store a list of questions and answers
        faq["4.05"] = ""
        faq["4.06"] = ""
        faq["4.07"] = ""
        faq["4.08"] = ""
        faq["4.09"] = ""
        faq["4.10"] = ""
        faq["4.11"] = ""
        faq["4.12"] = "4.12"
        faq["4.13"] = ""
        faq["4.14"] = ""
        faq["4.14A"] = ""

        scrollView {
            verticalLayout {
                for (f in faq) {
                    button {
                        background
                        text = f.key
                        background = if (f.value == "4.12") {
                            buttonGreen()
                        } else {
                            buttonGrey()
                        }
                        onClick {
                            if (f.value == "4.12") {
                                switchActivity(WebView())
                            } else {
                                toast("Data on this room is not available")
                            }
                        }
                    }.lparams(width = matchParent) {
                        topMargin = dip(10)
                    }
                }
            }
        }

    }
}
