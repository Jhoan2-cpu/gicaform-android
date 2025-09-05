package com.example.gica2025.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gica2025.data.model.ProfileUpdateRequest
import com.example.gica2025.data.model.User
import com.example.gica2025.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProfileUiState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isEditing: Boolean = false,
    val updateSuccess: Boolean = false,
    val updateMessage: String? = null
)

class ProfileViewModel(private val authRepository: AuthRepository) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            authRepository.getProfile()
                .collect { result ->
                    result.fold(
                        onSuccess = { profileResponse ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                user = profileResponse.data,
                                error = null
                            )
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = exception.message ?: "Error al cargar perfil"
                            )
                        }
                    )
                }
        }
    }
    
    fun updateProfile(profileUpdateRequest: ProfileUpdateRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            
            authRepository.updateProfile(profileUpdateRequest)
                .collect { result ->
                    result.fold(
                        onSuccess = { updateResponse ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                updateSuccess = true,
                                isEditing = false,
                                error = null,
                                updateMessage = updateResponse.message
                            )
                            // Reload profile after update
                            loadProfile()
                        },
                        onFailure = { exception ->
                            _uiState.value = _uiState.value.copy(
                                isLoading = false,
                                error = exception.message ?: "Error al actualizar perfil"
                            )
                        }
                    )
                }
        }
    }
    
    fun startEditing() {
        _uiState.value = _uiState.value.copy(isEditing = true)
    }
    
    fun cancelEditing() {
        _uiState.value = _uiState.value.copy(isEditing = false, error = null)
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }
    
    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(updateSuccess = false, updateMessage = null)
    }
}