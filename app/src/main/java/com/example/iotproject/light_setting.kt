package com.example.iotproject

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.SeekBar
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_light_setting.*
import java.lang.Thread.sleep
import kotlin.math.log
import kotlin.math.roundToInt

class light_setting : AppCompatActivity() {

    private var auto = false
    private var lightIndex = 0
    private lateinit var lightListener:ValueEventListener
    private lateinit var lightAutoListener:ValueEventListener
    private lateinit var light:DatabaseReference
    private lateinit var lightAuto:DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = FirebaseDatabase.getInstance()
        val id = intent.getStringExtra("RoomId");
        light = database.getReference("/Room/$id/light")
        lightAuto = database.getReference("/Room/$id/lightAuto")
        var lightValue = 0
        setContentView(R.layout.activity_light_setting)
        val actionbar = supportActionBar
        actionbar!!.title = "Adjust Brightness"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        LightTitle.text = "The current light brightness"

        lightAutoListener = lightAuto.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(String::class.java) ?: return
                if(post=="1"){
                    autoOn()
                    lightValue = 0
                }else if(post=="0"){
                    autoOff()
                }else{
                    autoBtn.isEnabled = false
                }
            }
        })
        lightListener = light.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(String::class.java) ?: ""
                if(post ==""){
                    LightIntensityText.text = "Error"
                    adjust.isEnabled = false
                }else{
                    if(lightValue==0){
                        lightValue = post.toInt()
                        adjust.progress = lightValue
                    }
                    var l = (post.toFloat() / 255 * 100).roundToInt()
                    LightIntensityText.text = l.toString()
                }

            }
        })
        adjust.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var p = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                p = progress
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {
                autoOff()
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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        light.removeEventListener(lightListener)
        lightAuto.removeEventListener(lightAutoListener)
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
            startActivity(Intent(this, qrCodeGenerator::class.java))
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
