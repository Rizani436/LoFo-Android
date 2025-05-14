package com.example.LoFo.data.model.jawabanpertanyaan

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class daftarlaporanklaimresponse(
    val idBarangTemuan: String,
    val penanya: String,
    val pertanyaan: String,
    val penjawab: String,
    val jawaban: String,
    val alamat: String,
    val namaLengkap: String,
): Parcelable