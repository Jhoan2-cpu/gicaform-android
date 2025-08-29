package com.example.gica2025.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val username: String,
    val password: String,
    @SerializedName("device_info")
    val deviceInfo: DeviceInfo
)

data class DeviceInfo(
    val type: String = "mobile",
    val platform: String = "android",
    @SerializedName("app_version")
    val appVersion: String = "1.0.0",
    @SerializedName("device_id")
    val deviceId: String
)