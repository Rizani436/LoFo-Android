package com.example.LoFo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.LoFo.R
import com.example.LoFo.data.model.baranghilang.BarangHilang

class ListDaftarBarangHilangAdapter(
    private val listBarang: ArrayList<BarangHilang>,
) : RecyclerView.Adapter<ListDaftarBarangHilangAdapter.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgItemPhoto: ImageView = itemView.findViewById(R.id.img_item_photo)
        val username: TextView = itemView.findViewById(R.id.username)
        val namaBarang: TextView = itemView.findViewById(R.id.namaBarang)
        val kategoriBarang: TextView = itemView.findViewById(R.id.kategoriBarang)
        val tanggalKehilangan: TextView = itemView.findViewById(R.id.tanggalKehilangan)
        val tempatKehilangan: TextView = itemView.findViewById(R.id.tempatKehilangan)
        val kotaKabupaten: TextView = itemView.findViewById(R.id.kotaKabupaten)
        val informasiDetail: TextView = itemView.findViewById(R.id.informasiDetail)
        val noHP: TextView = itemView.findViewById(R.id.noHP)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_daftar_barang_hilang, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val barang = listBarang[position]

        holder.username.text = barang.uploader
        holder.namaBarang.text = barang.namaBarang
        holder.kategoriBarang.text = barang.kategoriBarang
        holder.tanggalKehilangan.text = barang.tanggalHilang
        holder.tempatKehilangan.text = barang.tempatHilang
        holder.kotaKabupaten.text = barang.kotaKabupaten
        holder.informasiDetail.text = barang.informasiDetail
        holder.noHP.text = barang.noHP

        Glide.with(holder.itemView.context)
            .load(barang.pictUrl)
            .placeholder(R.drawable.placeholder)
            .into(holder.imgItemPhoto)

    }

    override fun getItemCount(): Int = listBarang.size
}
