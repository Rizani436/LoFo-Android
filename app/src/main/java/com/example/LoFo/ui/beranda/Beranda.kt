package com.example.LoFo.ui.beranda

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.LoFo.MainActivity
import com.example.LoFo.R
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.data.model.logout.LogoutRequest
import com.example.LoFo.data.model.logout.LogoutResponse
import com.example.LoFo.ui.baranghilang.*
import com.example.LoFo.ui.barangtemuan.*
import com.example.LoFo.utils.SharedPrefHelper
import com.google.android.material.navigation.NavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Beranda : AppCompatActivity() {
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_beranda)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.navigation_view)

        // Toggle drawer
        val toggle = androidx.appcompat.app.ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Icon notifikasi
        val notificationIcon: ImageView = findViewById(R.id.notification_icon)
        notificationIcon.setOnClickListener {
            Toast.makeText(this, "Notifikasi diklik!", Toast.LENGTH_SHORT).show()
        }

        // Gambar profil di header
        val headerView = navView.getHeaderView(0)
        val imageProfile = headerView.findViewById<ImageView>(R.id.image_profile)
        imageProfile.setOnClickListener {
            Toast.makeText(this, "Profil diklik!", Toast.LENGTH_SHORT).show()
        }

        // Navigasi drawer klik
        navView.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_daftar -> {
                    showSubmenuDialog("Daftar Laporan", listOf("Barang Hilang", "Barang Temuan"), "all")
                }

                R.id.nav_riwayat -> {
                    showSubmenuDialog("Riwayat Laporan", listOf("Barang Hilang", "Barang Temuan"), "all")
                }

                R.id.nav_lapor -> {
                    showSubmenuDialog("Lapor", listOf("Barang Hilang", "Barang Temuan"), "all")
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
            val intent = Intent(this, daftarbaranghilang::class.java)
            intent.putExtra("kategori", "all")
            startActivity(intent)
        }

        val buttonBarangTemuan: Button = findViewById(R.id.button_barangTemuan)
        buttonBarangTemuan.setOnClickListener {
            val intent = Intent(this, daftarbarangtemuan::class.java)
            intent.putExtra("kategori", "all")
            startActivity(intent)
        }

        val aksesoris= findViewById<RelativeLayout>(R.id.kategori_aksesoris)
        aksesoris.setOnClickListener {
            showSubmenuDialog("Aksesoris", listOf("Barang Hilang", "Barang Temuan"), "aksesoris")
        }

        val kendaraan= findViewById<RelativeLayout>(R.id.kategori_kendaraan)
        kendaraan.setOnClickListener {
            showSubmenuDialog("Kendaraan", listOf("Barang Hilang", "Barang Temuan"), "kendaraan")
        }

        val elektronik= findViewById<RelativeLayout>(R.id.kategori_elektronik)
        elektronik.setOnClickListener {
            showSubmenuDialog("Elektronik", listOf("Barang Hilang", "Barang Temuan"), "elektronik")
        }

        val dokumen= findViewById<RelativeLayout>(R.id.kategori_dokumen)
        dokumen.setOnClickListener {
            showSubmenuDialog("Dokumen", listOf("Barang Hilang", "Barang Temuan"), "dokumen")
        }

        val lainnya = findViewById<RelativeLayout>(R.id.kategori_lainnya)
        lainnya.setOnClickListener {
            showSubmenuDialog("Lainnya", listOf("Barang Hilang", "Barang Temuan"), "lainnya")
        }

//        val buttonLogout: Button = findViewById(R.id.buttonLogout)
//        buttonLogout.setOnClickListener {
//            performLogout()
//        }
    }
    val kategoriArray = listOf("Aksesoris", "Elektronik", "Kendaraan", "Dokumen", "Lainnya").toTypedArray()

    private fun showSubmenuDialog(title: String, items: List<String>, kategori: String) {
        val itemsArray = items.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle(title)
            .setItems(itemsArray) { _, which ->
                val selected = items[which]
                val intent = if (selected == "Barang Hilang") {
                    if (title == "Daftar Laporan" || kategoriArray.contains(title)) {
                        Intent(this, daftarbaranghilang::class.java)
                    } else if (title == "Riwayat Laporan") {
                        Intent(this, riwayatbaranghilang::class.java)
                    }else if (title == "Lapor") {
                        Intent(this, laporbaranghilang::class.java)
                    } else {
                        null
                    }
                } else if (selected == "Barang Temuan") {
                    if (title == "Daftar Laporan"|| kategoriArray.contains(title)) {
                        Intent(this, daftarbarangtemuan::class.java)
                    } else if (title == "Riwayat Laporan") {
                        Intent(this, riwayatbarangtemuan::class.java)
                    }else if (title == "Lapor") {
                        Intent(this, laporbarangtemuan::class.java)
                    } else {
                        null
                    }
                } else {
                    null
                }

                intent?.putExtra("kategori", kategori)
                intent?.let { startActivity(it) }
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
