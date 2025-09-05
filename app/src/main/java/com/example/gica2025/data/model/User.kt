package com.example.gica2025.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int,
    val username: String,
    val email: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val roles: List<String>? = null,
    @SerializedName("avatar_url")
    val avatarUrl: String,
    val registered: String? = null,
    @SerializedName("last_login")
    val lastLogin: String? = null,
    @SerializedName("completion_percentage")
    val completionPercentage: Int? = null,
    @SerializedName("device_info")
    val deviceInfo: DeviceInfo? = null,
    val meta: UserMeta? = null,
    @SerializedName("login_history")
    val loginHistory: List<LoginHistory>? = null,
    @SerializedName("profile_data")
    val profileData: ProfileData? = null,
    @SerializedName("external_registration")
    val externalRegistration: ExternalRegistration? = null
)

data class UserMeta(
    val phone: String? = null,
    val city: String? = null,
    val country: String? = null
)

data class LoginHistory(
    val timestamp: String,
    @SerializedName("ip_address")
    val ipAddress: String,
    @SerializedName("user_agent")
    val userAgent: String,
    @SerializedName("device_info")
    val deviceInfo: DeviceInfo,
    @SerializedName("logout_timestamp")
    val logoutTimestamp: String? = null
)

data class ProfileData(
    val phone: String? = null,
    val address: String? = null,
    val dni: String? = null,
    val city: String? = null,
    val region: String? = null,
    val country: String? = null,
    val reference: String? = null
)

data class ExternalRegistration(
    @SerializedName("document_type")
    val documentType: String,
    @SerializedName("document_number")
    val documentNumber: String,
    val phone: String,
    @SerializedName("status_label")
    val statusLabel: String
)