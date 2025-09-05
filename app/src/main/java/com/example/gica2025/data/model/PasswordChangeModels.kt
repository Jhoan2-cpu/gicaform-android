package com.example.gica2025.data.model

import com.google.gson.annotations.SerializedName

data class PasswordChangeRequest(
    @SerializedName("current_password")
    val currentPassword: String,
    @SerializedName("new_password")
    val newPassword: String,
    @SerializedName("confirm_password")
    val confirmPassword: String
)

data class PasswordChangeResponse(
    val success: Boolean,
    val message: String,
    val data: PasswordChangeData? = null
)

data class PasswordChangeData(
    @SerializedName("password_changed_at")
    val passwordChangedAt: String,
    @SerializedName("tokens_invalidated")
    val tokensInvalidated: Boolean
)