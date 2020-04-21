package com.example.iotproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.android.synthetic.main.activity_qr_code_generator.*

class QRCodeGenerator : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_code_generator)

        val sharedPreferences = getSharedPreferences("phoneId", Context.MODE_PRIVATE)
        var phoneid = sharedPreferences.getString("phoneId", "")

        id_text.text = phoneid

        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(phoneid, BarcodeFormat.QR_CODE, 256, 256)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        qrCodeImage.setImageBitmap(bitmap)

        back_btn.setOnClickListener{
            this.finish()
        }
    }
}
