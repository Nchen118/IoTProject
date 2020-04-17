package com.example.iotproject

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.CancellationSignal
import android.os.Vibrator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat

@SuppressLint("ByteOrderMark")
class FingerprintHelper(private val appContext: Context, private val roomid: String) : FingerprintManager.AuthenticationCallback() {
    private lateinit var cancellationSignal: CancellationSignal

    fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject) {
        cancellationSignal = CancellationSignal()

        if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.USE_FINGERPRINT) !=
            PackageManager.PERMISSION_GRANTED) {
            return
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }

    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        Toast.makeText(appContext, "Authentication error\n$errString", Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        Toast.makeText(appContext, "Authentication help\n$helpString", Toast.LENGTH_LONG).show()
    }

    override fun onAuthenticationFailed() {
        Toast.makeText(appContext,"Authentication failed.", Toast.LENGTH_LONG).show()
        val vibrator = appContext.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(200)
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        Toast.makeText(appContext,"Authentication succeeded.", Toast.LENGTH_LONG).show()
        appContext.startActivity(Intent(appContext, Room::class.java).putExtra("roomid", roomid))
    }
}

class fingerPrintUI : AppCompatActivity() {

}