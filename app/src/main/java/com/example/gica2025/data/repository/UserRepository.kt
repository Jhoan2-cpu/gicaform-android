package com.example.gica2025.data.repository

import com.example.gica2025.data.model.DeleteUserResponse
import com.example.gica2025.data.model.UserResponse
import com.example.gica2025.data.model.UsersResponse
import com.example.gica2025.data.network.NetworkModule
import retrofit2.Response

class UserRepository {
    private val apiService = NetworkModule.authApiService
    
    suspend fun getUsers(
        bearerToken: String,
        page: Int = 1,
        perPage: Int = 10,
        search: String? = null,
        role: String? = null,
        orderBy: String? = null,
        order: String? = null
    ): Response<UsersResponse> {
        return apiService.getUsers(bearerToken, page, perPage, search, role, orderBy, order)
    }
    
    suspend fun getUserById(userId: Int, bearerToken: String): Response<UserResponse> {
        return apiService.getUserById(userId, bearerToken)
    }
    
    suspend fun deleteUser(userId: Int, bearerToken: String): Response<DeleteUserResponse> {
        return apiService.deleteUser(userId, bearerToken)
    }
}