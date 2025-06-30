package com.example.LoFo.ui.barangtemuan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.LoFo.R
import com.example.LoFo.data.api.ApiClient

import androidx.lifecycle.lifecycleScope
import com.example.LoFo.data.model.baranghilang.BarangHilang
import com.example.LoFo.data.model.barangtemuan.BarangTemuan
import com.example.LoFo.data.model.jawabanpertanyaan.daftarlaporanklaimresponse
import com.example.LoFo.data.model.notifikasi.Notifikasi
import com.example.LoFo.ui.baranghilang.ubahbaranghilang
import com.example.LoFo.ui.barangtemuan.riwayatbarangtemuan
import com.example.LoFo.ui.beranda.Beranda
import com.example.LoFo.utils.SharedPrefHelper
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.toString

class laporanklaim : AppCompatActivity() {
    lateinit var jawaban : daftarlaporanklaimresponse
    lateinit var imgItemPhoto : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporanklaim)

        imgItemPhoto = findViewById(R.id.img_item_photo)
        val username = findViewById<TextView>(R.id.username)
        val namaBarang = findViewById<TextView>(R.id.namaBarang)
        val pertanyaan = findViewById<TextView>(R.id.informasiDetail)
        val jawabannya = findViewById<TextView>(R.id.jawaban)
        val buttonTolak = findViewById<Button>(R.id.buttonTolak)
        val buttonTerima = findViewById<Button>(R.id.buttonTerima)
        val buttonBack = findViewById<ImageView>(R.id.back)
        var idBarangTemuan = ""
        var penjawab = ""
        jawaban = intent.getParcelableExtra<daftarlaporanklaimresponse>("jawaban")!!
        jawaban?.let {
            pertanyaan.setText(it.pertanyaan)
            jawabannya.setText(it.jawaban)
            idBarangTemuan = it.idBarangTemuan
            penjawab = it.penjawab
            username.setText(it.penjawab)
        }
        var pictUrl = ""
        lifecycleScope.launch {
            try {
                val barangTemuan = ApiClient.apiService.getByIdBarangTemuan(idBarangTemuan)
                findViewById<TextView>(R.id.namaBarang).text = barangTemuan?.namaBarang
                pictUrl = barangTemuan?.pictUrl.toString()
                Glide.with(this@laporanklaim)
                    .load(barangTemuan?.pictUrl)
                    .placeholder(R.drawable.profile_picture)
                    .error(R.drawable.profile_picture)
                    .into(imgItemPhoto)
            } catch (e: Exception) {
                Toast.makeText(this@laporanklaim, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
            }
        }
        
        buttonBack.setOnClickListener {
            val user = SharedPrefHelper.getUser(this)
            val username = user?.username
            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.getMyAllJawabanPertanyaan(username.toString(), idBarangTemuan)
                    val intent = Intent(this@laporanklaim, daftarlaporanklaim::class.java)
                    intent.putParcelableArrayListExtra("dataJawaban", ArrayList(response))
                    startActivity(intent)

                } catch (e: Exception) {
                    Toast.makeText(this@laporanklaim, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }

        buttonTolak.setOnClickListener {
            val user = SharedPrefHelper.getUser(this)
            val username = user?.username
            val map = mapOf(
                "idBarangTemuan" to idBarangTemuan,
                "idUser" to penjawab,
                "pesanNotifikasi" to "Telah menolak klaim barang Anda".toString(),
            )
            ApiClient.apiService.createNotifikasi(map)
                .enqueue(object : Callback<Notifikasi> {
                    override fun onResponse(call: Call<Notifikasi>, response: Response<Notifikasi>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@laporanklaim, "Berhasil menolak klaim barang", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@laporanklaim, "Gagal menolak klaim barang", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Notifikasi>, t: Throwable) {
                        Toast.makeText(this@laporanklaim, "Error: ${t.message}", Toast.LENGTH_SHORT).show()

                    }
                })
            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.getMyAllJawabanPertanyaan(username.toString(), idBarangTemuan)
                    val intent = Intent(this@laporanklaim, daftarlaporanklaim::class.java)
                    intent.putParcelableArrayListExtra("dataJawaban", ArrayList(response))
                    startActivity(intent)

                } catch (e: Exception) {
                    Toast.makeText(this@laporanklaim, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@laporanklaim, Beranda::class.java)
                    startActivity(intent)
                }
            }
        }

        buttonTerima.setOnClickListener {
            val user = SharedPrefHelper.getUser(this)
            val username = user?.username
            val map = mapOf(
                "idBarangTemuan" to idBarangTemuan,
                "idUser" to penjawab,
                "pesanNotifikasi" to "Telah menerima klaim barang Anda".toString(),
            )
            ApiClient.apiService.createNotifikasi(map)
                .enqueue(object : Callback<Notifikasi> {
                    override fun onResponse(call: Call<Notifikasi>, response: Response<Notifikasi>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@laporanklaim, "Berhasil Menerima klaim barang", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@laporanklaim, "Gagal Menerima klaim barang", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Notifikasi>, t: Throwable) {
                        Toast.makeText(this@laporanklaim, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        lifecycleScope.launch {
                            try {
                                val response = ApiClient.apiService.getMyAllJawabanPertanyaan(username.toString(), idBarangTemuan)
                                val intent = Intent(this@laporanklaim, daftarlaporanklaim::class.java)
                                intent.putParcelableArrayListExtra("dataJawaban", ArrayList(response))
                                startActivity(intent)

                            } catch (e: Exception) {
                                Toast.makeText(this@laporanklaim, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })
            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.getMyAllJawabanPertanyaan(username.toString(), idBarangTemuan)
                    val intent = Intent(this@laporanklaim, daftarlaporanklaim::class.java)
                    intent.putParcelableArrayListExtra("dataJawaban", ArrayList(response))
                    startActivity(intent)

                } catch (e: Exception) {
                    Toast.makeText(this@laporanklaim, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this@laporanklaim, Beranda::class.java)
                    startActivity(intent)
                }
            }
        }
    }

}




