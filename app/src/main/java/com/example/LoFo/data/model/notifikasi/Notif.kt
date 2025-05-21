package com.example.LoFo.data.model.notifikasi

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notif(
    val idBarangTemuan: String,
    val uploader: String,
    val namaBarang: String,
    val kategoriBarang: String,
    val tanggalTemuan: String,
    val tempatTemuan: String,
    val kotaKabupaten: String,
    val informasiDetail: String,
    val noHP: String,
    val pictUrl: String,
    val status: String,
    val latitude: Double,
    val longitude: Double
): Parcelable