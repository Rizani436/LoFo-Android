package com.example.LoFo.data.api

import com.example.LoFo.data.model.login.LoginRequest
import com.example.LoFo.data.model.login.LoginResponse
import com.example.LoFo.data.model.logout.LogoutRequest
import com.example.LoFo.data.model.logout.LogoutResponse
import com.example.LoFo.data.model.register.RegisterRequest
import com.example.LoFo.data.model.register.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("user/login")
    fun loginUser(@Body request: LoginRequest): Call<LoginResponse>
    @POST("user/register")
    fun registerUser(@Body request: RegisterRequest): Call<RegisterResponse>
    @POST("user/logout")
    fun logoutUser(@Body request: LogoutRequest) : Call<LogoutResponse>

}