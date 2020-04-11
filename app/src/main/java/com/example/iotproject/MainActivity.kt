package com.example.iotproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.*
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        txt_welcome.visibility = View.GONE
        txt_to.visibility = View.GONE
        txt_smartroom.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()

        TransitionManager.beginDelayedTransition(Constraint_Layout, Fade().setDuration(1000).setStartDelay(5000))

        txt_welcome.visibility = View.VISIBLE
        txt_to.visibility = View.VISIBLE
        txt_smartroom.visibility = View.VISIBLE
    }
}
