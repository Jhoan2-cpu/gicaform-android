package com.example.gica2025.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gica2025.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChangePasswordViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChangePasswordUiState())
    val uiState: StateFlow<ChangePasswordUiState> = _uiState.asStateFlow()

    fun updateCurrentPassword(password: String) {
        _uiState.value = _uiState.value.copy(currentPassword = password)
        validatePasswords()
    }

    fun updateNewPassword(password: String) {
        _uiState.value = _uiState.value.copy(newPassword = password)
        validatePasswords()
    }

    fun updateConfirmPassword(password: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = password)
        validatePasswords()
    }

    private fun validatePasswords() {
        val currentState = _uiState.value
        val isValidNewPassword = currentState.newPassword.length >= 6
        val doPasswordsMatch = currentState.newPassword == currentState.confirmPassword
        val isCurrentPasswordFilled = currentState.currentPassword.isNotEmpty()

        _uiState.value = currentState.copy(
            isValid = isValidNewPassword && doPasswordsMatch && isCurrentPasswordFilled,
            newPasswordError = if (currentState.newPassword.isNotEmpty() && !isValidNewPassword) {
                "La contraseña debe tener al menos 6 caracteres"
            } else null,
            confirmPasswordError = if (currentState.confirmPassword.isNotEmpty() && !doPasswordsMatch) {
                "Las contraseñas no coinciden"
            } else null
        )
    }

    fun changePassword() {
        val currentState = _uiState.value
        if (!currentState.isValid || currentState.isLoading) return

        viewModelScope.launch {
            _uiState.value = currentState.copy(isLoading = true, error = null)

            authRepository.changePassword(
                currentPassword = currentState.currentPassword,
                newPassword = currentState.newPassword,
                confirmPassword = currentState.confirmPassword
            ).collect { result ->
                result.fold(
                    onSuccess = { response ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            success = true,
                            successMessage = response.message
                        )
                    },
                    onFailure = { error ->
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            error = error.message
                        )
                    }
                )
            }
        }
    }

    fun clearState() {
        _uiState.value = ChangePasswordUiState()
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(error = null)
    }

    fun clearSuccess() {
        _uiState.value = _uiState.value.copy(success = false, successMessage = null)
    }
}

data class ChangePasswordUiState(
    val isLoading: Boolean = false,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val newPasswordError: String? = null,
    val confirmPasswordError: String? = null,
    val isValid: Boolean = false,
    val error: String? = null,
    val success: Boolean = false,
    val successMessage: String? = null
)