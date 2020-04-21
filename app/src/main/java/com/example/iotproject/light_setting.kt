package com.example.iotproject

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_light_setting.*
import kotlin.math.roundToInt

class light_setting : AppCompatActivity() {

    private var auto = false
    private lateinit var lightListener: ValueEventListener
    private lateinit var lightAutoListener: ValueEventListener
    private lateinit var light: DatabaseReference
    private lateinit var lightAuto: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_light_setting)

        val database = FirebaseDatabase.getInstance()
        val id = intent.getStringExtra("RoomId")
        light = database.getReference("/Room/$id/light")
        lightAuto = database.getReference("/Room/$id/lightAuto")

        val actionbar = supportActionBar
        actionbar!!.title = "Adjust Brightness"
        actionbar.setDisplayHomeAsUpEnabled(true)

        lightAuto.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                autoSwitch.isChecked = p0.value.toString() == "1"
            }
        })

        light.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.value.toString()
                if (post.isEmpty()) {
                    LevelText.text = "Error"
                    adjust.isEnabled = false
                } else {
                    adjust.progress = post.toFloat().roundToInt()
                    LightLevel.text = (post.toFloat() / 255 * 100).roundToInt().toString() + " %"
                }

            }
        })

        adjust.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            var p = 0
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                p = progress
                LevelText.text = "Level : ${p}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                auto = false
                autoSwitch.isChecked = false
                lightAuto.setValue("0")
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (p in 0..255) light.setValue(p.toString())
            }

        })

        autoSwitch.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            auto = b
            if (b) lightAuto.setValue("1")
            else lightAuto.setValue("0")
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
        if (item.itemId == R.id.qrCode) {
            startActivity(Intent(this, QRCodeGenerator::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
