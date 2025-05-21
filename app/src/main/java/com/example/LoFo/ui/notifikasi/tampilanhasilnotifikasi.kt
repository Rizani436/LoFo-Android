package com.example.LoFo.ui.notifikasi

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
import com.yalantis.ucrop.UCrop
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
import com.example.LoFo.data.model.notifikasi.Notif
import com.example.LoFo.ui.beranda.Beranda
import com.example.LoFo.utils.SharedPrefHelper
import kotlinx.coroutines.launch

class tampilanhasilnotifikasi : AppCompatActivity() {
    lateinit var barang : Notif
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tampilanhasilnotifikasi)

        val imgItemPhoto = findViewById<ImageView>(R.id.img_item_photo)
        val username = findViewById<TextView>(R.id.username)
        val namaBarang = findViewById<TextView>(R.id.namaBarang)
        val kategoriBarang = findViewById<TextView>(R.id.kategori)
        val tempatTemuan = findViewById<TextView>(R.id.tempatTemuan)
        val kotaKabupaten = findViewById<TextView>(R.id.kotaKabupaten)
        val tanggalTemuan = findViewById<TextView>(R.id.tanggalTemuan)
        val status = findViewById<TextView>(R.id.status)
        val buttonChat = findViewById<LinearLayout>(R.id.buttonChat)
        val buttonBack = findViewById<ImageView>(R.id.back)
        val buttonPesan = findViewById<LinearLayout>(R.id.buttonSMS)
        val buttonTelpon = findViewById<LinearLayout>(R.id.buttonTelpon)

        var noHP = ""

        barang = intent.getParcelableExtra<Notif>("barangTemuan")!!
        barang?.let {
            username.setText(it.uploader)
            namaBarang.setText(it.namaBarang)
            tempatTemuan.setText(it.tempatTemuan)
            kategoriBarang.setText(it.kategoriBarang)
            kotaKabupaten.setText(it.kotaKabupaten)
            tanggalTemuan.setText(it.tanggalTemuan)
            kategoriBarang.setText(it.kategoriBarang)
            status.setText(it.status)
            noHP = it.noHP

            Glide.with(imgItemPhoto.context)
                .load(barang.pictUrl)
                .placeholder(R.drawable.placeholder)
                .into(imgItemPhoto)
        }

        buttonChat.setOnClickListener {

            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("https://wa.me/$noHP")
                }
                startActivity(intent)
            } catch (e: Exception) {
                Toast.makeText(this, "WhatsApp tidak terpasang.", Toast.LENGTH_SHORT).show()
            }
        }

        buttonPesan.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse("sms:$noHP")
                }
                startActivity(intent)
        }
            catch (e: Exception) {
                Toast.makeText(this, "WhatsApp tidak terpasang.", Toast.LENGTH_SHORT).show()
            }
        }
        buttonTelpon.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:$noHP")
            }
            startActivity(intent)
        }

        buttonBack.setOnClickListener {
            val user = SharedPrefHelper.getUser(this)
            val username = user?.username
            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.getMyAllNotifikasi(username.toString())
                    val intent = Intent(this@tampilanhasilnotifikasi, notifikasi::class.java)
                    intent.putParcelableArrayListExtra("dataNotifikasi", ArrayList(response))
                    startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(this@tampilanhasilnotifikasi, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}




