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
    val roles: List<String>,
    @SerializedName("avatar_url")
    val avatarUrl: String
)