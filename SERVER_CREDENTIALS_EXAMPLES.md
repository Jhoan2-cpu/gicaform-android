# 🔐 Configuración de Credenciales de Servidor

Este proyecto está adaptado para trabajar con servidores que requieren o no autenticación HTTP básica.

## 📋 Configuración en build.gradle.kts

### ✅ Servidor CON autenticación HTTP básica

```kotlin
buildConfigField("String", "BASE_URL", "\"https://mi-servidor-seguro.com/\"")
buildConfigField("String", "HTTP_USERNAME", "\"mi_usuario\"")
buildConfigField("String", "HTTP_PASSWORD", "\"mi_password\"")
```

### ✅ Servidor SIN autenticación HTTP básica

```kotlin
buildConfigField("String", "BASE_URL", "\"https://mi-servidor-publico.com/\"")
buildConfigField("String", "HTTP_USERNAME", "\"\"")  // Vacío
buildConfigField("String", "HTTP_PASSWORD", "\"\"")  // Vacío
```

## 🔧 Cómo funciona internamente

El `NetworkModule.kt` verifica automáticamente si las credenciales están vacías:

```kotlin
// Solo agregar autenticación si las credenciales no están vacías
if (BuildConfig.HTTP_USERNAME.isNotEmpty() && BuildConfig.HTTP_PASSWORD.isNotEmpty()) {
    val credentials = Credentials.basic(BuildConfig.HTTP_USERNAME, BuildConfig.HTTP_PASSWORD)
    requestBuilder.addHeader("Authorization", credentials)
}
```

## 🌐 Ejemplos de configuración por ambiente

### Desarrollo (con credenciales)
```kotlin
create("development") {
    buildConfigField("String", "BASE_URL", "\"https://dev.midominio.com/\"")
    buildConfigField("String", "HTTP_USERNAME", "\"dev_user\"")
    buildConfigField("String", "HTTP_PASSWORD", "\"dev_pass\"")
}
```

### Producción (sin credenciales)
```kotlin
create("production") {
    buildConfigField("String", "BASE_URL", "\"https://api.midominio.com/\"")
    buildConfigField("String", "HTTP_USERNAME", "\"\"")
    buildConfigField("String", "HTTP_PASSWORD", "\"\"")
}
```

## ⚠️ Notas importantes

- Las credenciales vacías (`""`) desactivan automáticamente la autenticación HTTP básica
- No es necesario modificar el código de red para cambiar entre servidores con/sin autenticación
- Esta configuración afecta tanto a las APIs REST como a los endpoints FCM