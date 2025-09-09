package com.example.gica2025.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gica2025.data.model.User
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
                        val users = usersResponse.data.users
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            users = users,
                            filteredUsers = users,
                            error = null,
                            totalUsers = users.size
                        )
                        filterAndSortUsers()
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
    
    fun getUserById(userId: Int, token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val response = userRepository.getUserById(userId, token)
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse?.success == true) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            actionMessage = "Usuario obtenido correctamente"
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            actionMessage = userResponse?.message ?: "Error al obtener usuario"
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
    
    fun deleteUser(user: User, token: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                deleteLoading = _uiState.value.deleteLoading + user.id
            )
            
            try {
                val response = userRepository.deleteUser(user.id, token)
                if (response.isSuccessful) {
                    val deleteResponse = response.body()
                    if (deleteResponse?.success == true) {
                        // Remove user from local state and recalculate pagination
                        val updatedUsers = _uiState.value.users.filter { it.id != user.id }
                        _uiState.value = _uiState.value.copy(
                            deleteLoading = _uiState.value.deleteLoading - user.id,
                            actionMessage = deleteResponse?.message ?: "Usuario eliminado exitosamente",
                            users = updatedUsers,
                            totalUsers = updatedUsers.size
                        )
                        filterAndSortUsers()
                    } else {
                        _uiState.value = _uiState.value.copy(
                            deleteLoading = _uiState.value.deleteLoading - user.id,
                            actionMessage = deleteResponse?.message ?: "Usuario eliminado exitosamente"
                        )
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    _uiState.value = _uiState.value.copy(
                        deleteLoading = _uiState.value.deleteLoading - user.id,
                        actionMessage = "Error ${response.code()}: ${errorBody ?: response.message()}"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    deleteLoading = _uiState.value.deleteLoading - user.id,
                    actionMessage = "Error: ${e.message}"
                )
            }
        }
    }
    
    
    fun retryAllUsers(token: String) {
        loadUsers(token)
    }
    
    
    fun clearActionMessage() {
        _uiState.value = _uiState.value.copy(actionMessage = null)
    }
    
    
    fun updateRoleFilter(role: String?) {
        _uiState.value = _uiState.value.copy(selectedRole = role)
        filterAndSortUsers()
    }
    
    fun updateSorting(sortBy: String, sortOrder: String) {
        _uiState.value = _uiState.value.copy(sortBy = sortBy, sortOrder = sortOrder)
        filterAndSortUsers()
    }
    
    private fun filterAndSortUsers() {
        val currentState = _uiState.value
        var filteredUsers = currentState.users
        
        // Apply role filter
        currentState.selectedRole?.let { role ->
            filteredUsers = filteredUsers.filter { user ->
                user.roles?.contains(role) == true
            }
        }
        
        // Apply sorting
        filteredUsers = when (currentState.sortBy) {
            "name" -> if (currentState.sortOrder == "asc") {
                filteredUsers.sortedBy { it.displayName }
            } else {
                filteredUsers.sortedByDescending { it.displayName }
            }
            "date" -> if (currentState.sortOrder == "asc") {
                filteredUsers.sortedBy { it.registered }
            } else {
                filteredUsers.sortedByDescending { it.registered }
            }
            "role" -> if (currentState.sortOrder == "asc") {
                filteredUsers.sortedBy { it.roles?.firstOrNull() ?: "zzz" }
            } else {
                filteredUsers.sortedByDescending { it.roles?.firstOrNull() ?: "" }
            }
            else -> filteredUsers
        }
        
        _uiState.value = currentState.copy(
            filteredUsers = filteredUsers
        )
    }
}

data class UsersUiState(
    val isLoading: Boolean = false,
    val users: List<User> = emptyList(),
    val filteredUsers: List<User> = emptyList(),
    val error: String? = null,
    val deleteLoading: Set<Int> = emptySet(),
    val actionMessage: String? = null,
    val selectedRole: String? = null,
    val sortBy: String = "name", // "name", "date", "role"
    val sortOrder: String = "asc", // "asc", "desc"
    val totalUsers: Int = 0
)