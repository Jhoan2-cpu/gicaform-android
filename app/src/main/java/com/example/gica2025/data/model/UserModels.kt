package com.example.gica2025.data.model

import com.google.gson.annotations.SerializedName

data class UsersResponse(
    val success: Boolean,
    val data: UsersData,
    val message: String? = null
)

data class UsersData(
    val users: List<User>,
    val pagination: Pagination
)


data class Pagination(
    val page: Int,
    @SerializedName("per_page")
    val perPage: Int,
    val total: Int,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("has_more")
    val hasMore: Boolean
)


data class UserResponse(
    val success: Boolean,
    val data: User,
    val message: String? = null
)

data class ProfileResponse(
    val success: Boolean,
    val data: User,
    val message: String? = null
)

data class ProfileUpdateRequest(
    val id: Int,
    val username: String,
    val email: String,
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("first_name")
    val firstName: String,
    @SerializedName("last_name")
    val lastName: String,
    val phone: String? = null,
    val address: String? = null,
    val dni: String? = null,
    val city: String? = null,
    val region: String? = null,
    val country: String? = null,
    val reference: String? = null,
    @SerializedName("completion_percentage")
    val completionPercentage: Int? = null
)

data class ProfileUpdateResponse(
    val success: Boolean,
    val message: String,
    val data: ProfileUpdateData
)

data class ProfileUpdateData(
    @SerializedName("completion_percentage")
    val completionPercentage: Int
)

data class DeleteUserResponse(
    val success: Boolean,
    val message: String,
    val data: DeleteUserData
)

data class DeleteUserData(
    @SerializedName("deleted_user")
    val deletedUser: DeletedUser,
    @SerializedName("content_reassigned_to")
    val contentReassignedTo: String? = null,
    @SerializedName("deletion_timestamp")
    val deletionTimestamp: String
)

data class DeletedUser(
    val id: Int,
    val username: String,
    val email: String,
    @SerializedName("display_name")
    val displayName: String,
    val roles: List<String>,
    val registered: String,
    @SerializedName("post_count")
    val postCount: String,
    @SerializedName("page_count")
    val pageCount: String
)

data class LogoutResponse(
    val success: Boolean,
    val message: String
)