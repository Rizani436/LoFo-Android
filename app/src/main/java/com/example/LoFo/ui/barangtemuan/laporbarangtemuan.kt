package com.example.LoFo.ui.barangtemuan

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.LoFo.MainActivity
import com.example.LoFo.R
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.data.model.barangtemuan.BarangTemuanResponse
import com.example.LoFo.utils.SharedPrefHelper
import okhttp3.MultipartBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import java.util.Calendar
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.*

class laporbarangtemuan : AppCompatActivity() {
    private lateinit var pilihGambarButton: Button
    private lateinit var namaFile: TextView
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporbarangtemuan)
        val user = SharedPrefHelper.getUser(this)
        val kategori = intent.getStringExtra("kategori")

        var namaBarang = findViewById<EditText>(R.id.namaBarang)
        var kategoriBarang = findViewById<Spinner>(R.id.kategoriBarang)
        val kategoriOptions = resources.getStringArray(R.array.kategori_barang)
        val adapterKetegori = ArrayAdapter(this, android.R.layout.simple_spinner_item, kategoriOptions)
        adapterKetegori.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        kategoriBarang.adapter = adapterKetegori
        val selectedKategori = kategoriBarang.selectedItem.toString()
        val tempatPenemuan = findViewById<EditText>(R.id.tempatPenemuan)
        var kotaKabupaten = findViewById<Spinner>(R.id.kotaKabupaten)
        val kotaKabupatenOptions = resources.getStringArray(R.array.kota_kabupaten)
        val adapterKotaKabupaten = ArrayAdapter(this, android.R.layout.simple_spinner_item, kotaKabupatenOptions)
        adapterKotaKabupaten.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        kotaKabupaten.adapter = adapterKotaKabupaten
        val selectedKotaKabupaten = kotaKabupaten.selectedItem.toString()
        var pertanyaan = findViewById<EditText>(R.id.pertanyaan)
        var nomorHandphone = findViewById<EditText>(R.id.nomorHandphone)
        var buttonRegister : Button = findViewById<Button>(R.id.daftar)
        val tanggalPenemuan = findViewById<EditText>(R.id.tanggalPenemuan)
        pilihGambarButton = findViewById(R.id.pilihGambarButton)
        namaFile = findViewById(R.id.namaFile)

        pilihGambarButton.setOnClickListener {
            // Intent untuk memilih gambar
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        tanggalPenemuan.setOnClickListener {
            val kalender = Calendar.getInstance()
            val tahun = kalender.get(Calendar.YEAR)
            val bulan = kalender.get(Calendar.MONTH)
            val hari = kalender.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val tanggal = String.format("%02d/%02d/%04d", month + 1, dayOfMonth, year)
                tanggalPenemuan.setText(tanggal)
            }, tahun, bulan, hari)

            datePicker.show()
        }
        var buttonBack : ImageView = findViewById<ImageView>(R.id.back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        buttonRegister.setOnClickListener {
            var cek = false;
            if (namaBarang.text.toString().isEmpty()){
                namaBarang.error = "Nama barang tidak boleh kosong"
                cek = true;
            }
            if ( tempatPenemuan.text.toString().isEmpty()){
                tempatPenemuan.error = "Tempat Penemuan tidak boleh kosong"
                cek = true;
            }

            if (pertanyaan.text.toString().isEmpty()){
                pertanyaan.error = "Pertanyaan tentang barang tidak boleh kosong"
                cek = true;
            }
            if (nomorHandphone.text.toString().isEmpty()) {
                nomorHandphone.error = "Nomor handphone tidak boleh kosong"
                cek = true;
            }
            if (kategoriBarang.selectedItemPosition == 0) {
                cek = true;
            }
            if (kotaKabupaten.selectedItemPosition == 0) {
                cek = true;
            }
            if(tanggalPenemuan.text.toString().isEmpty()){
                cek = true;
            }
            if (cek) {
                Toast.makeText(this, "Isi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else if(selectedImageUri == null){
                Toast.makeText(this, "Pilih gambar terlebih dahulu", Toast.LENGTH_SHORT).show()
            }


            val latitude = -6.200000f
            val longitude = 12.343f

            val fileUri = selectedImageUri!!
            val inputStream = contentResolver.openInputStream(fileUri)
            val fileBytes = inputStream!!.readBytes()
            val fileName = getFileName(fileUri)

            val requestFile = fileBytes.toRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", fileName, requestFile)

            val userIdBody = (user?.username ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
            val namaBarangBody = toRequestBody(namaBarang.text.toString())
            val kategoriBarangBody = toRequestBody(kategoriBarang.selectedItem.toString())
            val tempatTemuanBody = toRequestBody(tempatPenemuan.text.toString())
            val kotaKabupatenBody = toRequestBody(kotaKabupaten.selectedItem.toString())
            val tanggalTemuanBody = toRequestBody(tanggalPenemuan.text.toString())
            val informasiDetailBody = toRequestBody(pertanyaan.text.toString())
            val noHPBody = toRequestBody(nomorHandphone.text.toString())
            val latitudeBody = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val longitudeBody = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val statusBody = toRequestBody("Menunggu")

            ApiClient.apiService.createBarangTemuan(
                filePart,
                userIdBody,
                namaBarangBody,
                kategoriBarangBody,
                tempatTemuanBody,
                kotaKabupatenBody,
                tanggalTemuanBody,
                informasiDetailBody,
                noHPBody,
                latitudeBody,
                longitudeBody,
                statusBody
            ).enqueue(object : Callback<BarangTemuanResponse> {
                override fun onResponse(call: Call<BarangTemuanResponse>, response: Response<BarangTemuanResponse>) {
                    if (response.isSuccessful) {
                        val barangTemuanResponse = response.body()
                        Toast.makeText(this@laporbarangtemuan, "Laporan berhasil dikirim!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@laporbarangtemuan, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            val jsonObj = JSONObject(it)
                            val errorMessage = jsonObj.getString("message")
                            Toast.makeText(this@laporbarangtemuan, "Laporan gagal: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<BarangTemuanResponse>, t: Throwable) {
                    Toast.makeText(this@laporbarangtemuan, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            // Mendapatkan URI file yang dipilih
            selectedImageUri = data?.data
            selectedImageUri?.let { uri ->
                // Mendapatkan nama file dari URI
                val fileName = getFileName(uri)
                // Tampilkan nama file di TextView
                namaFile.text = fileName
            }
        }
    }

    // Fungsi untuk mendapatkan nama file dari URI
    private fun getFileName(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val fileName = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return fileName ?: "Unknown"
    }

    fun toRequestBody(value: String): RequestBody =
        value.toRequestBody("text/plain".toMediaTypeOrNull())


}