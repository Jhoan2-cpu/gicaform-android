package com.example.gica2025

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.gica2025.navigation.AppNavigation
import com.example.gica2025.navigation.Screen
import com.example.gica2025.presentation.viewmodel.MainViewModel
import com.example.gica2025.ui.theme.GicaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            val mainViewModel: MainViewModel = viewModel()
            val uiState by mainViewModel.uiState.collectAsState()
            
            GicaTheme(darkTheme = uiState.isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    val startDestination = if (uiState.isLoggedIn) {
                        Screen.Main.route
                    } else {
                        Screen.Splash.route
                    }
                    
                    AppNavigation(
                        navController = navController,
                        startDestination = startDestination,
                        mainViewModel = mainViewModel
                    )
                }
            }
        }
    }
}