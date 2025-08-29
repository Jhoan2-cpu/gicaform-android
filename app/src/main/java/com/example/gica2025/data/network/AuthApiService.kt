package com.example.gica2025.data.network

import com.example.gica2025.data.model.DeleteUserRequest
import com.example.gica2025.data.model.DeleteUserResponse
import com.example.gica2025.data.model.LoginRequest
import com.example.gica2025.data.model.LoginResponse
import com.example.gica2025.data.model.RetryResponse
import com.example.gica2025.data.model.UsersResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.HTTP
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApiService {
    @POST("wp-json/gicaform/v1/auth/login")
    suspend fun login(@Body loginRequest: LoginRequest): Response<LoginResponse>
    
    @GET("wp-json/gicaform/v1/users")
    suspend fun getUsers(@Header("X-Bearer-Token") bearerToken: String): Response<UsersResponse>
    
    @POST("wp-json/gicaform/v1/users/{userId}/retry")
    suspend fun retryUser(
        @Path("userId") userId: Int,
        @Header("X-Bearer-Token") bearerToken: String
    ): Response<RetryResponse>
    
    @HTTP(method = "DELETE", path = "wp-json/gicaform/v1/users/{userId}", hasBody = true)
    suspend fun deleteUser(
        @Path("userId") userId: Int,
        @Body deleteRequest: DeleteUserRequest,
        @Header("X-Bearer-Token") bearerToken: String
    ): Response<DeleteUserResponse>
    
    @POST("wp-json/gicaform/v1/users/retry-all")
    suspend fun retryAllUsers(@Header("X-Bearer-Token") bearerToken: String): Response<RetryResponse>
}