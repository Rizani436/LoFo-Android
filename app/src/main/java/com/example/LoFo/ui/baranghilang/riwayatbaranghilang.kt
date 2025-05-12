package com.example.LoFo.ui.baranghilang

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
import com.example.LoFo.adapter.ListRiwayatBarangHilangAdapter
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.data.model.baranghilang.BarangHilang
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class riwayatbaranghilang : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListRiwayatBarangHilangAdapter
    private val listBarang = ArrayList<BarangHilang>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_riwayatbaranghilang)

        listBarang.clear()
        listBarang.addAll(intent.getParcelableArrayListExtra<BarangHilang>("dataBarang") ?: arrayListOf())

        val buttonBack : ImageView = findViewById(R.id.back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        recyclerView = findViewById(R.id.recyclerViewBarang)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ListRiwayatBarangHilangAdapter(
            listBarang,
            onDeleteClick = { barangHilang ->
                val index = listBarang.indexOfFirst { it.idBarangHilang == barangHilang.idBarangHilang }
                if (index != -1) {
                    listBarang.removeAt(index)
                    adapter.notifyItemRemoved(index)
                    Toast.makeText(this, "Berhasil menghapus ${barangHilang.namaBarang}", Toast.LENGTH_SHORT).show()
                }
            },
            onEditClick = { barangHilang ->
                val intent = Intent(this@riwayatbaranghilang, ubahbaranghilang::class.java)
                intent.putExtra("barang", barangHilang)
                startActivity(intent)
            },
            onSelesaiClick = { barang ->
                if (barang.status == "Diterima") {
                    AlertDialog.Builder(this)
                        .setTitle("Konfirmasi")
                        .setMessage("Apakah Anda yakin ingin menyelesaikan laporan ini?")
                        .setPositiveButton("Ya") { _, _ ->
                            // API call PATCH untuk update status
                            val updateMap = mapOf("status" to "Selesai")

                            ApiClient.apiService.updateBarangHilang(barang.idBarangHilang, updateMap)
                                .enqueue(object : Callback<BarangHilang> {
                                    override fun onResponse(call: Call<BarangHilang>, response: Response<BarangHilang>) {
                                        if (response.isSuccessful) {
                                            // Update data di list dan refresh UI
                                            barang.status = "Selesai"
                                            adapter.notifyDataSetChanged()
                                            Toast.makeText(this@riwayatbaranghilang, "Status berhasil diperbarui", Toast.LENGTH_SHORT).show()
                                        } else {
                                            Toast.makeText(this@riwayatbaranghilang, "Gagal memperbarui status", Toast.LENGTH_SHORT).show()
                                        }
                                    }

                                    override fun onFailure(call: Call<BarangHilang>, t: Throwable) {
                                        Toast.makeText(this@riwayatbaranghilang, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
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
    }

}
