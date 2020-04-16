package com.example.iotproject

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.Gravity
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.qrcode.encoder.QRCode
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Starting_Layout.visibility = View.VISIBLE
        Intro_layout.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()

        Handler().postDelayed({
            TransitionManager.beginDelayedTransition(
                Starting_Layout,
                Fade().setDuration(1000)
            )

            txt_welcome.visibility = View.VISIBLE
            txt_to.visibility = View.VISIBLE
            txt_smartroom.visibility = View.VISIBLE

            Handler().postDelayed({
                TransitionManager.beginDelayedTransition(
                    Starting_Layout,
                    Fade().setDuration(1000)
                )

                touch_start.visibility = View.VISIBLE

                Starting_Layout.setOnClickListener {
                    TransitionManager.beginDelayedTransition(
                        Main_Layout,
                        TransitionSet().addTransition(Fade().setDuration(1000))
                    )

                    Starting_Layout.visibility = View.GONE

                    Handler().postDelayed({
                        TransitionManager.beginDelayedTransition(
                            Main_Layout,
                            TransitionSet().addTransition(Fade().setDuration(1000))
                        )
                        Intro_layout.visibility = View.VISIBLE

                    }, 1000)
                }
            }, 500)

        }, 100)

        scan_qr_btn.setOnClickListener {
            startActivity(Intent(this, QrCode::class.java))
        }
    }
}
