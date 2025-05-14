package com.example.LoFo.ui.barangtemuan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.LoFo.MainActivity
import com.example.LoFo.R
import com.example.LoFo.ui.barangtemuan.klaimbarangtemuan
import com.example.LoFo.adapter.ListDaftarLaporanKlaimAdapter
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.data.model.jawabanpertanyaan.daftarlaporanklaimresponse
import com.example.LoFo.ui.beranda.Beranda
import com.example.LoFo.utils.SharedPrefHelper
import kotlinx.coroutines.launch

class daftarlaporanklaim : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListDaftarLaporanKlaimAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_daftarlaporanklaim)

        val user = SharedPrefHelper.getUser(this)
        val username = user?.username
        val listJawaban = intent.getParcelableArrayListExtra<daftarlaporanklaimresponse>("dataJawaban") ?: arrayListOf()
        val buttonBack = findViewById<ImageView>(R.id.back)
        buttonBack.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.getMyAllBarangTemuan(username.toString())
                    val intent = Intent(this@daftarlaporanklaim, riwayatbarangtemuan::class.java)
                    intent.putParcelableArrayListExtra("dataBarang", ArrayList(response))
                    startActivity(intent)

                } catch (e: Exception) {
                    Toast.makeText(this@daftarlaporanklaim, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }
        recyclerView = findViewById(R.id.recyclerViewBarang)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ListDaftarLaporanKlaimAdapter(
            listJawaban
        ) { jawaban ->
            val intent = Intent(this, laporanklaim::class.java)
            intent.putExtra("jawaban", jawaban)
            startActivity(intent)
        }


        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()
    }
}
