package com.example.assign3

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        var username = findViewById<EditText>(R.id.username)
        var password = findViewById<EditText>(R.id.password)
        val showPasswordIcon = findViewById<ImageView>(R.id.showPasswordIcon)
        var isPasswordVisible = false
        showPasswordIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showPasswordIcon.setImageResource(R.drawable.eye_on)
            } else {
                password.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showPasswordIcon.setImageResource(R.drawable.eye_off)
            }

            password.setSelection(password.text.length)
        }
        var buttonBack : ImageView = findViewById<ImageView>(R.id.back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        var buttonLogin : Button = findViewById<Button>(R.id.login)
        buttonLogin.setOnClickListener {
            var cek = false;
            if (username.text.toString().isEmpty()){
                username.error = "Username tidak boleh kosong"
                cek = true;
            }
            if (password.text.toString().isEmpty()){
                password.error = "Password tidak boleh kosong"
                cek = true;
            }
            if (cek) {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val user = intent.getParcelableExtra<User>("user_data")
            if (user != null) {
                if (user.username == username.text.toString() && user.password == password.text.toString()) {
                    val intent = Intent(this, Beranda::class.java)
                    intent.putExtra("user_data", user)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "Username atau password salah", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }else{
                Toast.makeText(this, "Data kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }
        }
    }
}