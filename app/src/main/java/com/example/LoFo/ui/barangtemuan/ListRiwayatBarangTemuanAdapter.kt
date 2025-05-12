package com.example.LoFo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.LoFo.R
import com.example.LoFo.data.api.ApiClient
import com.example.LoFo.data.model.barangtemuan.BarangTemuan
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListRiwayatBarangTemuanAdapter(
    private val listBarang: ArrayList<BarangTemuan>,
    private val onDeleteClick: (BarangTemuan) -> Unit,
    private val onEditClick: (BarangTemuan) -> Unit,
    private val onLaporanClick: (BarangTemuan) -> Unit,
    private val onSelesaiClick: (BarangTemuan) -> Unit
) : RecyclerView.Adapter<ListRiwayatBarangTemuanAdapter.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgItemPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val namaBarang: TextView = itemView.findViewById(R.id.namaBarang)
        val kategoriBarang: TextView = itemView.findViewById(R.id.kategoriBarang)
        val tanggalTemuan: TextView = itemView.findViewById(R.id.tanggalTemuan)
        val tempatTemuan: TextView = itemView.findViewById(R.id.tempatTemuan)
        val kotaKabupaten: TextView = itemView.findViewById(R.id.kotaKabupaten)
        val informasiDetail: TextView = itemView.findViewById(R.id.informasiDetail)
        val noHP: TextView = itemView.findViewById(R.id.noHP)
        val status: TextView = itemView.findViewById(R.id.status)

        val buttonHapus: MaterialButton = itemView.findViewById(R.id.buttonHapus)
        val buttonUbah: MaterialButton = itemView.findViewById(R.id.buttonUbah)
        val buttonSelesai: MaterialButton = itemView.findViewById(R.id.buttonSelesai)
        val buttonLaporan: MaterialButton = itemView.findViewById(R.id.buttonLaporan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_riwayat_barang_temuan, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val barang = listBarang[position]

        holder.namaBarang.text = barang.namaBarang
        holder.kategoriBarang.text = barang.kategoriBarang
        holder.tanggalTemuan.text = barang.tanggalTemuan
        holder.tempatTemuan.text = barang.tempatTemuan
        holder.kotaKabupaten.text = barang.kotaKabupaten
        holder.informasiDetail.text = barang.informasiDetail
        holder.noHP.text = barang.noHP
        holder.status.text = barang.status

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

                        ApiClient.apiService.updateBarangTemuan(barang.idBarangTemuan, updateStatus)
                            .enqueue(object : Callback<BarangTemuan> {
                                override fun onResponse(call: Call<BarangTemuan>, response: Response<BarangTemuan>) {
                                    if (response.isSuccessful) {
                                        Toast.makeText(holder.itemView.context, "Status berhasil diubah menjadi Selesai", Toast.LENGTH_SHORT).show()
                                        barang.status = "Selesai"
                                        notifyItemChanged(position)
                                    } else {
                                        val msg = JSONObject(response.errorBody()?.string() ?: "{}")
                                            .optString("message", "Gagal mengubah status.")
                                        Toast.makeText(holder.itemView.context, msg, Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onFailure(call: Call<BarangTemuan>, t: Throwable) {
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
        holder.buttonLaporan.setOnClickListener {
            onLaporanClick(barang)
        }
    }
    private fun showDeleteConfirmationDialog(itemView: View, barang: BarangTemuan) {
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
    private fun deleteBarang(itemView: View, barang: BarangTemuan) {
        ApiClient.apiService.deleteBarangTemuan(barang.idBarangTemuan)
            .enqueue(object : Callback<BarangTemuan> {
                override fun onResponse(call: Call<BarangTemuan>, response: Response<BarangTemuan>) {
                    if (response.isSuccessful) {

                        onDeleteClick(barang)
                    } else {
                        val msg = JSONObject(response.errorBody()?.string() ?: "{}")
                            .optString("message", "Terjadi kesalahan.")
                        Toast.makeText(itemView.context, msg, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<BarangTemuan>, t: Throwable) {
                    Toast.makeText(itemView.context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun getItemCount(): Int = listBarang.size
}
