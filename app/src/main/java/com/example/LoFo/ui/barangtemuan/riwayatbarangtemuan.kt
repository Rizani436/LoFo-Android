package com.example.LoFo.ui.barangtemuan

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.LoFo.MainActivity
import com.example.LoFo.R
import com.example.LoFo.adapter.ListRiwayatBarangTemuanAdapter
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.data.model.barangtemuan.BarangTemuan
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class riwayatbarangtemuan : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListRiwayatBarangTemuanAdapter
    private val listBarang = ArrayList<BarangTemuan>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayatbarangtemuan)

        val kategori = intent.getStringExtra("kategori")
        listBarang.clear()
        listBarang.addAll(intent.getParcelableArrayListExtra<BarangTemuan>("dataBarang") ?: arrayListOf())

        val buttonBack : ImageView = findViewById<ImageView>(R.id.back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        recyclerView = findViewById(R.id.recyclerViewBarang)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ListRiwayatBarangTemuanAdapter(
            listBarang,
            onDeleteClick = { barangTemuan ->
                val index = listBarang.indexOfFirst { it.idBarangTemuan == barangTemuan.idBarangTemuan }
                if (index != -1) {
                    listBarang.removeAt(index)
                    adapter.notifyItemRemoved(index)
                    Toast.makeText(this, "Berhasil menghapus ${barangTemuan.namaBarang}", Toast.LENGTH_SHORT).show()
                }
            },
            onEditClick = { barangTemuan ->
                val intent = Intent(this@riwayatbarangtemuan, ubahbarangtemuan::class.java)
                intent.putExtra("barang", barangTemuan)
                startActivity(intent)
            },
            onLaporanClick = { barangTemuan ->

            },
            onSelesaiClick = { barang ->
                if (barang.status == "Diterima") {
                    AlertDialog.Builder(this)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah Anda yakin ingin menyelesaikan laporan ini?")
                        .setPositiveButton("Ya") { _, _ ->
                            val updateMap = mapOf("status" to "Selesai")

                            ApiClient.apiService.updateBarangTemuan(barang.idBarangTemuan, updateMap)
                                .enqueue(object : Callback<BarangTemuan> {
                                    override fun onResponse(call: Call<BarangTemuan>, response: Response<BarangTemuan>) {
                                        if (response.isSuccessful) {
                                            barang.status = "Selesai"
                                            adapter.notifyDataSetChanged()
                                            Toast.makeText(this@riwayatbarangtemuan, "Status berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this@riwayatbarangtemuan, "Gagal memperbarui status", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<BarangTemuan>, t: Throwable) {
                                        Toast.makeText(this@riwayatbarangtemuan, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                                    }
                                })
                        }
                        .setNegativeButton("Batal", null)
                        .show()
                } else {
                    Toast.makeText(this, "Status hanya bisa diubah jika sekarang 'Diterima'", Toast.LENGTH_SHORT).show()
                }
            }

        )

        recyclerView.adapter = adapter

        adapter.notifyDataSetChanged()
    }
}
