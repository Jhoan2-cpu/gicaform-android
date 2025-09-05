# 📱 Implementación FCM para App Android - GICA Account

Esta guía completa te ayudará a implementar Firebase Cloud Messaging en tu app Android para recibir notificaciones push desde WordPress.

## 🚀 Requisitos Previos

- Android Studio
- Proyecto Android existente
- Acceso a Firebase Console
- Dispositivo Android físico (recomendado para pruebas)

---

## 📋 **PASO 1: Configuración de Firebase Console**

### 1.1 Crear App Android en Firebase
1. Ve a [Firebase Console](https://console.firebase.google.com)
2. Selecciona tu proyecto: **`gicaform-notifications`**
3. Haz clic en **"Agregar app"** → **Android**
4. Completa los datos:
   ```
   Nombre del paquete Android: com.tuempresa.gicaapp
   Alias de la app: GICA App
   Certificado SHA-1: (opcional, obtén con: keytool -list -v -keystore ~/.android/debug.keystore)
   ```

### 1.2 Descargar google-services.json
1. Descarga el archivo `google-services.json`
2. Colócalo en `app/` de tu proyecto Android
3. **IMPORTANTE:** Verifica que contenga tu project_id:
   ```json
   {
     "project_info": {
       "project_id": "gicaform-notifications"
     }
   }
   ```

---

## ⚙️ **PASO 2: Configuración del Proyecto Android**

### 2.1 Configurar build.gradle (Project level)
```gradle
// build.gradle (Project: YourApp)
buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.3.15'
    }
}
```

### 2.2 Configurar build.gradle (App level)
```gradle
// build.gradle (Module: app)
apply plugin: 'com.android.application'
apply plugin: 'com.google.gms.google-services'

android {
    compileSdk 34
    
    defaultConfig {
        applicationId "com.tuempresa.gicaapp"
        minSdk 24
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }
}

dependencies {
    implementation 'com.google.firebase:firebase-bom:32.6.0'
    implementation 'com.google.firebase:firebase-messaging'
    implementation 'com.google.firebase:firebase-analytics'
    
    // Para HTTP requests
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.google.code.gson:gson:2.10.1'
}
```

### 2.3 Configurar AndroidManifest.xml
```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- Permisos necesarios -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.VIBRATE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    
    <application
        android:name=".GicaApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:theme="@style/Theme.GicaApp">
        
        <!-- Actividad principal -->
        <activity
            android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        
        <!-- Servicio FCM -->
        <service
            android:name=".fcm.GicaFirebaseMessagingService"
            android:exported="false">
            <intent-filter>
                <action android:name="com.google.firebase.MESSAGING_EVENT" />
            </intent-filter>
        </service>
        
        <!-- Metadata para notificaciones -->
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_icon"
            android:resource="@drawable/ic_notification" />
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_color"
            android:resource="@color/colorAccent" />
        <meta-data
            android:name="com.google.firebase.messaging.default_notification_channel_id"
            android:value="@string/default_notification_channel_id" />
            
    </application>
</manifest>
```

---

## 💻 **PASO 3: Implementar Código Java/Kotlin**

### 3.1 Crear GicaApplication.java
```java
// app/src/main/java/com/tuempresa/gicaapp/GicaApplication.java
package com.tuempresa.gicaapp;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

public class GicaApplication extends Application {
    public static final String NOTIFICATION_CHANNEL_ID = "gica_notifications";
    
    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }
    
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "GICA Notifications";
            String description = "Notificaciones de la app GICA";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            
            NotificationChannel channel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID, 
                name, 
                importance
            );
            channel.setDescription(description);
            
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
```

### 3.2 Crear GicaFirebaseMessagingService.java
```java
// app/src/main/java/com/tuempresa/gicaapp/fcm/GicaFirebaseMessagingService.java
package com.tuempresa.gicaapp.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tuempresa.gicaapp.GicaApplication;
import com.tuempresa.gicaapp.MainActivity;
import com.tuempresa.gicaapp.R;
import com.tuempresa.gicaapp.api.WordPressApiService;

public class GicaFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "GicaFCMService";
    
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "Mensaje FCM recibido de: " + remoteMessage.getFrom());
        
        // Verificar si el mensaje tiene notificación
        if (remoteMessage.getNotification() != null) {
            String title = remoteMessage.getNotification().getTitle();
            String body = remoteMessage.getNotification().getBody();
            
            Log.d(TAG, "Título: " + title);
            Log.d(TAG, "Cuerpo: " + body);
            
            showNotification(title, body, remoteMessage.getData());
        }
        
        // Verificar si el mensaje tiene datos
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Datos del mensaje: " + remoteMessage.getData());
            handleDataMessage(remoteMessage.getData());
        }
    }
    
    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Nuevo token FCM: " + token);
        
        // Enviar el token a WordPress
        WordPressApiService.getInstance().registerFCMToken(
            this,
            token,
            new WordPressApiService.ApiCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    Log.d(TAG, "Token FCM registrado exitosamente en WordPress");
                }
                
                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error registrando token FCM: " + error);
                }
            }
        );
    }
    
    private void showNotification(String title, String body, java.util.Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        
        // Agregar datos extras si existen
        for (java.util.Map.Entry<String, String> entry : data.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 
            0, 
            intent, 
            PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder notificationBuilder = 
            new NotificationCompat.Builder(this, GicaApplication.NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title != null ? title : "GICA")
                .setContentText(body != null ? body : "Nueva notificación")
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);
        
        NotificationManager notificationManager = 
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        notificationManager.notify(0, notificationBuilder.build());
    }
    
    private void handleDataMessage(java.util.Map<String, String> data) {
        // Procesar datos adicionales del mensaje
        String type = data.get("type");
        String userId = data.get("user_id");
        String url = data.get("url");
        
        Log.d(TAG, "Tipo de mensaje: " + type);
        
        // Aquí puedes agregar lógica específica según el tipo de notificación
        switch (type != null ? type : "") {
            case "user_registration":
                Log.d(TAG, "Notificación de nuevo usuario registrado");
                break;
            case "test":
                Log.d(TAG, "Notificación de prueba recibida");
                break;
            default:
                Log.d(TAG, "Tipo de notificación no reconocido: " + type);
                break;
        }
    }
}
```

### 3.3 Crear WordPressApiService.java
```java
// app/src/main/java/com/tuempresa/gicaapp/api/WordPressApiService.java
package com.tuempresa.gicaapp.api;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.*;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class WordPressApiService {
    private static final String TAG = "WordPressAPI";
    private static final String BASE_URL = "https://tu-sitio.com"; // ←← CAMBIAR POR TU URL
    private static final String API_KEY = "gica_mobile_2024"; // Clave de seguridad
    
    private static WordPressApiService instance;
    private OkHttpClient client;
    private Gson gson;
    
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(String error);
    }
    
    private WordPressApiService() {
        client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        gson = new Gson();
    }
    
    public static synchronized WordPressApiService getInstance() {
        if (instance == null) {
            instance = new WordPressApiService();
        }
        return instance;
    }
    
    /**
     * Registrar token FCM en WordPress
     */
    public void registerFCMToken(Context context, String fcmToken, ApiCallback<Void> callback) {
        String deviceId = getDeviceId(context);
        String deviceInfo = getDeviceInfo();
        String appVersion = getAppVersion(context);
        
        FormBody formBody = new FormBody.Builder()
            .add("action", "gica_mobile_register_token")
            .add("api_key", API_KEY)
            .add("fcm_token", fcmToken)
            .add("device_id", deviceId)
            .add("device_info", deviceInfo)
            .add("app_version", appVersion)
            .add("user_id", "") // Opcional: ID de usuario si está logueado
            .build();
        
        Request request = new Request.Builder()
            .url(BASE_URL + "/wp-admin/admin-ajax.php")
            .post(formBody)
            .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error de red registrando token FCM", e);
                callback.onError("Error de conexión: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d(TAG, "Respuesta registro token: " + responseBody);
                
                try {
                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                    boolean success = jsonResponse.get("success").getAsBoolean();
                    
                    if (success) {
                        Log.d(TAG, "Token FCM registrado exitosamente");
                        callback.onSuccess(null);
                    } else {
                        String errorMessage = "Error desconocido";
                        if (jsonResponse.has("data") && 
                            jsonResponse.getAsJsonObject("data").has("message")) {
                            errorMessage = jsonResponse.getAsJsonObject("data")
                                .get("message").getAsString();
                        }
                        Log.e(TAG, "Error del servidor: " + errorMessage);
                        callback.onError(errorMessage);
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parseando respuesta JSON", e);
                    callback.onError("Error procesando respuesta del servidor");
                }
            }
        });
    }
    
    /**
     * Obtener notificaciones desde WordPress
     */
    public void getNotifications(Context context, ApiCallback<NotificationList> callback) {
        String deviceId = getDeviceId(context);
        
        FormBody formBody = new FormBody.Builder()
            .add("action", "gica_mobile_get_notifications")
            .add("api_key", API_KEY)
            .add("device_id", deviceId)
            .add("limit", "20")
            .build();
        
        Request request = new Request.Builder()
            .url(BASE_URL + "/wp-admin/admin-ajax.php")
            .post(formBody)
            .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Error obteniendo notificaciones", e);
                callback.onError("Error de conexión: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseBody = response.body().string();
                Log.d(TAG, "Respuesta notificaciones: " + responseBody);
                
                try {
                    JsonObject jsonResponse = gson.fromJson(responseBody, JsonObject.class);
                    boolean success = jsonResponse.get("success").getAsBoolean();
                    
                    if (success) {
                        NotificationList notifications = gson.fromJson(
                            jsonResponse.getAsJsonObject("data"), 
                            NotificationList.class
                        );
                        callback.onSuccess(notifications);
                    } else {
                        callback.onError("Error obteniendo notificaciones");
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error parseando notificaciones", e);
                    callback.onError("Error procesando notificaciones");
                }
            }
        });
    }
    
    private String getDeviceId(Context context) {
        return Settings.Secure.getString(
            context.getContentResolver(), 
            Settings.Secure.ANDROID_ID
        );
    }
    
    private String getDeviceInfo() {
        return android.os.Build.MANUFACTURER + " " + 
               android.os.Build.MODEL + " (Android " + 
               android.os.Build.VERSION.RELEASE + ")";
    }
    
    private String getAppVersion(Context context) {
        try {
            return context.getPackageManager()
                .getPackageInfo(context.getPackageName(), 0)
                .versionName;
        } catch (Exception e) {
            return "1.0";
        }
    }
    
    // Clases para deserialización JSON
    public static class NotificationList {
        public NotificationItem[] notifications;
        public int total_count;
        public String timestamp;
    }
    
    public static class NotificationItem {
        public String id;
        public String title;
        public String body;
        public Object data;
        public String timestamp;
        public boolean success;
    }
}
```

### 3.4 Actualizar MainActivity.java
```java
// app/src/main/java/com/tuempresa/gicaapp/MainActivity.java
package com.tuempresa.gicaapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tuempresa.gicaapp.api.WordPressApiService;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1001;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        // Solicitar permisos de notificación (Android 13+)
        requestNotificationPermission();
        
        // Obtener token FCM
        obtainFCMToken();
    }
    
    private void requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) 
                != PackageManager.PERMISSION_GRANTED) {
                
                ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    REQUEST_NOTIFICATION_PERMISSION
                );
            }
        }
    }
    
    private void obtainFCMToken() {
        FirebaseMessaging.getInstance().getToken()
            .addOnCompleteListener(new OnCompleteListener<String>() {
                @Override
                public void onComplete(@NonNull Task<String> task) {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Error obteniendo token FCM", task.getException());
                        return;
                    }
                    
                    // Obtener nuevo token FCM
                    String token = task.getResult();
                    Log.d(TAG, "Token FCM: " + token);
                    
                    // Registrar token en WordPress
                    registerTokenInWordPress(token);
                }
            });
    }
    
    private void registerTokenInWordPress(String token) {
        WordPressApiService.getInstance().registerFCMToken(
            this,
            token,
            new WordPressApiService.ApiCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    runOnUiThread(() -> {
                        Toast.makeText(
                            MainActivity.this, 
                            "✅ Token FCM registrado en WordPress", 
                            Toast.LENGTH_SHORT
                        ).show();
                    });
                }
                
                @Override
                public void onError(String error) {
                    Log.e(TAG, "Error registrando token: " + error);
                    runOnUiThread(() -> {
                        Toast.makeText(
                            MainActivity.this, 
                            "❌ Error: " + error, 
                            Toast.LENGTH_LONG
                        ).show();
                    });
                }
            }
        );
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Permiso de notificaciones concedido");
                obtainFCMToken();
            } else {
                Log.w(TAG, "Permiso de notificaciones denegado");
                Toast.makeText(this, "Las notificaciones están deshabilitadas", 
                             Toast.LENGTH_LONG).show();
            }
        }
    }
}
```

---

## 🎨 **PASO 4: Recursos y Assets**

### 4.1 Crear ic_notification.xml
```xml
<!-- app/src/main/res/drawable/ic_notification.xml -->
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="24dp"
    android:height="24dp"
    android:viewportWidth="24"
    android:viewportHeight="24"
    android:tint="#FFFFFF">
  <path
      android:fillColor="@android:color/white"
      android:pathData="M12,22c1.1,0 2,-0.9 2,-2h-4c0,1.1 0.9,2 2,2zM18,16v-5c0,-3.07 -1.64,-5.64 -4.5,-6.32V4c0,-0.83 -0.67,-1.5 -1.5,-1.5s-1.5,0.67 -1.5,1.5v0.68C7.63,5.36 6,7.92 6,11v5l-2,2v1h16v-1l-2,-2z"/>
</vector>
```

### 4.2 Actualizar strings.xml
```xml
<!-- app/src/main/res/values/strings.xml -->
<resources>
    <string name="app_name">GICA App</string>
    <string name="default_notification_channel_id">gica_notifications</string>
    <string name="fcm_message">Mensaje FCM recibido</string>
</resources>
```

### 4.3 Actualizar colors.xml
```xml
<!-- app/src/main/res/values/colors.xml -->
<resources>
    <color name="colorPrimary">#6200EE</color>
    <color name="colorPrimaryDark">#3700B3</color>
    <color name="colorAccent">#03DAC5</color>
</resources>
```

---

## 🧪 **PASO 5: Testing y Debugging**

### 5.1 Logs importantes a monitorear
```bash
# En Android Studio Logcat, filtra por estos tags:
- GicaFCMService
- MainActivity  
- WordPressAPI
- FirebaseMessaging
```

### 5.2 Comandos ADB útiles
```bash
# Ver logs en tiempo real
adb logcat | grep -E "(GicaFCMService|MainActivity|WordPressAPI)"

# Limpiar logs
adb logcat -c

# Instalar APK para testing
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

### 5.3 Testing desde WordPress Admin
1. Ve a WordPress Admin → GICA Account → FCM
2. Revisa la sección "Dispositivos Móviles Registrados"
3. Usa "Enviar Notificación de Prueba"
4. Crea notificaciones personalizadas

---

## 🔧 **PASO 6: Configuración WordPress (Ya está listo)**

Las siguientes URLs de tu WordPress ya están configuradas:

### Endpoints disponibles:
- **Registrar Token:** `POST /wp-admin/admin-ajax.php`
  ```
  action=gica_mobile_register_token
  api_key=gica_mobile_2024
  fcm_token=[TOKEN]
  device_id=[DEVICE_ID]
  ```

- **Obtener Notificaciones:** `POST /wp-admin/admin-ajax.php`
  ```
  action=gica_mobile_get_notifications
  api_key=gica_mobile_2024
  device_id=[DEVICE_ID]
  ```

---

## ✅ **PASO 7: Checklist Final**

### Antes de compilar:
- [ ] `google-services.json` en carpeta `app/`
- [ ] Cambiar `BASE_URL` en `WordPressApiService.java`
- [ ] Cambiar `applicationId` en `build.gradle`
- [ ] Permisos agregados en `AndroidManifest.xml`
- [ ] Iconos de notificación creados

### Después de instalar:
- [ ] La app solicita permisos de notificación
- [ ] Se genera y registra el token FCM automáticamente
- [ ] WordPress muestra el dispositivo registrado
- [ ] Las notificaciones de prueba llegan correctamente

---

## 🚨 **Troubleshooting**

### Problema: No se genera el token FCM
**Solución:**
- Verifica que `google-services.json` esté en la carpeta correcta
- Asegúrate de que el SHA-1 coincida (si se configuró)
- Revisa los logs de Firebase en Logcat

### Problema: Token no se registra en WordPress
**Solución:**
- Verifica la URL en `WordPressApiService.java`
- Confirma que el API key sea correcto: `gica_mobile_2024`
- Revisa los logs de red en Logcat

### Problema: No llegan las notificaciones
**Solución:**
- Verifica permisos de notificación en configuración del dispositivo
- Asegúrate de que el servicio `GicaFirebaseMessagingService` esté declarado
- Prueba con el dispositivo desbloqueado primero

### Problema: Error de compilación
**Solución:**
- Ejecuta `./gradlew clean` y luego `./gradlew build`
- Verifica todas las dependencias en `build.gradle`
- Asegúrate de usar la misma versión de Firebase BOM

---

## 📞 **Soporte**

Si tienes problemas:
1. Revisa los logs de Android Studio
2. Verifica la configuración en WordPress Admin
3. Usa la página de testing: `/wp-content/plugins/GICAACCOUNT/test-fcm.html`

**¡Tu app Android ya está lista para recibir notificaciones push desde WordPress!** 🚀