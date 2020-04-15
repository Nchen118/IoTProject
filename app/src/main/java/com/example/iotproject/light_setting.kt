package com.example.iotproject

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_light_setting.*

class light_setting : AppCompatActivity() {


    private var lightValue = 0
    private var auto = false
    private val database = FirebaseDatabase.getInstance()
    private val light = database.getReference("/Room/light")
    private val lightAuto = database.getReference("/Room/lightAuto")
    private val intensity = database.getReference("/Room/lightIntensity")
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
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(String::class.java) ?: return
                if(post=="1"){
                    autoOn()
//                    listenIntensity()
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
                lightValue = l.lightPowerCon(post.toInt())
                LightIntensityText.text = lightValue.toString()


                if(lightValue== 0){
                    increase.setBackgroundColor(Color.GREEN)
                    increase.isEnabled = true
                    decrease.setBackgroundColor(Color.GRAY)
                    decrease.isEnabled = false
                }else if(lightValue == 5){
                    increase.setBackgroundColor(Color.GRAY)
                    increase.isEnabled = false
                    decrease.setBackgroundColor(Color.GREEN)
                    decrease.isEnabled = true
                }else{
                    increase.setBackgroundColor(Color.GREEN)
                    increase.isEnabled = true
                    decrease.setBackgroundColor(Color.GREEN)
                    decrease.isEnabled = true
                }
                updateText()
            }
        })
        decrease.setOnClickListener {
            auto = false
            lightAuto.setValue("0")
            if(lightValue==0){
                updateText()
            }
            else{
                lightValue--
                light.setValue(l.lightPower(lightValue).toString())
                updateText()
                increase.setBackgroundColor(Color.GREEN)
                increase.isEnabled = true
            }
        }
        increase.setOnClickListener{
            auto = false
            lightAuto.setValue("0")
            if(lightValue==5){
                updateText()
            }
            else{
                lightValue++
                light.setValue(l.lightPower(lightValue).toString())
                updateText()
                decrease.setBackgroundColor(Color.GREEN)
                decrease.isEnabled = true
            }
        }
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
    fun updateText(){
        LightIntensity.text = lightValue.toString()
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
        when (lightValue) {
            0 -> {
                increase.setBackgroundColor(Color.GREEN)
                increase.isEnabled = true
                decrease.setBackgroundColor(Color.GRAY)
                decrease.isEnabled = false
            }
            5 -> {
                increase.setBackgroundColor(Color.GRAY)
                increase.isEnabled = false
                decrease.setBackgroundColor(Color.GREEN)
                decrease.isEnabled = true
            }
            else -> {
                increase.setBackgroundColor(Color.GREEN)
                increase.isEnabled = true
                decrease.setBackgroundColor(Color.GREEN)
                decrease.isEnabled = true
            }
        }
    }
//    fun listenIntensity(){
//        intensity.addValueEventListener(object: ValueEventListener {
//            override fun onCancelled(p0: DatabaseError) {
//            }
//            override fun onDataChange(p0: DataSnapshot) {
//                val post = p0.getValue(String::class.java) ?: return
//                if(auto){
//                    var p = l.autoLight(post.toInt())
//
//                    light.setValue(p.toString())
//                }
//            }
//        })
//    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
