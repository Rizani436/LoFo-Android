package com.example.LoFo.data.model.login

import com.example.LoFo.data.model.user.User

data class LoginResponse(
    val User: User,
    val token: String
)
