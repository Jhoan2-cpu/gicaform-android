package com.example.gica2025.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.gica2025.presentation.screens.*
import com.example.gica2025.presentation.viewmodel.MainViewModel

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Main : Screen("main")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Users : Screen("users")
    object Settings : Screen("settings")
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = Screen.Splash.route,
    mainViewModel: MainViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Splash.route) {
            SplashScreen(
                onSplashFinished = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }
        
        composable(Screen.Main.route) {
            MainScreen(
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Main.route) { inclusive = true }
                    }
                },
                viewModel = mainViewModel
            )
        }
    }
}