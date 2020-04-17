package com.example.iotproject

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_fan_setting.*

class fan_setting : AppCompatActivity() {


    private var fanValue = 0
    private var auto = false
    private var roomId:Int = 1
    private val database = FirebaseDatabase.getInstance()
    private val fan = database.getReference("/Room/$roomId/fan")
    private val fanAuto = database.getReference("/Room/$roomId/fanAuto")
    private val temp = database.getReference("/Room/$roomId/temp")
    private var l = Library()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fan_setting)

        val actionbar = supportActionBar
        actionbar!!.title = "Adjust Fan Speed"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        FanTitle.text = "The current fan speed"
        fanAuto.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(String::class.java) ?: return
                if(post=="1"){
                    autoOn()
//                    listenTemp()
                }else{
                    autoOff()
                }
            }
        })
        fan.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(String::class.java) ?: return
                FanSpeedText.text = post
                fanValue = post.toInt()
                if(fanValue== 0){
                    increase.setBackgroundColor(Color.GREEN)
                    increase.isEnabled = true
                    decrease.setBackgroundColor(Color.GRAY)
                    decrease.isEnabled = false
                }else if(fanValue == 5){
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
            fanAuto.setValue("0")
            if(fanValue==0){
                updateText()
            }
            else{
                fanValue--
                fan.setValue(fanValue.toString())
                updateText()
                increase.setBackgroundColor(Color.GREEN)
                increase.isEnabled = true
            }
        }
        increase.setOnClickListener{
            auto = false
            fanAuto.setValue("0")
            if(fanValue==5){
                updateText()
            }
            else{
                fanValue++
                fan.setValue(fanValue.toString())
                updateText()
                decrease.setBackgroundColor(Color.GREEN)
                decrease.isEnabled = true
            }
        }
        autoBtn.setOnClickListener {
            if(!auto){
                auto = true
                fanAuto.setValue("1")
                autoOn()
            }else{
                auto = false
                fanAuto.setValue("0")
                autoOff()
            }
        }
    }
    fun updateText(){
        FanSpeed.text = fanValue.toString()
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
        when (fanValue) {
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
