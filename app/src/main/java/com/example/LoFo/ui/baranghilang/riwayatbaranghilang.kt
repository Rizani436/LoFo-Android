package com.example.LoFo.ui.baranghilang

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.LoFo.MainActivity
import com.example.LoFo.R
import com.example.LoFo.adapter.ListRiwayatBarangHilangAdapter
import com.example.LoFo.data.model.baranghilang.BarangHilang

class riwayatbaranghilang : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListRiwayatBarangHilangAdapter
    private val listBarang = ArrayList<BarangHilang>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayatbaranghilang)

        val kategori = intent.getStringExtra("kategori")
        val listBarang = intent.getParcelableArrayListExtra<BarangHilang>("dataBarang") ?: arrayListOf()

        var buttonBack : ImageView = findViewById<ImageView>(R.id.back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        recyclerView = findViewById(R.id.recyclerViewBarang)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Menambahkan lambdas untuk parameter onDeleteClick, onEditClick, dan onStatusUpdateClick
        adapter = ListRiwayatBarangHilangAdapter(
            listBarang,
            onDeleteClick = { barangHilang ->
                // Tindakan yang akan dilakukan saat tombol hapus diklik
                Toast.makeText(this, "Hapus ${barangHilang.namaBarang}", Toast.LENGTH_SHORT).show()
            },
            onEditClick = { barangHilang ->
                // Tindakan yang akan dilakukan saat tombol ubah diklik
                Toast.makeText(this, "Ubah ${barangHilang.namaBarang}", Toast.LENGTH_SHORT).show()
            },
            onSelesaiClick = { barangHilang ->
                // Tindakan yang akan dilakukan saat tombol perbarui status diklik
                Toast.makeText(this, "Perbarui status ${barangHilang.namaBarang} ke Selesai", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()
    }
}
