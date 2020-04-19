package com.example.iotproject

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_fan_setting.*

class fan_setting : AppCompatActivity() {


    private var fanValue = 0
    private var auto = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val roomId = intent.getStringExtra("RoomId");
        val database = FirebaseDatabase.getInstance()
        val fan = database.getReference("/Room/$roomId/fan")
        val fanAuto = database.getReference("/Room/$roomId/fanAuto")
        val temp = database.getReference("/Room/$roomId/temp")
        setContentView(R.layout.activity_fan_setting)

        val actionbar = supportActionBar
        actionbar!!.title = "Adjust Fan Speed"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        FanTitle.text = "The current fan speed"
        autoBtn.isEnabled = true
        fanAuto.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(String::class.java) ?: return
                if(post=="1"){
                    autoOn()
                }else if(post=="0"){
                    autoOff()
                }else{
                    autoBtn.isEnabled = false
                }
            }
        })
        fan.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(String::class.java) ?: ""
                if(post==""){
                    increase.isEnabled = false
                    decrease.isEnabled = false
                    FanSpeedText.text = "Error"
                }else{
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

            }
        })
        decrease.setOnClickListener {
            autoOff()
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
            autoOff()
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
        this.finish()
        return true
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        if(item.itemId == R.id.qrCode){
            startActivity(Intent(this, QRCodeGenerator::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
