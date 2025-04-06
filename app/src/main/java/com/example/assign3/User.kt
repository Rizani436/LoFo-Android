package com.example.assign3
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    val username: String,
    val email: String,
    val password: String,
    val namaLengkap: String,
    val jenisKelamin: String,
    val alamat: String,
    val nomorHandphone: String
) : Parcelable