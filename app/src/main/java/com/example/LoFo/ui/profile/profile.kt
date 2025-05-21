package com.example.LoFo.ui.profile

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.text.InputType
import android.util.Patterns
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.bumptech.glide.Glide
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.LoFo.MainActivity
import com.example.LoFo.R
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.data.model.user.User
import com.example.LoFo.utils.SharedPrefHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
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
import android.util.Log
import android.view.View
import com.yalantis.ucrop.UCrop

class profile : AppCompatActivity() {
    private lateinit var pilihGambarButton: RelativeLayout
    private lateinit var namaFile: TextView
    var REQUEST_IMAGE_CAPTURE = 1
    private var PICK_IMAGE_REQUEST = 2
    private val REQUEST_IMAGE_CROP = 3
    private var CAMERA_PERMISSION_CODE = 100
    private var selectedImageUri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val user = SharedPrefHelper.getUser(this)
        val userId = user?.username ?: return
        val imageUrl = user.pictUrl

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
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.profile_picture)
            .error(R.drawable.profile_picture)
            .into(imageView)

        pilihGambarButton = findViewById(R.id.pilihGambarButton)
        pilihGambarButton.setOnClickListener {
            REQUEST_IMAGE_CAPTURE = 1
            PICK_IMAGE_REQUEST = 2
            CAMERA_PERMISSION_CODE = 100
            selectedImageUri= null
            val options = arrayOf("Ambil Foto", "Pilih dari Galeri", "Hapus Gambar")
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
                    2 -> {
                        imageView.setImageResource(R.drawable.profile_picture)
                        val user = SharedPrefHelper.getUser(this)
                        val userId = user?.username ?: return@setItems
                        ApiClient.apiService.deleteProfile(userId)
                            .enqueue(object : Callback<User> {
                                override fun onResponse(call: Call<User>, response: Response<User>) {
                                    if (response.isSuccessful) {
                                        response.body()?.let { updatedUser ->
                                            SharedPrefHelper.saveUser(this@profile, updatedUser)
                                            Toast.makeText(this@profile, "Gambar Profile berhasil dihapus", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this@profile, profile::class.java))
                                            finish()
                                        }
                                    } else {
                                        val msg = JSONObject(response.errorBody()?.string() ?: "{}")
                                            .optString("message", "Terjadi kesalahan.")
                                        Toast.makeText(this@profile, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<User>, t: Throwable) {
                                    Toast.makeText(this@profile, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
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
            showEditNameDialogPassword()

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
                        val imageView = findViewById<ImageView>(R.id.image_profile)
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
            .withAspectRatio(1f, 1f)
            .withMaxResultSize(500, 500)
            .start(this, REQUEST_IMAGE_CROP)
    }

    private fun showEditNameDialog(jenis: String) {
        val user = SharedPrefHelper.getUser(this)
        val id = user?.username

        var view : View
        if (jenis == "Nomor HP") {
            view = LayoutInflater.from(this).inflate(R.layout.dialog_editnohp, null)
        }else {
            view = LayoutInflater.from(this).inflate(R.layout.dialog_edit, null)
        }
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

                    val updateMap = mutableMapOf<String, String>()

                    if(jenis=="Username"){
                        updateMap["username"] = newName.toString()
                    }
                    else if(jenis=="Email"){
                        if (!Patterns.EMAIL_ADDRESS.matcher(newName.toString()).matches()) {
                            Toast.makeText(this, "Format email tidak valid", Toast.LENGTH_SHORT).show()
                            return@setPositiveButton
                        }
                        updateMap["email"] = newName.toString()
                    }
                    else if(jenis=="Nama Lengkap"){
                        updateMap["namaLengkap"] = newName.toString()
                    }
                    else if(jenis=="Jenis Kelamin"){
                        updateMap["jenisKelamin"] = newName.toString()
                    }
                    else if(jenis=="Alamat"){
                        updateMap["alamat"] = newName.toString()
                    }
                    else if(jenis=="Nomor HP"){
                        updateMap["noHP"] = "62"+newName.toString()
                    } else if (jenis == "Password") {
                        updateMap["password"] = newName.toString()
                    }

                    ApiClient.apiService.updateUser(id.toString(), updateMap).enqueue(object : Callback<User> {
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            if (response.isSuccessful) {
                                val user = response.body()
                                if (user != null) {
                                    Log.d("UserUpdate", user.toString())
                                    SharedPrefHelper.saveUser(this@profile, user)
                                    Toast.makeText(this@profile, "Update berhasil!", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@profile, profile::class.java))
                                } else {
                                    Toast.makeText(this@profile, "Update gagal: data kosong", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                val errorBody = response.errorBody()?.string()
                                errorBody?.let {
                                    val jsonObj = JSONObject(it)
                                    val errorMessage = jsonObj.optString("message", "Terjadi kesalahan.")
                                    Toast.makeText(this@profile, "Update gagal: $errorMessage", Toast.LENGTH_SHORT).show()
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

    private fun showEditNameDialogPassword() {
        val user = SharedPrefHelper.getUser(this)
        val id = user?.username ?: return

        val view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_password, null)
        val passwordBaru = view.findViewById<EditText>(R.id.passwordBaru)
        val passwordBaruLagi = view.findViewById<EditText>(R.id.passwordBaruLagi)
        val passwordLama = view.findViewById<EditText>(R.id.passwordLama)

        val showPasswordLamaIcon = view.findViewById<ImageView>(R.id.showPasswordLamaIcon)
        val showPasswordBaruIcon = view.findViewById<ImageView>(R.id.showPasswordBaruIcon)
        val showPasswordBaruLagiIcon = view.findViewById<ImageView>(R.id.showPasswordBaruLagiIcon)

        var isPasswordLamaVisible = false
        var isPasswordBaruVisible = false
        var isPasswordBaruLagiVisible = false

        showPasswordLamaIcon.setOnClickListener {
            isPasswordLamaVisible = !isPasswordLamaVisible
            togglePasswordVisibility(passwordLama, showPasswordLamaIcon, isPasswordLamaVisible)
        }

        showPasswordBaruIcon.setOnClickListener {
            isPasswordBaruVisible = !isPasswordBaruVisible
            togglePasswordVisibility(passwordBaru, showPasswordBaruIcon, isPasswordBaruVisible)
        }

        showPasswordBaruLagiIcon.setOnClickListener {
            isPasswordBaruLagiVisible = !isPasswordBaruLagiVisible
            togglePasswordVisibility(passwordBaruLagi, showPasswordBaruLagiIcon, isPasswordBaruLagiVisible)
        }

        val dialog = MaterialAlertDialogBuilder(this)
            .setView(view)
            .setPositiveButton("Simpan", null)
            .setNegativeButton("Batal", null)
            .create()

        dialog.setOnShowListener {
            val btnSimpan = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnSimpan.setOnClickListener {
                val password = passwordLama.text.toString().trim()
                val newPassword = passwordBaru.text.toString().trim()
                val confirmPassword = passwordBaruLagi.text.toString().trim()

                if (password.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                if (newPassword != confirmPassword) {
                    Toast.makeText(this, "Password baru tidak cocok", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val updateMap = mapOf(
                    "password" to password,
                    "newPassword" to newPassword,
                    "confirmPassword" to confirmPassword
                )

                ApiClient.apiService.changePassword(id, updateMap)
                    .enqueue(object : Callback<User> {
                        override fun onResponse(call: Call<User>, response: Response<User>) {
                            if (response.isSuccessful) {
                                response.body()?.let { updatedUser ->
                                    SharedPrefHelper.saveUser(this@profile, updatedUser)
                                    Toast.makeText(this@profile, "Password berhasil diubah", Toast.LENGTH_SHORT).show()
                                    startActivity(Intent(this@profile, profile::class.java))
                                    dialog.dismiss()
                                }
                            } else {
                                val msg = JSONObject(response.errorBody()?.string() ?: "{}")
                                    .optString("message", "Terjadi kesalahan.")
                                Toast.makeText(this@profile, msg, Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<User>, t: Throwable) {
                            Toast.makeText(this@profile, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }
        }

        dialog.show()

    }

    private fun togglePasswordVisibility(editText: EditText, icon: ImageView, visible: Boolean) {
        editText.inputType = if (visible) {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        icon.setImageResource(if (visible) R.drawable.eye_on else R.drawable.eye_off)
        editText.setSelection(editText.text.length)
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

        val user = SharedPrefHelper.getUser(this)
        val userId = user?.username ?: return

        ApiClient.apiService.changeProfile(userId, filePart)
            .enqueue(object : Callback<User> {
                override fun onResponse(call: Call<User>, response: Response<User>) {
                    if (response.isSuccessful) {
                        response.body()?.let { updatedUser ->
                            SharedPrefHelper.saveUser(this@profile, updatedUser)
                            Toast.makeText(this@profile, "Gambar Profile berhasil diubah", Toast.LENGTH_SHORT).show()
                            startActivity(Intent(this@profile, profile::class.java))
                            finish()
                        }
                    } else {
                        val msg = JSONObject(response.errorBody()?.string() ?: "{}")
                            .optString("message", "Terjadi kesalahan.")
                        Toast.makeText(this@profile, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<User>, t: Throwable) {
                    Toast.makeText(this@profile, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

}
