package com.example.gica2025.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import com.example.gica2025.data.model.DeviceInfo
import com.example.gica2025.data.model.LoginRequest
import com.example.gica2025.data.model.LoginResponse
import com.example.gica2025.data.model.User
import com.example.gica2025.data.network.NetworkModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository(private val context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("gica_prefs", Context.MODE_PRIVATE)
    
    private val authApiService = NetworkModule.authApiService
    
    suspend fun login(username: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            val deviceId = Settings.Secure.getString(
                context.contentResolver, 
                Settings.Secure.ANDROID_ID
            ) ?: "unknown_device"
            
            val loginRequest = LoginRequest(
                username = username,
                password = password,
                deviceInfo = DeviceInfo(deviceId = deviceId)
            )
            
            val response = authApiService.login(loginRequest)
            
            if (response.isSuccessful) {
                response.body()?.let { loginResponse ->
                    if (loginResponse.success) {
                        saveUserSession(loginResponse)
                        emit(Result.success(loginResponse))
                    } else {
                        emit(Result.failure(Exception(loginResponse.message)))
                    }
                } ?: emit(Result.failure(Exception("Respuesta vacía del servidor")))
            } else {
                emit(Result.failure(Exception("Error ${response.code()}: ${response.message()}")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    private fun saveUserSession(loginResponse: LoginResponse) {
        with(sharedPreferences.edit()) {
            putString("auth_token", loginResponse.data?.token)
            putString("expires_at", loginResponse.data?.expiresAt)
            putString("user_id", loginResponse.data?.user?.id.toString())
            putString("username", loginResponse.data?.user?.username)
            putString("display_name", loginResponse.data?.user?.displayName)
            putString("email", loginResponse.data?.user?.email)
            putString("avatar_url", loginResponse.data?.user?.avatarUrl)
            apply()
        }
    }
    
    fun isLoggedIn(): Boolean {
        return sharedPreferences.getString("auth_token", null) != null
    }
    
    fun getAuthToken(): String? {
        return sharedPreferences.getString("auth_token", null)
    }
    
    fun getCurrentUser(): User? {
        val userId = sharedPreferences.getString("user_id", null)?.toIntOrNull()
        val username = sharedPreferences.getString("username", null)
        val displayName = sharedPreferences.getString("display_name", null)
        val email = sharedPreferences.getString("email", null)
        val avatarUrl = sharedPreferences.getString("avatar_url", null)
        
        return if (userId != null && username != null && displayName != null && 
                   email != null && avatarUrl != null) {
            User(
                id = userId,
                username = username,
                email = email,
                displayName = displayName,
                firstName = "",
                lastName = "",
                roles = emptyList(),
                avatarUrl = avatarUrl
            )
        } else null
    }
    
    fun logout() {
        sharedPreferences.edit().clear().apply()
    }
}