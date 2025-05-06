package com.example.LoFo.ui.barangtemuan

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.LoFo.R

class daftarbarangtemuan : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftarbarangtemuan)

        val kategori = intent.getStringExtra("kategori")

        // Gunakan kategori untuk menampilkan data sesuai, contoh:
        Toast.makeText(this, "Kategori: $kategori", Toast.LENGTH_SHORT).show()

        // Misalnya: Ambil data dari API berdasarkan kategori
        // fetchBarangHilang(kategori)
    }
}