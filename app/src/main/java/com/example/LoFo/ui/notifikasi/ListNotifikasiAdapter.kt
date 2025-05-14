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
import com.example.LoFo.data.model.jawabanpertanyaan.JawabanPertanyaanResponse
import com.example.LoFo.data.model.jawabanpertanyaan.daftarlaporanklaimresponse
import com.example.LoFo.data.model.notifikasi.Notifikasi
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListNotifikasiAdapter(
    private val listNotifikasi: ArrayList<Notifikasi>,
    private val onTampilkanClick: (Notifikasi) -> Unit,
    private val onDeleteClick: (Notifikasi) -> Unit,
) : RecyclerView.Adapter<ListNotifikasiAdapter.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val pesan : TextView = itemView.findViewById(R.id.pesan)
        val buttonTampilkan : TextView = itemView.findViewById(R.id.tombolTampilkan)
        val buttonHapus : TextView = itemView.findViewById(R.id.tombolHapus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_notifikasi, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val notifikasi = listNotifikasi[position]

        holder.pesan.text = notifikasi.pesanNotifikasi
        holder.buttonTampilkan.setOnClickListener {
            onTampilkanClick(notifikasi)
        }
        holder.buttonHapus.setOnClickListener {
            onDeleteClick(notifikasi)
        }
    }
    override fun getItemCount(): Int = listNotifikasi.size
}
