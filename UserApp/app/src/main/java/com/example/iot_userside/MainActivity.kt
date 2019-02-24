package com.example.iot_userside

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.WindowManager
import kotlinx.android.synthetic.*
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick


@Suppress("DEPRECATION")
class MainActivity : BaseActivity() {
    private var continueThread = true
    private var userGoThrough = false
    override fun onBackPressed() {
        clearFindViewByIdCache()
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        startActivity(intent)
    }


    @SuppressLint("ApplySharedPref")
    private fun saveInt(key: String, value: Int) {
        /* Function to save an SharedPreference value which holds an Int*/
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.commit()
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() //hide actionbar
        userGoThrough = false

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON) //This will keep the screen on, overriding users settings
        verticalLayout {
            imageView(R.drawable.ic_launcher) {
                backgroundColor = Color.TRANSPARENT //Removes gray border
                horizontalPadding = dip(10)
                verticalPadding = dip(15)
            }.lparams(width = matchParent) {
                topMargin = dip(150)
            }

            button("VIEW") {
                textSize = 32f
                //background = ResourcesCompat.getDrawable(resources, R.drawable.rb2, null) Using XML
                background = buttonGreen() // Using kotlin - better ;)
                onClick {
                    switchActivity(ChooseLevel())
                }
            }
        }
    }


}
