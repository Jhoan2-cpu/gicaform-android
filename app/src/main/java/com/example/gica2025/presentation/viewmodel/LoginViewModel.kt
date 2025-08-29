package com.example.gica2025.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gica2025.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class LoginUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val username: String = "",
    val password: String = ""
)

class LoginViewModel(application: Application) : AndroidViewModel(application) {
    
    private val authRepository = AuthRepository(application.applicationContext)
    
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()
    
    fun updateUsername(username: String) {
        _uiState.value = _uiState.value.copy(
            username = username,
            errorMessage = null
        )
    }
    
    fun updatePassword(password: String) {
        _uiState.value = _uiState.value.copy(
            password = password,
            errorMessage = null
        )
    }
    
    fun login() {
        val currentState = _uiState.value
        
        if (currentState.username.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Ingrese su usuario")
            return
        }
        
        if (currentState.password.isBlank()) {
            _uiState.value = currentState.copy(errorMessage = "Ingrese su contraseña")
            return
        }
        
        _uiState.value = currentState.copy(
            isLoading = true,
            errorMessage = null
        )
        
        viewModelScope.launch {
            authRepository.login(currentState.username, currentState.password)
                .collect { result ->
                    result.fold(
                        onSuccess = { loginResponse ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSuccess = true,
                                errorMessage = null
                            )
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                isSuccess = false,
                                errorMessage = exception.message ?: "Error desconocido"
                            )
                        }
                    )
                }
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}