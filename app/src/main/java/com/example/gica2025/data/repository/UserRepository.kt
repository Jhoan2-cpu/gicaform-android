package com.example.gica2025.data.repository

import com.example.gica2025.data.model.DeleteUserRequest
import com.example.gica2025.data.model.DeleteUserResponse
import com.example.gica2025.data.model.RetryResponse
import com.example.gica2025.data.model.UsersResponse
import com.example.gica2025.data.network.NetworkModule
import retrofit2.Response

class UserRepository {
    private val apiService = NetworkModule.authApiService
    
    suspend fun getUsers(bearerToken: String): Response<UsersResponse> {
        return apiService.getUsers(bearerToken)
    }
    
    suspend fun retryUser(userId: Int, bearerToken: String): Response<RetryResponse> {
        return apiService.retryUser(userId, bearerToken)
    }
    
    suspend fun deleteUser(
        userId: Int, 
        deleteRequest: DeleteUserRequest, 
        bearerToken: String
    ): Response<DeleteUserResponse> {
        return apiService.deleteUser(userId, deleteRequest, bearerToken)
    }
    
    suspend fun retryAllUsers(bearerToken: String): Response<RetryResponse> {
        return apiService.retryAllUsers(bearerToken)
    }
}