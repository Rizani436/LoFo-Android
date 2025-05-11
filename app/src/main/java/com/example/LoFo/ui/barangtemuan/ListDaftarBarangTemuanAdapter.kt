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

class ListDaftarBarangTemuanAdapter(
    private val listBarang: ArrayList<BarangTemuan>,
    private val onKlaimClick: (BarangTemuan) -> Unit
) : RecyclerView.Adapter<ListDaftarBarangTemuanAdapter.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.username)
        val imgItemPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val namaBarang: TextView = itemView.findViewById(R.id.namaBarang)
        val kategoriBarang: TextView = itemView.findViewById(R.id.kategoriBarang)
        val tanggalTemuan: TextView = itemView.findViewById(R.id.tanggalTemuan)
        val tempatTemuan: TextView = itemView.findViewById(R.id.tempatTemuan)
        val kotaKabupaten: TextView = itemView.findViewById(R.id.kotaKabupaten)

        val buttonKlaim: MaterialButton = itemView.findViewById(R.id.buttonKlaim)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_daftar_barang_temuan, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val barang = listBarang[position]

        // Bind data ke UI
        holder.username.text = barang.uploader
        holder.namaBarang.text = barang.namaBarang
        holder.kategoriBarang.text = barang.kategoriBarang
        holder.tanggalTemuan.text = barang.tanggalTemuan
        holder.tempatTemuan.text = barang.tempatTemuan
        holder.kotaKabupaten.text = barang.kotaKabupaten

        // Load gambar jika diperlukan
        Glide.with(holder.itemView.context)
            .load(barang.pictUrl)
            .placeholder(R.drawable.placeholder)
            .into(holder.imgItemPhoto)

        holder.buttonKlaim.setOnClickListener {
            onKlaimClick(barang)
        }
    }

    override fun getItemCount(): Int = listBarang.size
}
