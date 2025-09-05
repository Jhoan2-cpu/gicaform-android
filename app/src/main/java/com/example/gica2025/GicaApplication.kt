package com.example.gica2025

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import com.google.firebase.FirebaseApp

class GicaApplication : Application() {
    
    companion object {
        private const val TAG = "GicaApplication"
        const val NOTIFICATION_CHANNEL_ID = "gica_notifications"
    }
    
    override fun onCreate() {
        super.onCreate()
        
        Log.d(TAG, "Inicializando GICA Application...")
        
        // Inicializar Firebase
        initializeFirebase()
        
        // Crear canal de notificaciones
        createNotificationChannel()
        
        Log.d(TAG, "GICA Application inicializada correctamente")
    }
    
    private fun initializeFirebase() {
        try {
            FirebaseApp.initializeApp(this)
            Log.d(TAG, "✅ Firebase inicializado correctamente")
        } catch (e: Exception) {
            Log.e(TAG, "❌ Error inicializando Firebase: ${e.message}", e)
        }
    }
    
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "GICA Notifications"
            val descriptionText = "Notificaciones de nuevos registros y actualizaciones"
            val importance = NotificationManager.IMPORTANCE_HIGH
            
            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }
            
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
            
            Log.d(TAG, "✅ Canal de notificación creado: $NOTIFICATION_CHANNEL_ID")
        }
    }
}