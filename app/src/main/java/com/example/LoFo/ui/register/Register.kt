package com.example.LoFo.ui.register

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.MainActivity
import com.example.LoFo.R
import com.example.LoFo.data.model.register.RegisterRequest
import com.example.LoFo.data.model.register.RegisterResponse
import com.example.LoFo.ui.login.login
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        var username = findViewById<EditText>(R.id.username)
        val email = findViewById<EditText>(R.id.email)
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
        var konfirmasiPassword = findViewById<EditText>(R.id.konfirmasiPassword)
        val showKonfirmasiPasswordIcon = findViewById<ImageView>(R.id.showKonfirmasiPasswordIcon)
        var isKonfirmasiPasswordVisible = false
        showKonfirmasiPasswordIcon.setOnClickListener {
            isKonfirmasiPasswordVisible = !isKonfirmasiPasswordVisible

            if (isKonfirmasiPasswordVisible) {
                konfirmasiPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                showKonfirmasiPasswordIcon.setImageResource(R.drawable.eye_on)
            } else {
                konfirmasiPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                showKonfirmasiPasswordIcon.setImageResource(R.drawable.eye_off)
            }

            konfirmasiPassword.setSelection(konfirmasiPassword.text.length)
        }
        var namaLengkap = findViewById<EditText>(R.id.namaLengkap)
        var jenisKelamin = findViewById<Spinner>(R.id.jenisKelamin)
        val genderOptions = resources.getStringArray(R.array.jenis_kelamin)
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, genderOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        jenisKelamin.adapter = adapter
        val selectedGender = jenisKelamin.selectedItem.toString()
        var alamat = findViewById<EditText>(R.id.alamat)
        var nomorHandphone = findViewById<EditText>(R.id.nomorHandphone)
        var buttonRegister : Button = findViewById<Button>(R.id.register)
        var buttonBack : ImageView = findViewById<ImageView>(R.id.back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        buttonRegister.setOnClickListener {
            var cek = false;
            var emaiCek = false;
            if (username.text.toString().isEmpty()){
                username.error = "Username tidak boleh kosong"
                cek = true;
            }
            if ( email.text.toString().isEmpty()){
                email.error = "Email tidak boleh kosong"
                cek = true;
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email.text.toString()).matches()) {
                emaiCek = true;
            }

            if (password.text.toString().isEmpty()){
                password.error = "Password tidak boleh kosong"
                cek = true;
            }
            if (konfirmasiPassword.text.toString().isEmpty()) {
                konfirmasiPassword.error = "Konfirmasi password tidak boleh kosong"
                cek = true;
            }
            if (namaLengkap.text.toString().isEmpty()) {
                namaLengkap.error = "Nama lengkap tidak boleh kosong"
                cek = true;
            }

            if (alamat.text.toString().isEmpty()) {
                alamat.error = "Alamat tidak boleh kosong"
                cek = true;
            }
            if (nomorHandphone.text.toString().isEmpty()) {
                nomorHandphone.error = "Nomor handphone tidak boleh kosong"
                cek = true;
            }
            if (jenisKelamin.selectedItemPosition == 0) {
                cek = true;
            }
            if (cek) {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (emaiCek) {
                Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (password.text.toString() != konfirmasiPassword.text.toString()) {
                Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val registerRequest = RegisterRequest(
                username = username.text.toString(),
                email = email.text.toString(),
                password = password.text.toString(),
                namaLengkap = namaLengkap.text.toString(),
                jenisKelamin = jenisKelamin.selectedItem.toString(),
                alamat = alamat.text.toString(),
                noHP = "62"+nomorHandphone.text.toString(),
                pictUrl = "profile/kosong.jpg"
            )

            ApiClient.apiService.registerUser(registerRequest).enqueue(object :
                Callback<RegisterResponse> {
                override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(this@Register, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@Register, login::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            val jsonObj = JSONObject(it)
                            val errorMessage = jsonObj.getString("message")
                            Toast.makeText(this@Register, "Registrasi gagal.  $errorMessage", Toast.LENGTH_SHORT).show()
                        }

                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    Toast.makeText(this@Register, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }

}