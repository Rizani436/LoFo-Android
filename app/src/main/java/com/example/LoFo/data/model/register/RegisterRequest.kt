package com.example.LoFo.data.model.register

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val namaLengkap: String,
    val jenisKelamin: String,
    val alamat: String,
    val noHP: String,
    val pictUrl: String
)