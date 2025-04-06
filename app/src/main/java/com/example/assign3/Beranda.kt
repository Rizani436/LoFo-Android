package com.example.assign3

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Beranda : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_beranda)
        val user = intent.getParcelableExtra<User>("user_data")
        if (user != null) {
            // Gunakan datanya
            val text = findViewById<TextView>(R.id.text)
            text.text = "Halo, ${user.username} "
        }else{
            val text = findViewById<TextView>(R.id.text)
            text.text = "error "
        }
    }
}