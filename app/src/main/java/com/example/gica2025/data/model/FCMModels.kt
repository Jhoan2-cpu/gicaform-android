package com.example.gica2025.data.model

import com.google.gson.annotations.SerializedName

data class FCMTokenRequest(
    val action: String = "gica_mobile_register_token",
    @SerializedName("api_key")
    val apiKey: String = "gica_mobile_2024",
    @SerializedName("fcm_token")
    val fcmToken: String,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("device_info")
    val deviceInfo: String,
    @SerializedName("app_version")
    val appVersion: String,
    @SerializedName("user_id")
    val userId: String = "" // Opcional: ID de usuario si está logueado
)

data class FCMTokenResponse(
    val success: Boolean,
    val data: FCMResponseData?
)

data class FCMResponseData(
    val message: String?
)