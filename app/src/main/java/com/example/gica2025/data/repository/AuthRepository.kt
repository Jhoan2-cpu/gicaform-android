package com.example.gica2025.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.provider.Settings
import android.util.Log
import com.example.gica2025.data.model.DeviceInfo
import com.example.gica2025.data.model.LoginRequest
import com.example.gica2025.data.model.LoginResponse
import com.example.gica2025.data.model.LogoutResponse
import com.example.gica2025.data.model.PasswordChangeRequest
import com.example.gica2025.data.model.PasswordChangeResponse
import com.example.gica2025.data.model.ProfileResponse
import com.example.gica2025.data.model.ProfileUpdateRequest
import com.example.gica2025.data.model.ProfileUpdateResponse
import com.example.gica2025.data.model.User
import com.example.gica2025.data.network.NetworkModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository(private val context: Context) {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("gica_prefs", Context.MODE_PRIVATE)
    
    private val authApiService = NetworkModule.authApiService
    
    companion object {
        private const val TAG = "AuthRepository"
    }
    
    suspend fun login(username: String, password: String): Flow<Result<LoginResponse>> = flow {
        try {
            Log.d(TAG, "Iniciando login para usuario: $username")
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
            Log.d(TAG, "Respuesta HTTP: ${response.code()} - ${response.message()}")
            
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
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Error body: $errorBody")
                val errorMessage = when {
                    errorBody?.contains("<html>", ignoreCase = true) == true -> 
                        "El servidor devolvió HTML en lugar de JSON. Verifique la URL del endpoint."
                    errorBody != null -> 
                        "Error ${response.code()}: $errorBody"
                    else -> 
                        "Error ${response.code()}: ${response.message()}"
                }
                emit(Result.failure(Exception(errorMessage)))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    private fun saveUserSession(loginResponse: LoginResponse) {
        with(sharedPreferences.edit()) {
            putString("auth_token", loginResponse.data?.token)
            putInt("expires_in", loginResponse.data?.expiresIn ?: 0)
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
    
    suspend fun logout(): Flow<Result<LogoutResponse>> = flow {
        try {
            val token = getAuthToken()
            if (token != null) {
                val response = authApiService.logout(token)
                if (response.isSuccessful) {
                    response.body()?.let { logoutResponse ->
                        if (logoutResponse.success) {
                            clearUserSession()
                            emit(Result.success(logoutResponse))
                        } else {
                            emit(Result.failure(Exception(logoutResponse.message)))
                        }
                    } ?: emit(Result.failure(Exception("Respuesta vacía del servidor")))
                } else {
                    emit(Result.failure(Exception("Error ${response.code()}: ${response.message()}")))
                }
            } else {
                clearUserSession()
                emit(Result.success(LogoutResponse(true, "Sesión cerrada localmente")))
            }
        } catch (e: Exception) {
            clearUserSession()
            emit(Result.failure(e))
        }
    }
    
    private fun clearUserSession() {
        sharedPreferences.edit().clear().apply()
    }
    
    suspend fun getProfile(): Flow<Result<ProfileResponse>> = flow {
        try {
            val token = getAuthToken()
            if (token != null) {
                val response = authApiService.getProfile(token)
                if (response.isSuccessful) {
                    response.body()?.let { profileResponse ->
                        if (profileResponse.success) {
                            emit(Result.success(profileResponse))
                        } else {
                            emit(Result.failure(Exception(profileResponse.message ?: "Error desconocido")))
                        }
                    } ?: emit(Result.failure(Exception("Respuesta vacía del servidor")))
                } else {
                    emit(Result.failure(Exception("Error ${response.code()}: ${response.message()}")))
                }
            } else {
                emit(Result.failure(Exception("Token no disponible")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun updateProfile(profileUpdateRequest: ProfileUpdateRequest): Flow<Result<ProfileUpdateResponse>> = flow {
        try {
            val token = getAuthToken()
            if (token != null) {
                val response = authApiService.updateProfile(token, profileUpdateRequest)
                if (response.isSuccessful) {
                    response.body()?.let { updateResponse ->
                        if (updateResponse.success) {
                            emit(Result.success(updateResponse))
                        } else {
                            emit(Result.failure(Exception(updateResponse.message)))
                        }
                    } ?: emit(Result.failure(Exception("Respuesta vacía del servidor")))
                } else {
                    emit(Result.failure(Exception("Error ${response.code()}: ${response.message()}")))
                }
            } else {
                emit(Result.failure(Exception("Token no disponible")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }
    
    suspend fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String): Flow<Result<PasswordChangeResponse>> = flow {
        try {
            val token = getAuthToken()
            if (token != null) {
                // Validaciones locales
                if (newPassword != confirmPassword) {
                    emit(Result.failure(Exception("Las contraseñas no coinciden")))
                    return@flow
                }
                
                if (newPassword.length < 6) {
                    emit(Result.failure(Exception("La nueva contraseña debe tener al menos 6 caracteres")))
                    return@flow
                }
                
                val passwordChangeRequest = PasswordChangeRequest(
                    currentPassword = currentPassword,
                    newPassword = newPassword,
                    confirmPassword = confirmPassword
                )
                
                val response = authApiService.changePassword(token, passwordChangeRequest)
                if (response.isSuccessful) {
                    response.body()?.let { passwordChangeResponse ->
                        if (passwordChangeResponse.success) {
                            // Si los tokens fueron invalidados, limpiar la sesión local
                            if (passwordChangeResponse.data?.tokensInvalidated == true) {
                                Log.d("AuthRepository", "Tokens invalidated, clearing local session")
                                clearUserSession()
                            }
                            emit(Result.success(passwordChangeResponse))
                        } else {
                            emit(Result.failure(Exception(passwordChangeResponse.message)))
                        }
                    } ?: emit(Result.failure(Exception("Respuesta vacía del servidor")))
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("AuthRepository", "Error changing password: ${response.code()} - $errorBody")
                    
                    // Manejo específico del error 404
                    if (response.code() == 404) {
                        emit(Result.failure(Exception("El endpoint de cambio de contraseña no está disponible en el servidor. Contacte al administrador.")))
                    } else {
                        emit(Result.failure(Exception("Error ${response.code()}: ${errorBody ?: response.message()}")))
                    }
                }
            } else {
                emit(Result.failure(Exception("Token no disponible")))
            }
        } catch (e: Exception) {
            Log.e("AuthRepository", "Exception changing password", e)
            emit(Result.failure(e))
        }
    }
}