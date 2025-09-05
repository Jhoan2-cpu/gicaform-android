package com.example.gica2025

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.gica2025.data.model.TokenRequest
import com.example.gica2025.data.network.NetworkModule
import com.example.gica2025.navigation.AppNavigation
import com.example.gica2025.utils.DeviceUtils
import com.example.gica2025.navigation.Screen
import com.example.gica2025.presentation.viewmodel.MainViewModel
import com.example.gica2025.ui.theme.GicaTheme
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    
    companion object {
        private const val TAG = "MainActivity"
    }
    
    // Registro para solicitar permiso de notificaciones
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d(TAG, "Permiso de notificaciones concedido")
            getFCMToken()
        } else {
            Log.w(TAG, "Permiso de notificaciones denegado")
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Logs de debugging detallados
        Log.d(TAG, "🚀 MainActivity iniciada - onCreate()")
        Log.d(TAG, "📱 Dispositivo: ${Build.MANUFACTURER} ${Build.MODEL}")
        Log.d(TAG, "🤖 Android: ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})")
        
        // Solicitar permisos de notificación y obtener token FCM
        Log.d(TAG, "🔔 Iniciando proceso de notificaciones...")
        requestNotificationPermission()
        
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
    
    private fun requestNotificationPermission() {
        Log.d(TAG, "🔐 Verificando permisos de notificación...")
        
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                Log.d(TAG, "📱 Android 13+ detectado - Verificando POST_NOTIFICATIONS")
                when {
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED -> {
                        Log.d(TAG, "✅ Permiso de notificaciones ya concedido")
                        getFCMToken()
                    }
                    else -> {
                        Log.d(TAG, "❓ Solicitando permiso de notificaciones...")
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
            else -> {
                Log.d(TAG, "📱 Android < 13 - Permisos automáticos")
                getFCMToken()
            }
        }
    }
    
    private fun getFCMToken() {
        Log.d(TAG, "🔥 Iniciando obtención de token FCM...")
        
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                Log.d(TAG, "📬 Callback de token FCM ejecutado")
                
                if (!task.isSuccessful) {
                    Log.e(TAG, "❌ Error obteniendo token FCM", task.exception)
                    Log.e(TAG, "❌ Detalles del error: ${task.exception?.message}")
                    return@addOnCompleteListener
                }

                // Obtener token
                val token = task.result
                Log.d(TAG, "🎯 Token FCM obtenido exitosamente!")
                Log.d(TAG, "🔑 Token: $token")
                
                if (token.isNullOrEmpty()) {
                    Log.e(TAG, "❌ Token FCM está vacío o nulo")
                } else {
                    Log.d(TAG, "📡 Enviando token al servidor WordPress...")
                    sendTokenToWordPress(token)
                }
            }
    }

    private fun sendTokenToWordPress(token: String) {
        Log.d(TAG, "📤 Enviando token FCM a WordPress (mobile-api-public.php)...")
        Log.d(TAG, "🔑 Token: ${token.take(50)}...")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Obtener información del dispositivo
                val deviceId = DeviceUtils.getDeviceId(this@MainActivity)
                val deviceInfo = DeviceUtils.getDeviceInfo()
                val appVersion = DeviceUtils.getAppVersion(this@MainActivity)
                
                Log.d(TAG, "📱 Información del dispositivo:")
                Log.d(TAG, "   Device ID: $deviceId")
                Log.d(TAG, "   Device Info: $deviceInfo")
                Log.d(TAG, "   App Version: $appVersion")
                
                // Enviar token a WordPress usando FCM API
                val response = NetworkModule.fcmApiService.registerFCMToken(
                    fcmToken = token,
                    deviceId = deviceId,
                    deviceInfo = deviceInfo,
                    appVersion = appVersion
                )
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true) {
                        Log.d(TAG, "✅ Token FCM registrado exitosamente en WordPress!")
                        Log.d(TAG, "📨 Respuesta: ${responseBody.data?.message}")
                    } else {
                        Log.e(TAG, "❌ WordPress respondió con error: ${responseBody?.data?.message}")
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e(TAG, "❌ Error HTTP: ${response.code()} - ${response.message()}")
                    Log.e(TAG, "❌ URL: ${response.raw().request.url}")
                    Log.e(TAG, "❌ Headers: ${response.headers()}")
                    Log.e(TAG, "❌ Error body: $errorBody")
                    
                    when (response.code()) {
                        401 -> Log.e(TAG, "🔐 PROBLEMA: WordPress requiere autenticación diferente")
                        404 -> Log.e(TAG, "🔍 PROBLEMA: Endpoint no encontrado - verificar URL")
                        500 -> Log.e(TAG, "💥 PROBLEMA: Error interno de WordPress - revisar logs del servidor")
                        else -> Log.e(TAG, "❓ PROBLEMA: Error desconocido del servidor")
                    }
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error enviando token a WordPress: ${e.message}", e)
            }
        }
    }
}