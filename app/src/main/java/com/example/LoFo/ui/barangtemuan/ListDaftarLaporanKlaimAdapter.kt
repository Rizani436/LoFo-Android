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
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ListDaftarLaporanKlaimAdapter(
    private val listJawaban: ArrayList<daftarlaporanklaimresponse>,
    private val onJawabanClick: (daftarlaporanklaimresponse) -> Unit
) : RecyclerView.Adapter<ListDaftarLaporanKlaimAdapter.ListViewHolder>() {

    inner class ListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nama : TextView = itemView.findViewById(R.id.nama)
        val username : TextView = itemView.findViewById(R.id.username)
        val alamat : TextView = itemView.findViewById(R.id.alamat)

        val buttonJawaban: MaterialButton = itemView.findViewById(R.id.buttonJawaban)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_row_daftar_laporan_klaim, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val jawaban = listJawaban[position]

        holder.nama.text = jawaban.namaLengkap
        holder.username.text = jawaban.penjawab
        holder.alamat.text = jawaban.alamat

        holder.buttonJawaban.setOnClickListener {
            onJawabanClick(jawaban)
        }
    }
    override fun getItemCount(): Int = listJawaban.size
}
