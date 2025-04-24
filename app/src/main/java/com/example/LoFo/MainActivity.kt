package com.example.LoFo

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.LoFo.ui.beranda.Beranda
import com.example.LoFo.ui.login.login
import com.example.LoFo.ui.register.Register
import com.example.LoFo.utils.SharedPrefHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val isLoggedIn = SharedPrefHelper.getBoolean(this, "IS_LOGGED_IN", false)

        if (isLoggedIn) {
            startActivity(Intent(this, Beranda::class.java))
            finish()
        } else {
            setContentView(R.layout.activity_main)
        }
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