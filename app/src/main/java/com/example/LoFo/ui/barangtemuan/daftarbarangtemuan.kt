package com.example.LoFo.ui.barangtemuan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.LoFo.MainActivity
import com.example.LoFo.R
import com.example.LoFo.adapter.ListDaftarBarangTemuanAdapter
import com.example.LoFo.data.model.barangtemuan.BarangTemuan

class daftarbarangtemuan : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListDaftarBarangTemuanAdapter
    private val listBarang = ArrayList<BarangTemuan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftarbarangtemuan)

        val kategori = intent.getStringExtra("kategori")
        val listBarang = intent.getParcelableArrayListExtra<BarangTemuan>("dataBarang") ?: arrayListOf()

        var buttonBack : ImageView = findViewById<ImageView>(R.id.back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        recyclerView = findViewById(R.id.recyclerViewBarang)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Menambahkan lambdas untuk parameter onDeleteClick, onEditClick, dan onStatusUpdateClick
        adapter = ListDaftarBarangTemuanAdapter(
            listBarang,
            onKlaimClick = { barangTemuan ->
                // Tindakan yang akan dilakukan saat tombol hapus diklik
                Toast.makeText(this, "Klaim ${barangTemuan.namaBarang}", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()
    }
}
