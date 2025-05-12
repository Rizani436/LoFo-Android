package com.example.LoFo.ui.beranda

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.LoFo.MainActivity
import com.example.LoFo.R
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.data.model.logout.LogoutRequest
import com.example.LoFo.data.model.logout.LogoutResponse
import com.example.LoFo.data.model.user.User
import com.example.LoFo.ui.baranghilang.*
import com.example.LoFo.ui.barangtemuan.*
import com.example.LoFo.ui.profile.profile
import com.example.LoFo.utils.SharedPrefHelper
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Beranda : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beranda)
        val user = SharedPrefHelper.getUser(this)
        val imageUrl = user?.pictUrl

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.navigation_view)

        val toggle = androidx.appcompat.app.ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val notificationIcon: ImageView = findViewById(R.id.notification_icon)
        notificationIcon.setOnClickListener {
            Toast.makeText(this, "Notifikasi diklik!", Toast.LENGTH_SHORT).show()
        }

        val headerView = navView.getHeaderView(0)
        val imageProfile = headerView.findViewById<ImageView>(R.id.image_profile)
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.profile_picture)
            .error(R.drawable.profile_picture)
            .into(imageProfile)
        val user_name = headerView.findViewById<TextView>(R.id.user_name)
        user_name.text = user?.username
        imageProfile.setOnClickListener {

        }

        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_daftar -> {
                    showSubmenuDialog("Daftar Laporan", listOf("Barang Hilang", "Barang Temuan"), "All", user)
                }

                R.id.nav_riwayat -> {
                    showSubmenuDialog("Riwayat Laporan", listOf("Barang Hilang", "Barang Temuan"), "All", user)
                }

                R.id.nav_lapor -> {
                    showSubmenuDialog("Lapor", listOf("Barang Hilang", "Barang Temuan"), "All", user)
                }
                R.id.nav_profil -> {
                    val intent = Intent(this, profile::class.java)
                    startActivity(intent)
                }
                R.id.nav_keluar -> {
                    performLogout()
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        val buttonBarangKehilangan: Button = findViewById(R.id.button_barangKehilangan)
        buttonBarangKehilangan.setOnClickListener {
            val username = user?.username
            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.getOtherAllBarangHilang(username.toString(), "All")
                    val intent = Intent(this@Beranda, daftarbaranghilang::class.java)
                    intent.putParcelableArrayListExtra("dataBarang", ArrayList(response))
                    startActivity(intent)

                } catch (e: Exception) {
                    Toast.makeText(this@Beranda, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val buttonBarangTemuan: Button = findViewById(R.id.button_barangTemuan)
        buttonBarangTemuan.setOnClickListener {
            val username = user?.username
            lifecycleScope.launch {
                try {
                    val response = ApiClient.apiService.getOtherAllBarangTemuan(username.toString(), "All")
                    val intent = Intent(this@Beranda, daftarbarangtemuan::class.java)
                    intent.putParcelableArrayListExtra("dataBarang", ArrayList(response))
                    startActivity(intent)

                } catch (e: Exception) {
                    Toast.makeText(this@Beranda, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                }
            }
        }

        val aksesoris= findViewById<RelativeLayout>(R.id.kategori_aksesoris)
        aksesoris.setOnClickListener {
            showSubmenuDialog("Aksesoris", listOf("Barang Hilang", "Barang Temuan"), "Aksesoris", user)
        }

        val kendaraan= findViewById<RelativeLayout>(R.id.kategori_kendaraan)
        kendaraan.setOnClickListener {
            showSubmenuDialog("Kendaraan", listOf("Barang Hilang", "Barang Temuan"), "Kendaraan", user)
        }

        val elektronik= findViewById<RelativeLayout>(R.id.kategori_elektronik)
        elektronik.setOnClickListener {
            showSubmenuDialog("Elektronik", listOf("Barang Hilang", "Barang Temuan"), "Elektronik", user)
        }

        val dokumen= findViewById<RelativeLayout>(R.id.kategori_dokumen)
        dokumen.setOnClickListener {
            showSubmenuDialog("Dokumen", listOf("Barang Hilang", "Barang Temuan"), "Dokumen", user)
        }

        val lainnya = findViewById<RelativeLayout>(R.id.kategori_lainnya)
        lainnya.setOnClickListener {
            showSubmenuDialog("DLL", listOf("Barang Hilang", "Barang Temuan"), "DLL", user)
        }

    }
    val kategoriArray = listOf("Aksesoris", "Elektronik", "Kendaraan", "Dokumen", "DLL").toTypedArray()

    private fun showSubmenuDialog(title: String, items: List<String>, kategori: String, user: User?) {
        val username = user?.username
        val itemsArray = items.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle(title)
            .setItems(itemsArray) { _, which ->
                val selected = items[which]
                if (selected == "Barang Hilang") {
                    if (title == "Daftar Laporan" || kategoriArray.contains(title)) {
                        lifecycleScope.launch {
                            try {
                                val response = ApiClient.apiService.getOtherAllBarangHilang(username.toString(), kategori.toString())
                                val intent = Intent(this@Beranda, daftarbaranghilang::class.java)
                                intent.putParcelableArrayListExtra("dataBarang", ArrayList(response))
                                startActivity(intent)

                            } catch (e: Exception) {
                                Toast.makeText(this@Beranda, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } else if (title == "Riwayat Laporan") {

                        lifecycleScope.launch {
                            try {
                                val response = ApiClient.apiService.getMyAllBarangHilang(username.toString())
                                val intent = Intent(this@Beranda, riwayatbaranghilang::class.java)
                                intent.putParcelableArrayListExtra("dataBarang", ArrayList(response))
                                startActivity(intent)

                            } catch (e: Exception) {
                                Toast.makeText(this@Beranda, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }else if (title == "Lapor") {
                        val intent = Intent(this, laporbaranghilang::class.java)
                        startActivity(intent)
                    } else {
                        null
                    }
                } else if (selected == "Barang Temuan") {
                    if (title == "Daftar Laporan"|| kategoriArray.contains(title)) {
                        lifecycleScope.launch {
                            try {
                                val response = ApiClient.apiService.getOtherAllBarangTemuan(username.toString(), kategori.toString())
                                val intent = Intent(this@Beranda, daftarbarangtemuan::class.java)
                                intent.putParcelableArrayListExtra("dataBarang", ArrayList(response))
                                startActivity(intent)

                            } catch (e: Exception) {
                                Toast.makeText(this@Beranda, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                            }
                        }

                    } else if (title == "Riwayat Laporan") {
                        lifecycleScope.launch {
                            try {
                                val response = ApiClient.apiService.getMyAllBarangTemuan(username.toString())
                                val intent = Intent(this@Beranda, riwayatbarangtemuan::class.java)
                                intent.putParcelableArrayListExtra("dataBarang", ArrayList(response))
                                startActivity(intent)

                            } catch (e: Exception) {
                                Toast.makeText(this@Beranda, "Gagal mengambil data / data kosong", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }else if (title == "Lapor") {
                        val intent = Intent(this, laporbarangtemuan::class.java)
                        startActivity(intent)
                    } else {
                        null
                    }
                } else {
                    null
                }

            }
            .show()
    }

    private fun performLogout() {
        val token = SharedPrefHelper.getToken(this)
        val request = LogoutRequest(token.toString())

        ApiClient.apiService.logoutUser(request).enqueue(object : Callback<LogoutResponse> {
            override fun onResponse(
                call: Call<LogoutResponse>,
                response: Response<LogoutResponse>
            ) {
                if (response.isSuccessful) {
                    SharedPrefHelper.clear(this@Beranda)
                    Toast.makeText(this@Beranda, "Logout Berhasil", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@Beranda, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this@Beranda, "Logout Gagal", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                Toast.makeText(this@Beranda, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
