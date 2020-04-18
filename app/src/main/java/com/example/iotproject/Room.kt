package com.example.iotproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_room.*
import kotlin.math.roundToInt

class Room : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

//        var id = intent.getStringExtra("roomid")
        var id = 2
        val database = FirebaseDatabase.getInstance()
        val light = database.getReference("/Room/$id/light")
        val fan = database.getReference("/Room/$id/fan")
        var name = database.getReference("/Room/$id/name")

        var nameListener = name.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var name = p0.getValue(String::class.java) ?: return
                nameTxt.text = "Hi, $name"
            }
        })


        var lightListener = light.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(String::class.java) ?: ""
                if(post == ""){
                    lightText.text = "  Light: Reading ..."
                }else{
                    var p = (post.toFloat() / 255 * 100).roundToInt()
                    lightText.text = "  Light: $p %"
                }
            }
        })

        var fanListener = fan.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.getValue(String::class.java) ?: ""
                if(post != "")
                    fanText.text = "Fan: $post"
                else{
                    fanText.text = "Fan: Reading ..."
                }
            }
        })
        lightB.setOnClickListener {
//            light.removeEventListener(lightListener)
//            fan.removeEventListener(fanListener)
//            name.removeEventListener(nameListener)
            startActivity(Intent(this,light_setting::class.java).putExtra("RoomId", id.toString()))
        }

        fanB.setOnClickListener {
//            light.removeEventListener(lightListener)
//            fan.removeEventListener(fanListener)
//            name.removeEventListener(nameListener)
            startActivity(Intent(this,fan_setting::class.java).putExtra("RoomId", id.toString()))
        }
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
