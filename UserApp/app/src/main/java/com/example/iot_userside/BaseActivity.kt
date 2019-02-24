package com.example.iot_userside

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_base)
    }

    fun switchActivity(activityName: Activity) {
        val myIntent = Intent(this, activityName::class.java)
        startActivity(myIntent)
    }

    fun buttonGreen() = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = 100f
        setColor(Color.parseColor("#32CD32"))
        setStroke(2, Color.BLACK)
    }

    fun buttonGrey() = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
        cornerRadius = 100f
        setColor(resources.getColor(R.color.material_blue_grey_800))
        setStroke(2, Color.BLACK)
    }


}
