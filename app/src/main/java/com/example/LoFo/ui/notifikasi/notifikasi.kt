package com.example.LoFo.ui.notifikasi

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
import com.example.LoFo.adapter.ListNotifikasiAdapter
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.data.model.baranghilang.BarangHilang
import com.example.LoFo.data.model.barangtemuan.BarangTemuan
import com.example.LoFo.data.model.jawabanpertanyaan.daftarlaporanklaimresponse
import com.example.LoFo.data.model.notifikasi.Notifikasi
import com.example.LoFo.ui.baranghilang.daftarbaranghilang
import com.example.LoFo.ui.baranghilang.ubahbaranghilang
import com.example.LoFo.ui.barangtemuan.daftarlaporanklaim
import com.example.LoFo.ui.barangtemuan.riwayatbarangtemuan
import com.example.LoFo.ui.barangtemuan.ubahbarangtemuan
import com.example.LoFo.ui.beranda.Beranda
import com.example.LoFo.utils.SharedPrefHelper
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class notifikasi : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListNotifikasiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifikasi)

        val user = SharedPrefHelper.getUser(this)
        val username = user?.username
        val listNotifikasi = intent.getParcelableArrayListExtra<Notifikasi>("dataNotifikasi") ?: arrayListOf()
        val buttonBack = findViewById<ImageView>(R.id.back)
        buttonBack.setOnClickListener {
            val intent = Intent(this@notifikasi, Beranda::class.java)
            startActivity(intent)
        }
        recyclerView = findViewById(R.id.recyclerViewBarang)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ListNotifikasiAdapter(
            listNotifikasi,
            onDeleteClick = { notifikasiItem ->
                lifecycleScope.launch {
                    try {
                        val response = ApiClient.apiService.deleteNotifikasi(notifikasiItem.notification_id)
                        if (response.isSuccessful) {
                            val position = listNotifikasi.indexOf(notifikasiItem)
                            if (position != -1) {
                                listNotifikasi.removeAt(position)
                                adapter.notifyItemRemoved(position)
                            }

                            Toast.makeText(this@notifikasi, "Notifikasi dihapus", Toast.LENGTH_SHORT).show()
                        }else{
                            Toast.makeText(this@notifikasi, "Gagal menghapus notifikasi", Toast.LENGTH_SHORT).show()
                        }


                    } catch (e: Exception) {
                        Toast.makeText(this@notifikasi, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onTampilkanClick = { Notifikasi ->
                val map = mapOf(
                    "read" to "sudahDibaca"
                )
                ApiClient.apiService.updateNotifikasi(Notifikasi.notification_id, map)
                    .enqueue(object : Callback<BarangTemuan> {
                        override fun onResponse(call: Call<BarangTemuan>, response: Response<BarangTemuan>) {
                            if (response.isSuccessful) {
                                Toast.makeText(this@notifikasi, "Berhasil Memperbarui barang", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@notifikasi, "Gagal memperbarui barang", Toast.LENGTH_SHORT).show()
                            }
                            val intent = Intent(this@notifikasi, ubahbarangtemuan::class.java)
                            intent.putExtra("barangTemuan", response.body())
                            startActivity(intent)
                        }
                        override fun onFailure(call: Call<BarangTemuan>, t: Throwable) {
                            Toast.makeText(this@notifikasi, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })

            },

        )


        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()
    }
}
