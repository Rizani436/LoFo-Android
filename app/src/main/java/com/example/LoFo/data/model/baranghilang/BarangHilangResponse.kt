package com.example.LoFo.data.model.baranghilang

data class BarangHilangResponse(
    val userId: String,
    val namaBarang: String,
    val kategoriBarang: String,
    val tempatHilang: String,
    val kotaKabupaten: String,
    val tanggalHilang: String,
    val informasiDetail: String,
    val noHP : String,
    val pictUrl : String,
    val status : String,
    val latitude : Float,
    val longitude : Float

)