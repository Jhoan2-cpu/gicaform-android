package com.example.gica2025.presentation.screens

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gica2025.data.repository.AuthRepository
import com.example.gica2025.navigation.BottomNavItem
import com.example.gica2025.navigation.BottomNavigationBar
import com.example.gica2025.presentation.components.TopBar
import com.example.gica2025.presentation.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onLogout: () -> Unit,
    viewModel: MainViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val navController = rememberNavController()
    val context = LocalContext.current
    val authRepository = remember { AuthRepository(context) }

    LaunchedEffect(Unit) {
        viewModel.checkLoginStatus()
    }

    Scaffold(
        topBar = {
            TopBar()
        },
        bottomBar = {
            BottomNavigationBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                HomeScreen()
            }
            
            composable(BottomNavItem.Profile.route) {
                ProfileScreen(
                    authRepository = authRepository,
                    onLogout = {
                        viewModel.logout()
                        onLogout()
                    }
                )
            }
            
            composable(BottomNavItem.Users.route) {
                UsersScreen(token = uiState.authToken)
            }
            
            composable(BottomNavItem.Settings.route) {
                SettingsScreen(
                    authRepository = authRepository,
                    isDarkTheme = uiState.isDarkTheme,
                    onToggleTheme = { viewModel.toggleTheme() },
                    onLogout = {
                        viewModel.logout()
                        onLogout()
                    }
                )
            }
        }
    }
}