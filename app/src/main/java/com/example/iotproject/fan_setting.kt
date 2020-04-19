package com.example.iotproject

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CompoundButton
import androidx.appcompat.app.AppCompatActivity
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
        setContentView(R.layout.activity_fan_setting)

        val actionbar = supportActionBar
        actionbar!!.title = "Adjust Fan Speed"
        actionbar.setDisplayHomeAsUpEnabled(true)

        val roomId = intent.getStringExtra("RoomId")
        val database = FirebaseDatabase.getInstance()
        val fan = database.getReference("/Room/$roomId/fan")
        val fanAuto = database.getReference("/Room/$roomId/fanAuto")

        fanAuto.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.value.toString()
                if (post == "1") autoSwitch.isChecked = true
                else if (post == "0") autoSwitch.isChecked = false
            }
        })

        fan.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(p0: DataSnapshot) {
                val post = p0.value.toString()
                if (p0.value.toString().isNotEmpty()) {
                    fanValue = post.toInt()
                    when {
                        fanValue <= 0 -> {
                            decrease.setBackgroundColor(Color.GRAY)
                            decrease.isEnabled = false
                        }
                        fanValue >= 5 -> {
                            increase.setBackgroundColor(Color.GRAY)
                            increase.isEnabled = false
                        }
                        else -> {
                            increase.setBackgroundColor(Color.parseColor("#333333"))
                            increase.isEnabled = true
                            decrease.setBackgroundColor(Color.parseColor("#333333"))
                            decrease.isEnabled = true
                        }
                    }
                    updateText()
                }

            }
        })

        decrease.setOnClickListener {
            autoSwitch.isChecked = false
            fanAuto.setValue("0")
            if (fanValue > 0) {
                fanValue--
                fan.setValue(fanValue.toString())
                updateText()
            }
        }

        increase.setOnClickListener {
            autoSwitch.isChecked = false
            fanAuto.setValue("0")
            if (fanValue < 5) {
                fanValue++
                fan.setValue(fanValue.toString())
                updateText()
            }
        }

        autoSwitch.setOnCheckedChangeListener { compoundButton: CompoundButton, b: Boolean ->
            auto = b
            if (b) fanAuto.setValue("1")
            else fanAuto.setValue("0")
        }
    }

    fun updateText() {
        FanSpeed.text = fanValue.toString()
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
