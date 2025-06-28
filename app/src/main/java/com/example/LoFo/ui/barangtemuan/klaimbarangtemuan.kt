package com.example.LoFo.ui.barangtemuan

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.LoFo.R
import com.example.LoFo.data.api.ApiClient
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull

import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.lifecycle.lifecycleScope
import com.example.LoFo.data.model.barangtemuan.BarangTemuan
import com.example.LoFo.data.model.jawabanpertanyaan.JawabanPertanyaanResponse
import com.example.LoFo.ui.barangtemuan.laporanklaim
import com.example.LoFo.ui.beranda.Beranda
import com.example.LoFo.utils.SharedPrefHelper
import kotlinx.coroutines.launch

class klaimbarangtemuan : AppCompatActivity() {
    lateinit var barang : BarangTemuan
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_klaimbarangtemuan)
        val user = SharedPrefHelper.getUser(this)
        val username = user?.username

        val uploader: TextView = findViewById(R.id.username)
        val imgPhotoProfile: ImageView = findViewById(R.id.image_profile)
        val imgItemPhoto = findViewById<ImageView>(R.id.img_item_photo)
        val pertanyaan = findViewById<TextView>(R.id.pertanyaan)
        val jawaban = findViewById<EditText>(R.id.jawaban)
        val buttonKlaim = findViewById<Button>(R.id.buttonKlaim)
        val buttonBack = findViewById<ImageView>(R.id.back)
        var idBarangTemuan = ""
        var penanya = ""
        barang = intent.getParcelableExtra<BarangTemuan>("barang")!!
        barang.let {
            pertanyaan.setText(it.informasiDetail)
            uploader.text = it.uploader
            penanya = it.uploader


            idBarangTemuan = it.idBarangTemuan
            Glide.with(this@klaimbarangtemuan)
                .load(it?.pictUrl)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(imgItemPhoto)
        }

        buttonKlaim.setOnClickListener {
            val map = mapOf(
                "idBarangTemuan" to idBarangTemuan.toString(),
                "penanya" to penanya.toString(),
                "pertanyaan" to pertanyaan.text.toString(),
                "penjawab" to username.toString(),
                "jawaban" to jawaban.text.toString(),
            )
            if (jawaban.text.toString().isEmpty()) {
                Toast.makeText(this, "Jawaban tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            ApiClient.apiService.createJawabanPertanyaan(map)
                .enqueue(object : Callback<JawabanPertanyaanResponse> {
                    override fun onResponse(call: Call<JawabanPertanyaanResponse>, response: Response<JawabanPertanyaanResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@klaimbarangtemuan, "Berhasil Klaim barang, Tunggu Konfirmasi", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@klaimbarangtemuan, "Gagal Klaim barang", Toast.LENGTH_SHORT).show()
                        }
                        lifecycleScope.launch {
                            try {
                                val response = ApiClient.apiService.getOtherAllBarangTemuan(username.toString(), "All".toString())
                                val intent = Intent(this@klaimbarangtemuan, daftarbarangtemuan::class.java)
                                intent.putParcelableArrayListExtra("dataBarang", ArrayList(response))
                                startActivity(intent)

                            } catch (e: Exception) {
                                Toast.makeText(this@klaimbarangtemuan, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    override fun onFailure(call: Call<JawabanPertanyaanResponse>, t: Throwable) {
                        Toast.makeText(this@klaimbarangtemuan, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        lifecycleScope.launch {
                            try {
                                val response = ApiClient.apiService.getOtherAllBarangTemuan(username.toString(), "All".toString())
                                val intent = Intent(this@klaimbarangtemuan, daftarbarangtemuan::class.java)
                                intent.putParcelableArrayListExtra("dataBarang", ArrayList(response))
                                startActivity(intent)

                            } catch (e: Exception) {
                                Toast.makeText(this@klaimbarangtemuan, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                })

        }

        buttonBack.setOnClickListener {
            val user = SharedPrefHelper.getUser(this)
            val username = user?.username
            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.getOtherAllBarangTemuan(username.toString(), "All".toString())
                    val intent = Intent(this@klaimbarangtemuan, daftarbarangtemuan::class.java)
                    intent.putParcelableArrayListExtra("dataBarang", ArrayList(response))
                    startActivity(intent)

                } catch (e: Exception) {
                    Toast.makeText(this@klaimbarangtemuan, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

}




