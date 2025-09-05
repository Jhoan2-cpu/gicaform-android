package com.example.gica2025.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.gica2025.data.model.User
import com.example.gica2025.data.repository.AuthRepository
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class MainUiState(
    val isLoggedIn: Boolean = false,
    val currentUser: User? = null,
    val isDarkTheme: Boolean = false,
    val authToken: String? = null
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val authRepository = AuthRepository(application.applicationContext)
    
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
    
    private val sharedPreferences = application.getSharedPreferences("gica_prefs", android.content.Context.MODE_PRIVATE)
    
    init {
        checkLoginStatus()
        loadThemePreference()
    }
    
    fun checkLoginStatus() {
        val isLoggedIn = authRepository.isLoggedIn()
        val currentUser = authRepository.getCurrentUser()
        val authToken = authRepository.getAuthToken()
        
        _uiState.value = _uiState.value.copy(
            isLoggedIn = isLoggedIn,
            currentUser = currentUser,
            authToken = authToken
        )
    }
    
    private fun loadThemePreference() {
        val isDarkTheme = sharedPreferences.getBoolean("is_dark_theme", false)
        _uiState.value = _uiState.value.copy(isDarkTheme = isDarkTheme)
    }
    
    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
        _uiState.value = _uiState.value.copy(
            isLoggedIn = false,
            currentUser = null,
            authToken = null
        )
    }
    
    fun toggleTheme() {
        val newTheme = !_uiState.value.isDarkTheme
        _uiState.value = _uiState.value.copy(isDarkTheme = newTheme)
        
        // Guardar la preferencia del tema
        sharedPreferences.edit()
            .putBoolean("is_dark_theme", newTheme)
            .apply()
    }
}