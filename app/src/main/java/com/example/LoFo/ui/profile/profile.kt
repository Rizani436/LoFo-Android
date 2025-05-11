package com.example.LoFo.ui.profile

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.example.LoFo.MainActivity
import com.example.LoFo.R
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.data.api.ApiClient.apiService
import com.example.LoFo.data.model.login.LoginResponse
import com.example.LoFo.data.model.logout.LogoutRequest
import com.example.LoFo.data.model.logout.LogoutResponse
import com.example.LoFo.data.model.user.User
import com.example.LoFo.ui.baranghilang.*
import com.example.LoFo.ui.barangtemuan.*
import com.example.LoFo.ui.beranda.Beranda
import com.example.LoFo.ui.login.login
import com.example.LoFo.utils.SharedPrefHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import retrofit2.*
import android.util.Log
import com.example.LoFo.ui.login.login

import kotlin.collections.contains

class profile : AppCompatActivity() {
    private lateinit var pilihGambarButton: RelativeLayout
    private lateinit var namaFile: TextView
    val REQUEST_IMAGE_CAPTURE = 1
    private val PICK_IMAGE_REQUEST = 2
    private val CAMERA_PERMISSION_CODE = 100
    private var selectedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val user = SharedPrefHelper.getUser(this)



        val username = findViewById<TextView>(R.id.username)
        username.text = user?.username
        val email = findViewById<TextView>(R.id.email)
        email.text = user?.email
        val namaLengkap = findViewById<TextView>(R.id.namaLengkap)
        namaLengkap.text = user?.namaLengkap
        val jenisKelamin = findViewById<TextView>(R.id.jenisKelamin)
        jenisKelamin.text = user?.jenisKelamin
        val alamat = findViewById<TextView>(R.id.alamat)
        alamat.text = user?.alamat
        val nomorHp = findViewById<TextView>(R.id.noHP)
        nomorHp.text = user?.noHP
        val imageView = findViewById<ImageView>(R.id.image_profile)

