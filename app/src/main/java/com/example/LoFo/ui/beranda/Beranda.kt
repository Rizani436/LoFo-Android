package com.example.LoFo.ui.beranda

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.LoFo.MainActivity
import com.example.LoFo.R
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.data.model.logout.LogoutRequest
import com.example.LoFo.data.model.logout.LogoutResponse
import com.example.LoFo.utils.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Beranda : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_beranda)
        val user = SharedPrefHelper.getUser(this)

        if (user != null) {
            // Gunakan datanya
            val text = findViewById<TextView>(R.id.text)
            text.text = "Halo, ${user.username} "
        }else{
            val text = findViewById<TextView>(R.id.text)
            text.text = "error "
        }
        var buttonLogout : Button = findViewById<Button>(R.id.buttonLogout)
        buttonLogout.setOnClickListener {
            val token = SharedPrefHelper.getToken(this)
            val request = LogoutRequest(token.toString())
            ApiClient.apiService.logoutUser(request).enqueue(object : Callback<LogoutResponse> {
                override fun onResponse(call: Call<LogoutResponse>, response: Response<LogoutResponse>) {
                    if (response.isSuccessful) {
                        SharedPrefHelper.clear(this@Beranda)
                        Toast.makeText(this@Beranda, "Logout Berhasil", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@Beranda, MainActivity::class.java))
                        finish()
                    }else{
                        Toast.makeText(this@Beranda, "Logout Gagal", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                    Toast.makeText(this@Beranda, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}