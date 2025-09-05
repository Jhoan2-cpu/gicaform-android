package com.example.gica2025.data.network

import com.example.gica2025.data.model.DeleteUserResponse
import com.example.gica2025.data.model.LoginRequest
import com.example.gica2025.data.model.LoginResponse
import com.example.gica2025.data.model.LogoutResponse
import com.example.gica2025.data.model.PasswordChangeRequest
import com.example.gica2025.data.model.PasswordChangeResponse
import com.example.gica2025.data.model.ProfileResponse
import com.example.gica2025.data.model.ProfileUpdateRequest
import com.example.gica2025.data.model.ProfileUpdateResponse
import com.example.gica2025.data.model.UserResponse
import com.example.gica2025.data.model.UsersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>
    
    @GET("admin/users")
    suspend fun getUsers(
        @Header("X-Bearer-Token") bearerToken: String,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 10,
        @Query("search") search: String? = null,
        @Query("role") role: String? = null,
        @Query("orderby") orderBy: String? = null,
        @Query("order") order: String? = null
    ): Response<UsersResponse>
    
    @GET("admin/users/{userId}")
    suspend fun getUserById(
        @Path("userId") userId: Int,
        @Header("X-Bearer-Token") bearerToken: String
    ): Response<UserResponse>
    
    @DELETE("admin/users/{userId}")
    suspend fun deleteUser(
        @Path("userId") userId: Int,
        @Header("X-Bearer-Token") bearerToken: String
    ): Response<DeleteUserResponse>
    
    @POST("auth/logout")
    suspend fun logout(@Header("X-Bearer-Token") bearerToken: String): Response<LogoutResponse>
    
    @GET("user/profile")
    suspend fun getProfile(@Header("X-Bearer-Token") bearerToken: String): Response<ProfileResponse>
    
    @PUT("user/profile")
    suspend fun updateProfile(
        @Header("X-Bearer-Token") bearerToken: String,
        @Body profileUpdateRequest: ProfileUpdateRequest
    ): Response<ProfileUpdateResponse>
    
    @POST("user/change-password")
    suspend fun changePassword(
        @Header("X-Bearer-Token") bearerToken: String,
        @Body passwordChangeRequest: PasswordChangeRequest
    ): Response<PasswordChangeResponse>
}