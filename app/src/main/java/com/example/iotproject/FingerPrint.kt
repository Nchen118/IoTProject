package com.example.iotproject

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_finger_print.*
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey


class FingerPrint : AppCompatActivity() {

    private lateinit var fingerprintManager: FingerprintManager
    private lateinit var keyguardManager: KeyguardManager
    private lateinit var keyStore: KeyStore
    private lateinit var keyGenerator: KeyGenerator
    private val KEY_NAME = "my_key"
    private lateinit var cipher: Cipher
    private lateinit var cryptoObject: FingerprintManager.CryptoObject

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_finger_print)

        loading.visibility = View.VISIBLE
        username.visibility = View.GONE
        fingerprint_iv.visibility = View.GONE
        fingerprint_tv.visibility = View.GONE

        val roomid = intent.getStringExtra("RoomId")

        FirebaseFirestore.getInstance().collection("users").document(roomid).get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    val sharedPreferences = getSharedPreferences("phoneId", Context.MODE_PRIVATE)
                    var phoneid = sharedPreferences.getString("phoneId", "")
                    var phoneFound: Boolean = false
                    for (mac in result.get("macAddress") as List<String>) {
                        Log.d("mac", "$mac = $phoneid")
                        if (phoneid == mac) {
                            loading.visibility = View.GONE
                            username.visibility = View.VISIBLE
                            fingerprint_iv.visibility = View.VISIBLE
                            fingerprint_tv.visibility = View.VISIBLE
                            phoneFound = true
                            username.text = result.get("name").toString()
                            if (checkLockScreen()) {
                                generateKey()
                                if (initCipher()) {
                                    cipher.let { cryptoObject = FingerprintManager.CryptoObject(it)}
                                    val helper = FingerprintHelper(this, roomid)

                                    if (fingerprintManager != null && cryptoObject != null) {
                                        helper.startAuth(fingerprintManager, cryptoObject)
                                    }
                                }
                            }
                        }
                    }
                    if (!phoneFound) {
                        Toast.makeText(this, "Unauthorized user!\nYou are not allow to unlock the door.", Toast.LENGTH_LONG).show()
                        this.finish()
                    }
                } else {
                    Toast.makeText(this, "No such room!", Toast.LENGTH_LONG).show()
                    this.finish()
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Firestore", exception)
            }
    }

    private fun checkLockScreen(): Boolean {
        keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
        if (!keyguardManager.isKeyguardSecure) {
            Toast.makeText(
                this,
                "Lock screen security not enabled",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.USE_FINGERPRINT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(
                this,
                "Permission not enabled (Fingerprint)",
                Toast.LENGTH_LONG
            ).show()
            return false
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {
            Toast.makeText(
                this,
                "No fingerprint registered, please register",
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        return true
    }

    private fun generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            keyGenerator = KeyGenerator.getInstance(
                KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore"
            )
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(
                "Failed to get KeyGenerator instance", e
            )
        } catch (e: NoSuchProviderException) {
            throw RuntimeException("Failed to get KeyGenerator instance", e)
        }

        try {
            keyStore.load(null)
            keyGenerator.init(
                KeyGenParameterSpec.Builder(
                    KEY_NAME,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_PKCS7
                    )
                    .build()
            )
            keyGenerator.generateKey()
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        } catch (e: InvalidAlgorithmParameterException) {
            throw RuntimeException(e)
        } catch (e: CertificateException) {
            throw RuntimeException(e)
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
    }

    private fun initCipher(): Boolean {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to get Cipher", e)
        } catch (e: NoSuchPaddingException) {
            throw RuntimeException("Failed to get Cipher", e)
        }

        try {
            keyStore.load(null)
            val key = keyStore.getKey(KEY_NAME, null) as SecretKey
            cipher.init(Cipher.ENCRYPT_MODE, key)
            return true
        } catch (e: KeyPermanentlyInvalidatedException) {
            return false
        } catch (e: KeyStoreException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: CertificateException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: UnrecoverableKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: IOException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException("Failed to init Cipher", e)
        } catch (e: InvalidKeyException) {
            throw RuntimeException("Failed to init Cipher", e)
        }
    }
}
