package com.example.LoFo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.LoFo.R
import com.example.LoFo.data.model.barangtemuan.BarangTemuan
import com.google.android.material.button.MaterialButton

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

        // Bind data ke UI
        holder.namaBarang.text = barang.namaBarang
        holder.kategoriBarang.text = barang.kategoriBarang
        holder.tanggalTemuan.text = barang.tanggalTemuan
        holder.tempatTemuan.text = barang.tempatTemuan
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
            onDeleteClick(barang)
        }

        holder.buttonUbah.setOnClickListener {
            onEditClick(barang)
        }
        holder.buttonSelesai.setOnClickListener {
            onSelesaiClick(barang)
        }
        holder.buttonLaporan.setOnClickListener {
            onLaporanClick(barang)
        }
    }

    override fun getItemCount(): Int = listBarang.size
}
