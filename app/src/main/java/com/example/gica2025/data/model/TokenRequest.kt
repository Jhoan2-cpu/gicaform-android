package com.example.gica2025.data.model

import com.google.gson.annotations.SerializedName

data class TokenRequest(
    @SerializedName("device_token")
    val deviceToken: String,
    val platform: String = "android"
)

data class TokenResponse(
    val success: Boolean,
    val message: String
)