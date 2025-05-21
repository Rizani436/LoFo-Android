package com.example.LoFo.ui.baranghilang

import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.LoFo.MainActivity
import com.example.LoFo.R
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.data.model.baranghilang.BarangHilangResponse

import com.example.LoFo.utils.SharedPrefHelper
import com.yalantis.ucrop.UCrop
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class laporbaranghilang : AppCompatActivity() {
    private lateinit var pilihGambarButton: Button
    private lateinit var namaFile: TextView
    val REQUEST_IMAGE_CAPTURE = 1
    private val PICK_IMAGE_REQUEST = 2
    private val REQUEST_IMAGE_CROP = 3
    private val CAMERA_PERMISSION_CODE = 100
    private var selectedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_laporbaranghilang)

        val user = SharedPrefHelper.getUser(this)
        var namaBarang = findViewById<EditText>(R.id.namaBarang)
        var kategoriBarang = findViewById<Spinner>(R.id.kategoriBarang)
        val kategoriOptions = resources.getStringArray(R.array.kategori_barang)
        val adapterKetegori = ArrayAdapter(this, android.R.layout.simple_spinner_item, kategoriOptions)
        adapterKetegori.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        kategoriBarang.adapter = adapterKetegori
        val tempatKehilangan = findViewById<EditText>(R.id.tempatKehilangan)
        var kotaKabupaten = findViewById<Spinner>(R.id.kotaKabupaten)
        val kotaKabupatenOptions = resources.getStringArray(R.array.kota_kabupaten)
        val adapterKotaKabupaten = ArrayAdapter(this, android.R.layout.simple_spinner_item, kotaKabupatenOptions)
        adapterKotaKabupaten.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        kotaKabupaten.adapter = adapterKotaKabupaten
        var informasiDetail = findViewById<EditText>(R.id.informasiDetail)
        var nomorHandphone = findViewById<EditText>(R.id.nomorHandphone)
        val tanggalKehilangan = findViewById<EditText>(R.id.tanggalKehilangan)
        var buttonRegister : Button = findViewById<Button>(R.id.daftar)

        var buttonBack : ImageView = findViewById<ImageView>(R.id.back)
        pilihGambarButton = findViewById(R.id.pilihGambarButton)
        namaFile = findViewById(R.id.namaFile)
        pilihGambarButton.setOnClickListener {
            val options = arrayOf("Ambil Foto", "Pilih dari Galeri")
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Pilih Sumber Gambar")
            builder.setItems(options) { _, which ->
                when (which) {
                    0 -> checkCameraPermissionAndOpenCamera()
                    1 -> {
                        val intent = Intent(Intent.ACTION_GET_CONTENT)
                        intent.type = "image/*"
                        startActivityForResult(intent, PICK_IMAGE_REQUEST)
                    }
                }
            }
            builder.show()
        }
        tanggalKehilangan.setOnClickListener {
            val kalender = Calendar.getInstance()
            val tahun = kalender.get(Calendar.YEAR)
            val bulan = kalender.get(Calendar.MONTH)
            val hari = kalender.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, year, month, dayOfMonth ->
                val tanggal = String.format("%02d/%02d/%04d", month + 1, dayOfMonth, year)
                tanggalKehilangan.setText(tanggal)
            }, tahun, bulan, hari)

            datePicker.show()
        }
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
            if ( tempatKehilangan.text.toString().isEmpty()){
                tempatKehilangan.error = "Tempat kehilangan tidak boleh kosong"
                cek = true;
            }

            if (informasiDetail.text.toString().isEmpty()){
                informasiDetail.error = "Informasi detail tidak boleh kosong"
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
            if(tanggalKehilangan.text.toString().isEmpty()){
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
            val fileName = getFileName(fileUri) ?: "uploaded_image.jpg"

            val requestFile = fileBytes.toRequestBody("image/*".toMediaTypeOrNull())
            val filePart = MultipartBody.Part.createFormData("file", fileName, requestFile)

            val userIdBody = (user?.username ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
            val namaBarangBody = toRequestBody(namaBarang.text.toString())
            val kategoriBarangBody = toRequestBody(kategoriBarang.selectedItem.toString())
            val tempatHilangBody = toRequestBody(tempatKehilangan.text.toString())
            val kotaKabupatenBody = toRequestBody(kotaKabupaten.selectedItem.toString())
            val tanggalHilangBody = toRequestBody(tanggalKehilangan.text.toString())
            val informasiDetailBody = toRequestBody(informasiDetail.text.toString())
            val noHPBody = toRequestBody("62"+nomorHandphone.text.toString())
            val latitudeBody = latitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val longitudeBody = longitude.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val statusBody = toRequestBody("Menunggu")

            ApiClient.apiService.createBarangHilang(
                filePart,
                userIdBody,
                namaBarangBody,
                kategoriBarangBody,
                tempatHilangBody,
                kotaKabupatenBody,
                tanggalHilangBody,
                informasiDetailBody,
                noHPBody,
                latitudeBody,
                longitudeBody,
                statusBody
            ).enqueue(object : Callback<BarangHilangResponse> {
                override fun onResponse(call: Call<BarangHilangResponse>, response: Response<BarangHilangResponse>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@laporbaranghilang, "Laporan berhasil dikirim!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@laporbaranghilang, MainActivity::class.java)
                        startActivity(intent)
                    }
                    else{
                        val errorBody = response.errorBody()?.string()
                        errorBody?.let {
                            val jsonObj = JSONObject(it)
                            val errorMessage = jsonObj.getString("message")
                            Toast.makeText(this@laporbaranghilang, "Laporan gagal: $errorMessage", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onFailure(call: Call<BarangHilangResponse>, t: Throwable) {
                    Toast.makeText(this@laporbaranghilang, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    val uri = data?.data
                    uri?.let { startCrop(it) }
                }

                REQUEST_IMAGE_CROP -> {
                    val resultUri = UCrop.getOutput(data!!)
                    if (resultUri != null) {
                        selectedImageUri = resultUri

                        val fileName = resultUri.lastPathSegment ?: "gambar_dipilih.jpg"
                        namaFile.text = fileName
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
        val fileName = "cropped_image_${System.currentTimeMillis()}.jpg"
        val destinationUri = Uri.fromFile(File(cacheDir, fileName))

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

    fun toRequestBody(value: String): RequestBody =
        value.toRequestBody("text/plain".toMediaTypeOrNull())
}