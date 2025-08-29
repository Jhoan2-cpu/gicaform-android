package com.example.gica2025.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gica2025.data.model.DeleteUserRequest
import com.example.gica2025.data.model.UserItem
import com.example.gica2025.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UsersViewModel : ViewModel() {
    private val userRepository = UserRepository()
    
    private val _uiState = MutableStateFlow(UsersUiState())
    val uiState: StateFlow<UsersUiState> = _uiState.asStateFlow()
    
    fun loadUsers(token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val response = userRepository.getUsers(token)
                if (response.isSuccessful) {
                    val usersResponse = response.body()
                    if (usersResponse?.success == true) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            users = usersResponse.data.users,
                            error = null
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = usersResponse?.message ?: "Error desconocido"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Error de red: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }
    
    fun retryUser(userId: Int, token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                retryLoading = _uiState.value.retryLoading + userId
            )
            
            try {
                val response = userRepository.retryUser(userId, token)
                if (response.isSuccessful) {
                    val retryResponse = response.body()
                    if (retryResponse?.success == true) {
                        _uiState.value = _uiState.value.copy(
                            retryLoading = _uiState.value.retryLoading - userId,
                            actionMessage = retryResponse.message
                        )
                        loadUsers(token)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            retryLoading = _uiState.value.retryLoading - userId,
                            actionMessage = retryResponse?.message ?: "Error al reintentar"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        retryLoading = _uiState.value.retryLoading - userId,
                        actionMessage = "Error de red: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    retryLoading = _uiState.value.retryLoading - userId,
                    actionMessage = "Error: ${e.message}"
                )
            }
        }
    }
    
    fun deleteUser(user: UserItem, token: String) {
        viewModelScope.launch {
            // Si tiene registro externo, usar ese ID; si no, usar wp_user_id
            val userIdForEndpoint = user.external_registration?.id ?: user.wp_user_id
            
            _uiState.value = _uiState.value.copy(
                deleteLoading = _uiState.value.deleteLoading + user.wp_user_id
            )
            
            try {
                val deleteRequest = DeleteUserRequest(
                    user_id = userIdForEndpoint,
                    delete_wp_user = true,
                    wp_user_id = user.wp_user_id
                )
                
                val response = userRepository.deleteUser(userIdForEndpoint, deleteRequest, token)
                if (response.isSuccessful) {
                    val deleteResponse = response.body()
                    if (deleteResponse?.success == true) {
                        _uiState.value = _uiState.value.copy(
                            deleteLoading = _uiState.value.deleteLoading - user.wp_user_id,
                            actionMessage = deleteResponse.message
                        )
                        loadUsers(token)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            deleteLoading = _uiState.value.deleteLoading - user.wp_user_id,
                            actionMessage = deleteResponse?.message ?: "Error al eliminar"
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _uiState.value = _uiState.value.copy(
                        deleteLoading = _uiState.value.deleteLoading - user.wp_user_id,
                        actionMessage = "Error ${response.code()}: ${errorBody ?: response.message()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    deleteLoading = _uiState.value.deleteLoading - user.wp_user_id,
                    actionMessage = "Error: ${e.message}"
                )
            }
        }
    }
    
    fun retryAllUsers(token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val response = userRepository.retryAllUsers(token)
                if (response.isSuccessful) {
                    val retryResponse = response.body()
                    if (retryResponse?.success == true) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            actionMessage = retryResponse.message
                        )
                        loadUsers(token)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            actionMessage = retryResponse?.message ?: "Error al reintentar todos"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        actionMessage = "Error de red: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    actionMessage = "Error: ${e.message}"
                )
            }
        }
    }
    
    fun clearActionMessage() {
        _uiState.value = _uiState.value.copy(actionMessage = null)
    }
}

data class UsersUiState(
    val isLoading: Boolean = false,
    val users: List<UserItem> = emptyList(),
    val error: String? = null,
    val retryLoading: Set<Int> = emptySet(),
    val deleteLoading: Set<Int> = emptySet(),
    val actionMessage: String? = null
)