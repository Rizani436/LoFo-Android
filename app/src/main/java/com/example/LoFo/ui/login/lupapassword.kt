package com.example.LoFo.ui.login

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.InputType
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.LoFo.MainActivity
import com.example.LoFo.R
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.data.model.login.LoginRequest
import com.example.LoFo.data.model.login.LoginResponse
import com.example.LoFo.ui.notifikasi.notifikasi
import com.example.LoFo.utils.SharedPrefHelper
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.*

class lupapassword : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_lupapassword)
        var email = findViewById<EditText>(R.id.email)
        var kodeVerifikasi = findViewById<EditText>(R.id.kodeVerifikasi)
        val mintakode = findViewById<TextView>(R.id.mintaKode)
        var buttonNext : Button = findViewById<Button>(R.id.next)

        var buttonBack : ImageView = findViewById<ImageView>(R.id.back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }
        mintakode.setOnClickListener {
            if (email.text.toString().isEmpty()) {
                email.error = "Email tidak boleh kosong"
                Toast.makeText(this, "Isi email", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Disable tombol dan mulai countdown
            mintakode.isEnabled = false

            val countdown = object : CountDownTimer(60000, 1000) { // 60 detik, update tiap 1 detik
                override fun onTick(millisUntilFinished: Long) {
                    val detik = millisUntilFinished / 1000
                    mintakode.text = "Tunggu ($detik)"
                }

                override fun onFinish() {
                    mintakode.isEnabled = true
                    mintakode.text = "Kirim Ulang"
                }
            }
            countdown.start()

            val body = mapOf("email" to email.text.toString())

            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.getCodeEmail(body)
                    if (response.isSuccessful) {
                        val json = response.body()
                        val message = json?.get("message")?.asString
                        Toast.makeText(this@lupapassword, "Berhasil: $message", Toast.LENGTH_SHORT).show()
                    } else {
                        val error = response.errorBody()?.string()
                        val message = JSONObject(error ?: "{}").optString("message", "Terjadi kesalahan")
                        Toast.makeText(this@lupapassword, "Gagal: $message", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@lupapassword, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }



        buttonNext.setOnClickListener {
            var cek = false;
            if (kodeVerifikasi.text.toString().isEmpty()){
                kodeVerifikasi.error = "Kode verifikasi tidak boleh kosong"
                cek = true;
            }
            if (cek) {
                Toast.makeText(this, "Isi Kode verifikasi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val body = mapOf("email" to email.text.toString(), "resetCode" to kodeVerifikasi.text.toString())

            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.confirmCode(body)
                    if (response.isSuccessful) {
                        Toast.makeText(this@lupapassword, "Kode berhasil diverifikasi", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@lupapassword, gantipassword::class.java)
                        intent.putExtra("email", email.text.toString())
                        startActivity(intent)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            val jsonObj = JSONObject(it)
                            val errorMessage = jsonObj.getString("message")
                            Toast.makeText(this@lupapassword, "$errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                catch (e: Exception) {
                    Toast.makeText(this@lupapassword, "Kode gagal diverifikasi", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

}