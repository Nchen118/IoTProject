package com.example.iotproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.transition.Fade
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.util.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Starting_Layout.visibility = View.VISIBLE
        Intro_layout.visibility = View.GONE

        var id = readId()

        if (id.isEmpty()) {
            generateId()
            id = readId()
        }

        Log.d("phoneid", id)

        val sharedPreferences = getSharedPreferences("phoneId", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("phoneId", id).apply()
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

    private fun generateId() {
        val id = UUID.randomUUID().toString()
        val fileOutputStream: FileOutputStream
        try {
            fileOutputStream = openFileOutput("phoneId", Context.MODE_PRIVATE)
            fileOutputStream.write(id.toByteArray())
        } catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun readId(): String {
        val stringBuilder = StringBuilder()
        var text: String? = null
        try {
            val bufferedReader = BufferedReader(InputStreamReader(openFileInput("phoneId")))
            while ({ text = bufferedReader.readLine(); text }() != null) {
                stringBuilder.append(text)
            }
        } catch (e: Exception) {
        }
        return stringBuilder.toString()
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        if(item.itemId == R.id.qrCode){
            startActivity(Intent(this, qrCodeGenerator::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
