package com.example.iotproject

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_light_setting.*
import java.lang.Thread.sleep
import kotlin.math.log
import kotlin.math.roundToInt

class light_setting : AppCompatActivity() {

    private var auto = false
    private var lightIndex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = FirebaseDatabase.getInstance()
        val id = intent.getStringExtra("RoomId");
        val light = database.getReference("/Room/$id/light")
        val lightAuto = database.getReference("/Room/$id/lightAuto")
        var startListen = true
        setContentView(R.layout.activity_light_setting)
        val actionbar = supportActionBar
        actionbar!!.title = "Adjust Brightness"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        LightTitle.text = "The current light brightness"

        lightAuto.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(String::class.java) ?: return
                if(post=="1"){
                    autoOn()
                }else{
                    autoOff()
                }
            }
        })
        light.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(String::class.java) ?: return
                var l = (post.toFloat() / 255 * 100).roundToInt()
//                sleep(100)
                if(startListen) {
                    LightIntensityText.text = l.toString()
                    adjust.progress = post.toInt()
                }
            }
        })
        adjust.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var p = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                p = progress
                var l = (p.toFloat() / 255 * 100).roundToInt()
                LightIntensityText.text = l.toString();
                if(p in 0..255){
                    light.setValue(p.toString())
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                startListen = false
                lightAuto.setValue("0")
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                startListen = true

            }

        })
        autoBtn.setOnClickListener {
            if(!auto){
                auto = true
                lightAuto.setValue("1")
            }else{
                auto = false
                lightAuto.setValue("0")
            }
        }
    }
    fun autoOn(){
        auto = true
        lightIndex = 0
        autoBtn.setBackgroundColor(Color.GREEN)
        autoText.visibility = View.VISIBLE
    }
    fun autoOff(){
        auto = false
        autoBtn.setBackgroundColor(Color.RED)
        autoText.visibility = View.INVISIBLE
    }
    fun listenIntensity(){
//        intensity.addValueEventListener(object: ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//            }
//            override fun onDataChange(p0: DataSnapshot) {
//                val post = p0.getValue(String::class.java) ?: return
//                if(auto){
//                    var p = l.autoLight(post.toInt())
//                    light.setValue(p.toString())
//                }
//            }
//        })
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
