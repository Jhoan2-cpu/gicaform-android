package com.example.gica2025.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.gica2025.MainActivity
import com.example.gica2025.R
import com.example.gica2025.data.model.TokenRequest
import com.example.gica2025.data.network.NetworkModule
import com.example.gica2025.utils.DeviceUtils
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {

    companion object {
        private const val TAG = "FCMService"
        private const val CHANNEL_ID = "gica_notifications"
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        Log.d(TAG, "Mensaje recibido de: ${remoteMessage.from}")

        // Crear canal de notificación
        createNotificationChannel()

        // Obtener título y cuerpo
        val title = remoteMessage.notification?.title ?: "GICA Ingenieros"
        val body = remoteMessage.notification?.body ?: "Nueva notificación"

        // Mostrar notificación
        showNotification(title, body, remoteMessage.data)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Nuevo token FCM: $token")

        // Enviar token al servidor WordPress
        sendTokenToServer(token)
    }

    private fun showNotification(title: String, body: String, data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            // Agregar datos extra si es necesario
            data.forEach { (key, value) ->
                putExtra(key, value)
            }
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 
            0, 
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_gica_logo)
            .setContentTitle(title)
            .setContentText(body)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "GICA Notifications"
            val descriptionText = "Notificaciones de nuevos registros y actualizaciones"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendTokenToServer(token: String) {
        Log.d(TAG, "📤 Enviando NUEVO token FCM a WordPress (mobile-api-public.php): $token")
        
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Obtener información del dispositivo
                val deviceId = DeviceUtils.getDeviceId(this@MyFirebaseMessagingService)
                val deviceInfo = DeviceUtils.getDeviceInfo()
                val appVersion = DeviceUtils.getAppVersion(this@MyFirebaseMessagingService)
                
                Log.d(TAG, "📱 Información del dispositivo (token refresh):")
                Log.d(TAG, "   Device ID: $deviceId")
                Log.d(TAG, "   Device Info: $deviceInfo")
                Log.d(TAG, "   App Version: $appVersion")
                
                // Enviar NUEVO token a WordPress usando FCM API
                val response = NetworkModule.fcmApiService.registerFCMToken(
                    fcmToken = token,
                    deviceId = deviceId,
                    deviceInfo = deviceInfo,
                    appVersion = appVersion
                )
                
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.success == true) {
                        Log.d(TAG, "✅ NUEVO token FCM actualizado exitosamente en WordPress!")
                        Log.d(TAG, "📨 Respuesta: ${responseBody.data?.message}")
                    } else {
                        Log.e(TAG, "❌ WordPress respondió con error: ${responseBody?.data?.message}")
                    }
                } else {
                    Log.e(TAG, "❌ Error HTTP actualizando token: ${response.code()} - ${response.message()}")
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error enviando nuevo token: ${e.message}", e)
            }
        }
    }
}