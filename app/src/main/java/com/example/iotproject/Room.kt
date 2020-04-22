package com.example.iotproject

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_room.*
import java.util.*
import kotlin.math.roundToInt

class Room : AppCompatActivity() {
    var gotTemp = false
    var gotHum = false
    var gotIntensity = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        loadingLayout.visibility = View.VISIBLE
        infoLayout.visibility = View.GONE
        lightLayout.visibility = View.GONE
        fanLayout.visibility = View.GONE

        var id = intent.getStringExtra("roomid")
        val database = FirebaseDatabase.getInstance()
        val light = database.getReference("/Room/$id/light")
        val fan = database.getReference("/Room/$id/fan")
        var name = database.getReference("/Room/$id/name")
        var intensity = database.getReference("/Room/$id/lightIntensity")
        var temperature = database.getReference("/Room/$id/temp")
        var humidity = database.getReference("/Room/$id/hum")
        var myData = database.getReference("/Room/$id")

        temperature.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(data: DataSnapshot) {
                if(data.value == ""){
                    gotTemp = false
                }else{
                    gotTemp = true
                    tempText.text = "Temperature: ${data.value.toString()}"
                }

            }
        })
        humidity.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(data: DataSnapshot) {
                if(data.value == ""){
                    gotHum = false
                }else{
                    gotHum = true
                    humidText.text = "Humidity: ${data.value.toString()}"
                }
            }
        })
        intensity.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(data: DataSnapshot) {
                if(data.value == ""){
                    gotIntensity = false
                }else{
                    gotIntensity = true
                    lightIntenText.text = "Light Intensity: ${data.value.toString()}"
                }
            }
        })
        Handler(Looper.getMainLooper()).post {
            val now = Calendar.getInstance() //LocalDateTime.now()
            val dbRef =
                "/PI_01_2020" + ("0" + (now.time.month + 1)).takeLast(2) + ("0" + (now.time.date)).takeLast(
                    2
                ) + "/" + ("0" + (now.time.hours)).takeLast(2)
            FirebaseDatabase.getInstance("https://bait2123-202003-02.firebaseio.com")
                .getReference(dbRef).limitToLast(1)
                .addValueEventListener(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        p0.children.forEach { id ->
                            id.children.forEach { data ->
                                if (data.key == "tempe" && !gotTemp) tempText.text = "Temperature: ${data.value.toString()}"
                                if (data.key == "humid" && !gotHum) humidText.text = "Humidity: ${data.value.toString()}"
                                if (data.key == "light" && !gotIntensity) lightIntenText.text = "Light Intensity: ${data.value.toString()}"
                                loadingLayout.visibility = View.GONE
                                infoLayout.visibility = View.VISIBLE
                                lightLayout.visibility = View.VISIBLE
                                fanLayout.visibility = View.VISIBLE
                            }
                        }
                    }
                })
        }

        name.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                nameText.text = "Hi, ${p0.value.toString()}"
            }
        })

        light.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.value.toString()
                if (post.isNotEmpty()) lightText.text = "Light: ${(post.toFloat() / 255 * 100).roundToInt()} %"
                else lightText.text = "Light: Reading..."
            }
        })

        fan.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.value.toString()
                if (post.isNotEmpty()) fanText.text = "Fan: $post"
                else fanText.text = "Fan: Reading ..."
            }
        })

        lightB.setOnClickListener {
            startActivity(Intent(this, light_setting::class.java).putExtra("RoomId", id.toString()))
        }

        fanB.setOnClickListener {
            startActivity(Intent(this, fan_setting::class.java).putExtra("RoomId", id.toString()))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle presses on the action bar menu items
        if (item.itemId == R.id.qrCode) {
            startActivity(Intent(this, QRCodeGenerator::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
