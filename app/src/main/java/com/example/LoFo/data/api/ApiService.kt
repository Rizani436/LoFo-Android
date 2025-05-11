package com.example.LoFo.data.api

import com.example.LoFo.data.model.baranghilang.BarangHilang
import com.example.LoFo.data.model.baranghilang.BarangHilangResponse
import com.example.LoFo.data.model.barangtemuan.BarangTemuan
import com.example.LoFo.data.model.barangtemuan.BarangTemuanResponse
import com.example.LoFo.data.model.login.LoginRequest
import com.example.LoFo.data.model.login.LoginResponse
import com.example.LoFo.data.model.logout.LogoutRequest
import com.example.LoFo.data.model.logout.LogoutResponse
import com.example.LoFo.data.model.register.RegisterRequest
import com.example.LoFo.data.model.register.RegisterResponse
import com.example.LoFo.data.model.user.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.Path
import retrofit2.http.Query

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

    @GET("barang-hilang/getAll")
    suspend fun getAllBarangHilang(): List<BarangHilang>

    @GET("barang-hilang/getMyAll/{id}")
    suspend fun getMyAllBarangHilang( @Path("id") id: String): List<BarangHilang>

    @GET("barang-hilang/getOtherAll/{id}")
    suspend fun getOtherAllBarangHilang(
        @Path("id") id: String,
        @Query("kategoriBarang") kategoriBarang: String
    ): List<BarangHilang>


    @GET("barang-temuan/getAll")
    suspend fun getAllBarangTemuan( ): List<BarangTemuan>

    @Multipart
    @PUT("user/update/{id}")
    fun updateUser(
        @Path("id") id: String,
        @PartMap params: Map<String, @JvmSuppressWildcards RequestBody>
    ): Call<User>



//    @PUT("user/update/{id}")
//    suspend fun updateUser(
//        @Path("id") id: String,
//        @Part("username") username: RequestBody,
//        @Part file: MultipartBody.Part,
//        @Part("email") email: RequestBody,
//        @Part("namaLengkap") namaLengkap: RequestBody,
//        @Part("jenisKelamin") jenisKelamin: RequestBody,
//        @Part("alamat") alamat: RequestBody,
//        @Part("noHP") noHP: RequestBody,
//        @Part("password") password: RequestBody,
//    ): List<User>



    @GET("barang-temuan/getMyAll/{id}")
    suspend fun getMyAllBarangTemuan( @Path("id") id: String): List<BarangTemuan>

    @GET("barang-temuan/getOtherAll/{id}")
    suspend fun getOtherAllBarangTemuan(
        @Path("id") id: String,
        @Query("kategoriBarang") kategoriBarang: String
    ): List<BarangTemuan>

//    @Multipart
//    @POST("barang-temuan/upload")
//    fun uploadBarangTemuan(
//        @Part file: MultipartBody.Part,
//        @Part("nama") nama: RequestBody,
//    ) : Call<ResponseBody>

}