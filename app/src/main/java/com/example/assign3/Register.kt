package com.example.assign3

import android.os.Bundle
import android.widget.*
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.text.InputType
import android.util.Patterns
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

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
            val user = User(username.text.toString(), email.text.toString(), password.text.toString(), namaLengkap.text.toString(), selectedGender, alamat.text.toString(), nomorHandphone.text.toString())
            val intent = Intent(this, login::class.java)
            intent.putExtra("user_data", user)
            startActivity(intent)
        }
    }

}