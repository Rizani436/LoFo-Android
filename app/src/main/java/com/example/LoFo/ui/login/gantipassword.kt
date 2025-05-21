package com.example.LoFo.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.LoFo.MainActivity
import com.example.LoFo.R
import com.example.LoFo.data.api.ApiClient
import kotlinx.coroutines.launch
import org.json.JSONObject

class gantipassword : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_gantipassword)
        val email = intent.getStringExtra("email")
        val passwordBaru = findViewById<EditText>(R.id.passwordBaru)
        val passwordBaruLagi = findViewById<EditText>(R.id.passwordBaruLagi)

        val showPasswordBaruIcon = findViewById<ImageView>(R.id.showPasswordBaruIcon)
        val showPasswordBaruLagiIcon = findViewById<ImageView>(R.id.showPasswordBaruLagiIcon)

        var isPasswordBaruVisible = false
        var isPasswordBaruLagiVisible = false

        showPasswordBaruIcon.setOnClickListener {
            isPasswordBaruVisible = !isPasswordBaruVisible
            togglePasswordVisibility(passwordBaru, showPasswordBaruIcon, isPasswordBaruVisible)
        }

        showPasswordBaruLagiIcon.setOnClickListener {
            isPasswordBaruLagiVisible = !isPasswordBaruLagiVisible
            togglePasswordVisibility(passwordBaruLagi, showPasswordBaruLagiIcon, isPasswordBaruLagiVisible)
        }

        var buttonBack : ImageView = findViewById<ImageView>(R.id.back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, lupapassword::class.java)
            startActivity(intent)
        }

        val konfirmasiPassword = findViewById<Button>(R.id.konfirmasiPassword)
        konfirmasiPassword.setOnClickListener {
            val newPassword = passwordBaru.text.toString().trim()
            val confirmPassword = passwordBaruLagi.text.toString().trim()

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (newPassword != confirmPassword) {
                Toast.makeText(this, "Password baru tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val body = mapOf(
                "email" to email.toString(),
                "newPassword" to passwordBaru.text.toString(),
                "confirmPassword" to passwordBaruLagi.text.toString())
            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.resetPassword(body)
                    if (response.isSuccessful) {
                        Toast.makeText(this@gantipassword, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@gantipassword, login::class.java)
                        startActivity(intent)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            val jsonObj = JSONObject(it)
                            val errorMessage = jsonObj.getString("message")
                            Toast.makeText(this@gantipassword, "$errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                catch (e: Exception) {
                    Toast.makeText(this@gantipassword, "Password gagal diubah", Toast.LENGTH_SHORT).show()
                }
            }


        }

    }

    private fun togglePasswordVisibility(editText: EditText, icon: ImageView, visible: Boolean) {
        editText.inputType = if (visible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        icon.setImageResource(if (visible) R.drawable.eye_on else R.drawable.eye_off)
        editText.setSelection(editText.text.length)
    }
}