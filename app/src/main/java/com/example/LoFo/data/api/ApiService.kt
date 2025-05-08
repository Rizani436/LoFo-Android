package com.example.LoFo.data.api

import com.example.LoFo.data.model.baranghilang.BarangHilangResponse
import com.example.LoFo.data.model.barangtemuan.BarangTemuanResponse
import com.example.LoFo.data.model.login.LoginRequest
import com.example.LoFo.data.model.login.LoginResponse
import com.example.LoFo.data.model.logout.LogoutRequest
import com.example.LoFo.data.model.logout.LogoutResponse
import com.example.LoFo.data.model.register.RegisterRequest
import com.example.LoFo.data.model.register.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @POST("user/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
    @POST("user/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>
    @POST("user/logout")
    fun logoutUser(@Body request: LogoutRequest) : Call<LogoutResponse>

    @Multipart
    @POST("barang-temuan/create")
    fun createBarangTemuan(
        @Part file: MultipartBody.Part,
        @Part("userId") userId: RequestBody,
        @Part("namaBarang") namaBarang: RequestBody,
        @Part("kategoriBarang") kategoriBarang: RequestBody,
        @Part("tempatTemuan") tempatTemuan: RequestBody,
        @Part("kotaKabupaten") kotaKabupaten: RequestBody,
        @Part("tanggalTemuan") tanggalTemuan: RequestBody,
        @Part("informasiDetail") informasiDetail: RequestBody,
        @Part("noHP") noHP: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("status") status: RequestBody
    ): Call<BarangTemuanResponse>

    @Multipart
    @POST("barang-hilang/create")
    fun createBarangHilang(
        @Part file: MultipartBody.Part,
        @Part("userId") userId: RequestBody,
        @Part("namaBarang") namaBarang: RequestBody,
        @Part("kategoriBarang") kategoriBarang: RequestBody,
        @Part("tempatHilang") tempatTemuan: RequestBody,
        @Part("kotaKabupaten") kotaKabupaten: RequestBody,
        @Part("tanggalHilang") tanggalTemuan: RequestBody,
        @Part("informasiDetail") informasiDetail: RequestBody,
        @Part("noHP") noHP: RequestBody,
        @Part("latitude") latitude: RequestBody,
        @Part("longitude") longitude: RequestBody,
        @Part("status") status: RequestBody
    ): Call<BarangHilangResponse>


//    @Multipart
//    @POST("barang-temuan/upload")
//    fun uploadBarangTemuan(
//        @Part file: MultipartBody.Part,
//        @Part("nama") nama: RequestBody,
//    ) : Call<ResponseBody>

}