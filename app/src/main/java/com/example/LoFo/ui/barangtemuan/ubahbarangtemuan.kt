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
import com.example.LoFo.utils.SharedPrefHelper
import kotlinx.coroutines.launch

class ubahbarangtemuan : AppCompatActivity() {
    val REQUEST_IMAGE_CAPTURE = 1
    private val PICK_IMAGE_REQUEST = 2
    private val REQUEST_IMAGE_CROP = 3
    private val CAMERA_PERMISSION_CODE = 100
    private var selectedImageUri: Uri? = null
    lateinit var barang : BarangTemuan
    lateinit var imgItemPhoto : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubahbarangtemuan)

        imgItemPhoto = findViewById(R.id.img_item_photo)
        val namaBarang = findViewById<EditText>(R.id.namaBarang)
        val kategoriBarang = findViewById<Spinner>(R.id.kategoriBarang)
        val tempatTemuan = findViewById<EditText>(R.id.tempatTemuan)
        val kotaKabupaten = findViewById<Spinner>(R.id.kotaKabupaten)
        val informasiDetail = findViewById<EditText>(R.id.informasiDetail)
        val nomorHandphone = findViewById<EditText>(R.id.noHP)
        val tanggalTemuan = findViewById<EditText>(R.id.tanggalTemuan)
        val buttonUbah = findViewById<Button>(R.id.buttonUbah)
        val buttonBack = findViewById<ImageView>(R.id.back)

        imgItemPhoto.setOnClickListener {
            val options = arrayOf("Ambil Foto", "Pilih dari Galeri")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Pilih Sumber Gambar")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        checkCameraPermissionAndOpenCamera()
                    }
                    1 -> {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "image/*"
                        startActivityForResult(intent, PICK_IMAGE_REQUEST)
                    }
                }
            }
            builder.show()
        }
        tanggalTemuan.setOnClickListener {
            val kalender = Calendar.getInstance()
            val tahun = kalender.get(Calendar.YEAR)
            val bulan = kalender.get(Calendar.MONTH)
            val hari = kalender.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val tanggal = String.format("%02d/%02d/%04d", month + 1, dayOfMonth, year)
                tanggalTemuan.setText(tanggal)
            }, tahun, bulan, hari)

            datePicker.show()
        }

        val kategoriOptions = resources.getStringArray(R.array.kategori_barang)
        val adapterKategori = ArrayAdapter(this, android.R.layout.simple_spinner_item, kategoriOptions)
        adapterKategori.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        kategoriBarang.adapter = adapterKategori

        val kotaOptions = resources.getStringArray(R.array.kota_kabupaten)
        val adapterKota = ArrayAdapter(this, android.R.layout.simple_spinner_item, kotaOptions)
        adapterKota.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        kotaKabupaten.adapter = adapterKota

        barang = intent.getParcelableExtra<BarangTemuan>("barang")!!
        barang?.let {
            namaBarang.setText(it.namaBarang)
            tempatTemuan.setText(it.tempatTemuan)
            informasiDetail.setText(it.informasiDetail)
            nomorHandphone.setText(it.noHP)
            tanggalTemuan.setText(it.tanggalTemuan)

            val kategoriIndex = kategoriOptions.indexOf(it.kategoriBarang)
            if (kategoriIndex != -1) kategoriBarang.setSelection(kategoriIndex)

            val kotaIndex = kotaOptions.indexOf(it.kotaKabupaten)
            if (kotaIndex != -1) kotaKabupaten.setSelection(kotaIndex)

            Glide.with(imgItemPhoto.context)
                .load(barang.pictUrl)
                .placeholder(R.drawable.placeholder)
                .into(imgItemPhoto)
        }

        buttonUbah.setOnClickListener {
            val map = mapOf(
                "namaBarang" to namaBarang.text.toString(),
                "kategoriBarang" to kategoriBarang.selectedItem.toString(),
                "tempatTemuan" to tempatTemuan.text.toString(),
                "kotaKabupaten" to kotaKabupaten.selectedItem.toString(),
                "tanggalTemuan" to tanggalTemuan.text.toString(),
                "informasiDetail" to informasiDetail.text.toString(),
                "noHP" to nomorHandphone.text.toString(),
                "status" to "Menunggu".toString(),
            )
            ApiClient.apiService.updateBarangTemuan(barang!!.idBarangTemuan, map)
                .enqueue(object : Callback<BarangTemuan> {
                    override fun onResponse(call: Call<BarangTemuan>, response: Response<BarangTemuan>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@ubahbarangtemuan, "Berhasil Memperbarui barang", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@ubahbarangtemuan, "Gagal memperbarui barang", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<BarangTemuan>, t: Throwable) {
                        Toast.makeText(this@ubahbarangtemuan, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })

        }

        buttonBack.setOnClickListener {
            val user = SharedPrefHelper.getUser(this)
            val username = user?.username
            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.getMyAllBarangTemuan(username.toString())
                    val intent = Intent(this@ubahbarangtemuan, riwayatbarangtemuan::class.java)
                    intent.putParcelableArrayListExtra("dataBarang", ArrayList(response))
                    startActivity(intent)

                } catch (e: Exception) {
                    Toast.makeText(this@ubahbarangtemuan, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    val selectedImageUri = data?.data
                    selectedImageUri?.let {
                        startCrop(it)
                    }
                }

                REQUEST_IMAGE_CROP -> {
                    val resultUri = UCrop.getOutput(data!!)
                    if (resultUri != null) {
                        val imageView = findViewById<ImageView>(R.id.img_item_photo)
                        Glide.with(this).load(resultUri).into(imageView)

                        uploadProfileImage(resultUri)
                    } else {
                        Toast.makeText(this, "Gagal crop gambar", Toast.LENGTH_SHORT).show()
                    }
                }

                REQUEST_IMAGE_CAPTURE -> {
                    selectedImageUri?.let {
                        startCrop(it)
                    }
                }
            }
        }
    }

    fun startCrop(uri: Uri) {
        val destinationUri = Uri.fromFile(File(cacheDir, "cropped_image.jpg"))

        UCrop.of(uri, destinationUri)
            .withAspectRatio(4f, 3f)
            .withMaxResultSize(1000, 750)
            .start(this, REQUEST_IMAGE_CROP)
    }
    fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val photoFile = createImageFile()
        if (photoFile != null) {
            selectedImageUri = FileProvider.getUriForFile(
                this,
                "${packageName}.provider",
                photoFile
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, selectedImageUri)
            startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
        } else {
            Toast.makeText(this, "Gagal membuat file gambar", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show()
            }
        }
    }



    fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (checkSelfPermission(android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            openCamera()
        }
    }

    private fun getFileName(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        val fileName = columnIndex?.let { cursor.getString(it) }
        cursor?.close()
        return fileName ?: "Unknown"
    }

    private fun uploadProfileImage(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)
        val fileBytes = inputStream!!.readBytes()
        val fileName = getFileName(uri) ?: "uploaded_image.jpg"


        val requestFile = fileBytes.toRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", fileName, requestFile)


        ApiClient.apiService.updateGambarBarangTemuan(barang!!.idBarangTemuan, filePart)
            .enqueue(object : Callback<BarangTemuan> {
                override fun onResponse(call: Call<BarangTemuan>, response: Response<BarangTemuan>) {
                    if (response.isSuccessful) {
                        response.body()?.let { updatedGambarBarang ->
                            Toast.makeText(this@ubahbarangtemuan, "Gambar Barang berhasil diubah", Toast.LENGTH_SHORT).show()
                            Glide.with(imgItemPhoto.context)
                                .load(updatedGambarBarang.pictUrl)
                                .into(imgItemPhoto)
                        }
                    } else {
                        val msg = JSONObject(response.errorBody()?.string() ?: "{}")
                            .optString("message", "Terjadi kesalahan.")
                        Toast.makeText(this@ubahbarangtemuan, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BarangTemuan>, t: Throwable) {
                    Toast.makeText(this@ubahbarangtemuan, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
    
}




