package com.example.assign3

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        var buttonLogin : Button = findViewById<Button>(R.id.login)
        buttonLogin.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }
        var buttonRegister : Button = findViewById<Button>(R.id.register)
        buttonRegister.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

    }
}