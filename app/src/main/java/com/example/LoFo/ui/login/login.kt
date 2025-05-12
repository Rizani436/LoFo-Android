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
import com.example.LoFo.ui.beranda.Beranda
import com.example.LoFo.MainActivity
import com.example.LoFo.R
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.data.model.login.LoginRequest
import com.example.LoFo.data.model.login.LoginResponse
import com.example.LoFo.utils.SharedPrefHelper
import org.json.JSONObject
import retrofit2.*

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
            val userInput = username.text.toString()
            val passInput = password.text.toString()
            val request = LoginRequest(userInput, passInput)
            ApiClient.apiService.loginUser(request).enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.isSuccessful) {
                        val loginResponse = response.body()
                        val user = loginResponse?.User
                        val token = loginResponse?.token
                        SharedPrefHelper.saveString(this@login, "TOKEN", token ?: "")
                        SharedPrefHelper.saveBoolean(this@login, "IS_LOGGED_IN", true)
                        SharedPrefHelper.saveUser(this@login, user)
                        SharedPrefHelper.saveToken(this@login, token ?: "")

                        Toast.makeText(this@login, "Login berhasil!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@login, Beranda::class.java))
                    } else {
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            val jsonObj = JSONObject(it)
                            val errorMessage = jsonObj.getString("message")
                            Toast.makeText(this@login, "Login gagal: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Toast.makeText(this@login, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}