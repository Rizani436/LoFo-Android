package com.example.LoFo.data.model.notifikasi

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Notifikasi(
    val notification_id: Number,
    val idUser: String,
    val idBarangTemuan: String,
    val pesanNotifikasi: String,
    val read: String
): Parcelable