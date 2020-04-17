package com.example.iotproject

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_light_setting.*
import java.lang.Thread.sleep
import kotlin.math.roundToInt

class light_setting : AppCompatActivity() {

    private var auto = false
    private val database = FirebaseDatabase.getInstance()
    private val id = 1;
    private val light = database.getReference("/Room/$id/light")
    private val lightAuto = database.getReference("/Room/$id/lightAuto")
    private val intensity = database.getReference("/Room/$id/lightIntensity")
    private var l = Library()
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
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
                    adjust.progress = post.toInt()
                var l = (post.toFloat() / 255 * 100).roundToInt()
                LightIntensityText.text = l.toString();
            }
        })

        adjust.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var p = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                p = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                lightAuto.setValue("0")
            }
            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if(p in 0..255){
                    light.setValue(p.toString())
                }
            }
        })
        autoBtn.setOnClickListener {
            if(!auto){
                auto = true
                lightAuto.setValue("1")
                autoOn()
            }else{
                auto = false
                lightAuto.setValue("0")
                autoOff()
            }
        }
    }
    fun autoOn(){
        auto = true
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
