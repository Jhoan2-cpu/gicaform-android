package com.example.gica2025.data.model

import com.google.gson.annotations.SerializedName

data class LoginResponse(
    val success: Boolean,
    val data: LoginData?,
    val message: String
)

data class LoginData(
    val token: String,
    @SerializedName("expires_in")
    val expiresIn: Int,
    val user: User
)