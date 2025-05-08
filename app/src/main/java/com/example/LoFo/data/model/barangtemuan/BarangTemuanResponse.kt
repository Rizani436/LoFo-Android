package com.example.LoFo.data.model.barangtemuan

data class BarangTemuanResponse(
    val uploader: String,
    val namaBarang: String,
    val kategoriBarang: String,
    val tempatTemuan: String,
    val kotaKabupaten: String,
    val tanggalTemuan: String,
    val informasiDetail: String,
    val noHP : String,
    val pictUrl : String,
    val status : String,
    val latitude : Float,
    val longitude : Float
)