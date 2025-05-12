package com.example.LoFo.data.model.baranghilang

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class BarangHilang(
    val idBarangHilang: String,
    val uploader: String,
    val namaBarang: String,
    val kategoriBarang: String,
    val tanggalHilang: String,
    val tempatHilang: String,
    val kotaKabupaten: String,
    val informasiDetail: String,
    val noHP: String,
    val pictUrl: String,
    var status: String,
    val latitude: Double,
    val longitude: Double
): Parcelable