package com.example.iotproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_room.*

class room : AppCompatActivity() {

    var tempInfo:String = ""
    var humInfo:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        val database = FirebaseDatabase.getInstance()
        val light = database.getReference("/Room/light")
        val fan = database.getReference("/Room/fan")
        val lightAuto = database.getReference("/Room/lightAuto")
        val fanAuto = database.getReference("/Room/fanAuto")
        val temp = database.getReference("/Room/temp")
        val hum = database.getReference("/Room/hum")
        val intensity = database.getReference("/Room/intensity")
        var l = Library()

//        light.setValue("0")
//        fan.setValue("0")
//        lightAuto.setValue("0")
//        fanAuto.setValue("0")
//        hum.setValue("0")
//        temp.setValue("32")
//        intensity.setValue("532")

        lightB.setOnClickListener {
            startActivity(Intent(this,light_setting::class.java))

        }
        fanB.setOnClickListener {
            startActivity(Intent(this,fan_setting::class.java))

        }
        light.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(String::class.java) ?: return
                lightText.text = "Light: ${l.lightPowerCon(post.toInt())}"

            }
        })
        fan.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(String::class.java) ?: return
                fanText.text = "  Fan: $post"

            }
        })
        temp.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(String::class.java) ?: return
                tempInfo = post
                updateTemp()
            }
        })
        hum.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }
            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(String::class.java) ?: return
                humInfo = post
                updateTemp()
            }
        })


    }
    fun updateTemp(){
        tempText.text = "Temp:$tempInfo\u2103 Hum:$humInfo"
    }
}