        pilihGambarButton = findViewById(R.id.pilihGambarButton)
        pilihGambarButton.setOnClickListener {
            val options = arrayOf("Ambil Foto", "Pilih dari Galeri", "Hapus Gambar")
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
                    2 -> {
                        imageView.setImageResource(R.drawable.profile_picture)
                        selectedImageUri = null
                        Toast.makeText(this, "Gambar dihapus", Toast.LENGTH_SHORT).show()
                    }

                }
            }
            builder.show()
        }
        var buttonBack : ImageView = findViewById<ImageView>(R.id.back)
        buttonBack.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        val buttonUsername: LinearLayout = findViewById(R.id.buttonUsername)
        buttonUsername.setOnClickListener {
            showEditNameDialog("Username")

        }

        val buttonEmail: LinearLayout = findViewById(R.id.buttonEmail)
        buttonEmail.setOnClickListener {
            showEditNameDialog("Email")
        }

        val buttonNamaLengkap: LinearLayout = findViewById(R.id.buttonNamaLengkap)
        buttonNamaLengkap.setOnClickListener {
            showEditNameDialog("Nama Lengkap")
        }

        val buttonJenisKelamin: LinearLayout = findViewById(R.id.buttonJenisKelamin)
        buttonJenisKelamin.setOnClickListener {
            showEditNameDialog("Jenis Kelamin")
        }

        val buttonAlamat: LinearLayout = findViewById(R.id.buttonAlamat)
        buttonAlamat.setOnClickListener {
            showEditNameDialog("Alamat")
        }

        val buttonNoHP: LinearLayout = findViewById(R.id.buttonNoHP)
        buttonNoHP.setOnClickListener {
            showEditNameDialog("Nomor HP")
        }

        val buttonUbahPassword: LinearLayout = findViewById(R.id.buttonUbahPassword)
        buttonUbahPassword.setOnClickListener {
            Toast.makeText(this, "Ubah Password", Toast.LENGTH_SHORT).show()

        }


    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PICK_IMAGE_REQUEST -> {
                    selectedImageUri = data?.data
                }
                REQUEST_IMAGE_CAPTURE -> {
                    // URI sudah diset di openCamera
                    // selectedImageUri sudah berisi file dari kamera
                }
            }

            selectedImageUri?.let {
                val fileName = getFileName(it)
            }
        }
    }
    private fun showEditNameDialog(jenis: String) {
        val user = SharedPrefHelper.getUser(this)
        val id = user?.username

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null)
        val etNewName = view.findViewById<EditText>(R.id.etNewName)
        val tvDialogTitle = view.findViewById<TextView>(R.id.tvDialogTitle)
        tvDialogTitle.text = "Masukkan $jenis Baru"
        etNewName.hint = "$jenis baru"
        if(jenis=="Nomor HP"){
            etNewName.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        }

        MaterialAlertDialogBuilder(this)
            .setView(view)
            .setPositiveButton("Simpan") { _, _ ->
                val newName = etNewName.text.toString().trim()

                if (newName.isNotEmpty()) {

                    val updatedUser = user?.copy(jenis = newName)
                    SharedPrefHelper.saveUser(this, updatedUser)

                    val map = mutableMapOf<String, RequestBody>()

                    if(jenis=="Username"){
                        map["username"] = newName.toRequestBody("text/plain".toMediaTypeOrNull())
                    }
                    else if(jenis=="Email"){
                        if (!Patterns.EMAIL_ADDRESS.matcher(newName.toString()).matches()) {
                            Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }
                        map["email"] = newName.toRequestBody("text/plain".toMediaTypeOrNull())
                    }
                    else if(jenis=="Nama Lengkap"){
                        map["namaLengkap"] = newName.toRequestBody("text/plain".toMediaTypeOrNull())
                    }
                    else if(jenis=="Jenis Kelamin"){
                        map["jenisKelamin"] = newName.toRequestBody("text/plain".toMediaTypeOrNull())
                    }
                    else if(jenis=="Alamat"){
                        map["alamat"] = newName.toRequestBody("text/plain".toMediaTypeOrNull())
                    }
                    else if(jenis=="Nomor HP"){
                        map["noHP"] = newName.toRequestBody("text/plain".toMediaTypeOrNull())
                    } else if (jenis == "Password") {
                        map["password"] = newName.toRequestBody("text/plain".toMediaTypeOrNull())
                    }



                    ApiClient.apiService.updateUser(id.toString(), map).enqueue(object : Callback<User> {
                        override fun onResponse(call: Call<LoginResponse>, \response: Response<User>) {
                            if (response.isSuccessful) {
                                val loginResponse = response.body()
                                val user = loginResponse
                                SharedPrefHelper.saveUser(this@profile, user)

                                Toast.makeText(this@profile, "Login berhasil!", Toast.LENGTH_SHORT).show()
                                startActivity(Intent(this@profile, Beranda::class.java))
                            } else {
                                val errorBody = response.errorBody()?.string()
                                errorBody?.let {
                                    val jsonObj = JSONObject(it)
                                    val errorMessage = jsonObj.getString("message")
                                    Toast.makeText(this@profile, "Login gagal: $errorMessage", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Toast.makeText(this@profile, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                } else {
                    Toast.makeText(this, "Nama $jenis tidak boleh kosong", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    val kategoriArray = listOf("Aksesoris", "Elektronik", "Kendaraan", "Dokumen", "DLL").toTypedArray()
//    private fun showSubmenuDialog(title: String, items: List<String>, kategori: String, user: User?) {
//        val username = user?.username
//        val itemsArray = items.toTypedArray()
//        AlertDialog.Builder(this)
//            .setTitle(title)
//            .setItems(itemsArray) { _, which ->
//                val selected = items[which]
//                if (selected == "Barang Hilang") {
//                    if (title == "Daftar Laporan" || kategoriArray.contains(title)) {
//                        lifecycleScope.launch {
//                            try {
//                                val response = ApiClient.apiService.getOtherAllBarangHilang(username.toString(), kategori.toString())
//                                val intent = Intent(this@Beranda, daftarbaranghilang::class.java)
//                                intent.putParcelableArrayListExtra("dataBarang", ArrayList(response))
//                                startActivity(intent)
//
//                            } catch (e: Exception) {
//                                Toast.makeText(this@Beranda, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//
//                    } else if (title == "Riwayat Laporan") {
//
//                        lifecycleScope.launch {
//                            try {
//                                val response = ApiClient.apiService.getMyAllBarangHilang(username.toString())
//                                val intent = Intent(this@Beranda, riwayatbaranghilang::class.java)
//                                intent.putParcelableArrayListExtra("dataBarang", ArrayList(response))
//                                startActivity(intent)
//
//                            } catch (e: Exception) {
//                                Toast.makeText(this@Beranda, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//
//                    }else if (title == "Lapor") {
//                        val intent = Intent(this, laporbaranghilang::class.java)
//                        startActivity(intent)
//                    } else {
//                        null
//                    }
//                } else if (selected == "Barang Temuan") {
//                    if (title == "Daftar Laporan"|| kategoriArray.contains(title)) {
//                        lifecycleScope.launch {
//                            try {
//                                val response = ApiClient.apiService.getOtherAllBarangTemuan(username.toString(), kategori.toString())
//                                val intent = Intent(this@Beranda, daftarbarangtemuan::class.java)
//                                intent.putParcelableArrayListExtra("dataBarang", ArrayList(response))
//                                startActivity(intent)
//
//                            } catch (e: Exception) {
//                                Toast.makeText(this@Beranda, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//
//                    } else if (title == "Riwayat Laporan") {
//                        lifecycleScope.launch {
//                            try {
//                                val response = ApiClient.apiService.getMyAllBarangTemuan(username.toString())
//                                val intent = Intent(this@Beranda, riwayatbarangtemuan::class.java)
//                                intent.putParcelableArrayListExtra("dataBarang", ArrayList(response))
//                                startActivity(intent)
//
//                            } catch (e: Exception) {
//                                Toast.makeText(this@Beranda, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//
//                    }else if (title == "Lapor") {
//                        val intent = Intent(this, laporbarangtemuan::class.java)
//                        startActivity(intent)
//                    } else {
//                        null
//                    }
//                } else {
//                    null
//                }
//
//            }
//            .show()
//    }


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
