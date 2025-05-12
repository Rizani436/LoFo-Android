package com.example.LoFo.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.LoFo.R
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.data.model.baranghilang.BarangHilang
import com.example.LoFo.data.model.user.User
import com.example.LoFo.ui.profile.profile
import com.example.LoFo.utils.SharedPrefHelper
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListRiwayatBarangHilangAdapter(
    private val listBarang: ArrayList<BarangHilang>,
    private val onDeleteClick: (BarangHilang) -> Unit,
    private val onEditClick: (BarangHilang) -> Unit,
    private val onSelesaiClick: (BarangHilang) -> Unit
) : RecyclerView.Adapter<ListRiwayatBarangHilangAdapter.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgItemPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val namaBarang: TextView = itemView.findViewById(R.id.namaBarang)
        val kategoriBarang: TextView = itemView.findViewById(R.id.kategoriBarang)
        val tanggalKehilangan: TextView = itemView.findViewById(R.id.tanggalKehilangan)
        val tempatKehilangan: TextView = itemView.findViewById(R.id.tempatKehilangan)
        val kotaKabupaten: TextView = itemView.findViewById(R.id.kotaKabupaten)
        val informasiDetail: TextView = itemView.findViewById(R.id.informasiDetail)
        val noHP: TextView = itemView.findViewById(R.id.noHP)
        val status: TextView = itemView.findViewById(R.id.status)

        val buttonHapus: MaterialButton = itemView.findViewById(R.id.buttonHapus)
        val buttonUbah: MaterialButton = itemView.findViewById(R.id.buttonUbah)
        val buttonSelesai: MaterialButton = itemView.findViewById(R.id.buttonSelesai)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_riwayat_barang_hilang, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val barang = listBarang[position]

        // Bind data ke UI
        holder.namaBarang.text = barang.namaBarang
        holder.kategoriBarang.text = barang.kategoriBarang
        holder.tanggalKehilangan.text = barang.tanggalHilang
        holder.tempatKehilangan.text = barang.tempatHilang
        holder.kotaKabupaten.text = barang.kotaKabupaten
        holder.informasiDetail.text = barang.informasiDetail
        holder.noHP.text = barang.noHP
        holder.status.text = barang.status

        // Load gambar jika diperlukan
        Glide.with(holder.itemView.context)
            .load(barang.pictUrl)
            .placeholder(R.drawable.placeholder)
            .into(holder.imgItemPhoto)

        holder.buttonHapus.setOnClickListener {
            showDeleteConfirmationDialog(holder.itemView, barang)
        }


        holder.buttonUbah.setOnClickListener {
            onEditClick(barang)
        }
        holder.buttonSelesai.setOnClickListener {
            val context = holder.itemView.context

            AlertDialog.Builder(context)
                .setTitle("Konfirmasi")
                .setMessage("Apakah Anda yakin ingin menyelesaikan laporan ini?")
                .setPositiveButton("Ya") { dialog, _ ->
                    if (barang.status == "Diterima") {
                        val updateStatus = mapOf("status" to "Selesai")

                        ApiClient.apiService.updateBarangHilang(barang.idBarangHilang, updateStatus)
                            .enqueue(object : Callback<BarangHilang> {
                                override fun onResponse(call: Call<BarangHilang>, response: Response<BarangHilang>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(holder.itemView.context, "Status berhasil diubah menjadi Selesai", Toast.LENGTH_SHORT).show()
                                        barang.status = "Selesai" // update status lokal
                                        notifyItemChanged(position) // refresh tampilan item
                                    } else {
                                        val msg = JSONObject(response.errorBody()?.string() ?: "{}")
                                            .optString("message", "Gagal mengubah status.")
                                        Toast.makeText(holder.itemView.context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<BarangHilang>, t: Throwable) {
                                    Toast.makeText(holder.itemView.context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                                }
                            })
                    } else if (barang.status == "Selesai"){
                        Toast.makeText(context, "Status Barang Sudah Selesai", Toast.LENGTH_SHORT).show()
                    } else if (barang.status == "Ditolak"){
                        Toast.makeText(context, "Barang Sudah Ditolak", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(context, "Status Barang Harus Diterima Dulu", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }


    }
    private fun showDeleteConfirmationDialog(itemView: View, barang: BarangHilang) {
        MaterialAlertDialogBuilder(itemView.context)
            .setTitle("Konfirmasi Penghapusan")
            .setMessage("Apakah kamu yakin ingin menghapus barang ini?")
            .setPositiveButton("Ya") { dialog, _ ->
                deleteBarang(itemView, barang)
                dialog.dismiss()
            }
            .setNegativeButton("Batal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }
    private fun deleteBarang(itemView: View, barang: BarangHilang) {
        ApiClient.apiService.deleteBarangHilang(barang.idBarangHilang)
            .enqueue(object : Callback<BarangHilang> {
                override fun onResponse(call: Call<BarangHilang>, response: Response<BarangHilang>) {
                    if (response.isSuccessful) {
                        onDeleteClick(barang) // Untuk memberitahu adapter/data diperbarui
                    } else {
                        val msg = JSONObject(response.errorBody()?.string() ?: "{}")
                            .optString("message", "Terjadi kesalahan.")
                        Toast.makeText(itemView.context, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BarangHilang>, t: Throwable) {
                    Toast.makeText(itemView.context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }



    override fun getItemCount(): Int = listBarang.size
}
