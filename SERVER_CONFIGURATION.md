# 🚀 Configuración de Servidor - GICA 2025

Esta guía explica cómo cambiar la configuración del servidor sin modificar el código fuente.

## 📝 Configuraciones Disponibles

### 🔧 Variables Configurables:

- **BASE_URL**: URL del servidor WordPress
- **API_PATH**: Ruta de la API REST de WordPress
- **FCM_ENDPOINT**: Endpoint del plugin FCM
- **HTTP_USERNAME**: Usuario para autenticación HTTP básica
- **HTTP_PASSWORD**: Contraseña para autenticación HTTP básica
- **FCM_API_KEY**: Clave API para el plugin FCM

## 🏗️ Build Variants Disponibles

### 1. **Development** (Desarrollo)
```gradle
./gradlew assembleDevelopmentDebug
```
- Servidor: `https://solid-plot.localsite.io/`
- Credenciales: `cushion` / `neighborly`

### 2. **Staging** (Pruebas)
```gradle
./gradlew assembleStagingDebug
```
- Servidor: `https://staging.your-domain.com/`
- Credenciales: `staging_user` / `staging_password`

### 3. **Production** (Producción)
```gradle
./gradlew assemblyProductionRelease
```
- Servidor: `https://your-production-domain.com/`
- Credenciales: `prod_user` / `prod_password`

## 🔧 Migrar a Nuevo Servidor

### Opción 1: Modificar build.gradle.kts

1. Abrir `app/build.gradle.kts`
2. Crear nuevo flavor o modificar uno existente:

```kotlin
create("newserver") {
    dimension = "server"
    buildConfigField("String", "BASE_URL", "\"https://nuevo-servidor.com/\"")
    buildConfigField("String", "HTTP_USERNAME", "\"nuevo_usuario\"")
    buildConfigField("String", "HTTP_PASSWORD", "\"nueva_password\"")
    buildConfigField("String", "FCM_API_KEY", "\"nueva_api_key\"")
}
```

3. Compilar nueva variante:
```bash
./gradlew assembleNewserverRelease
```

### Opción 2: Variables de Entorno (Recomendado)

1. Crear archivo `local.properties` en la raíz del proyecto:

```properties
# Configuración del servidor
server.base.url=https://nuevo-servidor.com/
server.http.username=nuevo_usuario
server.http.password=nueva_password
server.fcm.api.key=nueva_api_key
```

2. Modificar `build.gradle.kts` para leer del archivo:

```kotlin
// Leer configuración desde local.properties
val localProperties = Properties()
localProperties.load(FileInputStream(rootProject.file("local.properties")))

buildConfigField("String", "BASE_URL", "\"${localProperties.getProperty("server.base.url")}\"")
buildConfigField("String", "HTTP_USERNAME", "\"${localProperties.getProperty("server.http.username")}\"")
```

## ⚡ Cambio Rápido de Servidor

### Para cambiar ÚNICAMENTE a otro servidor:

1. **Modificar solo estas líneas** en `app/build.gradle.kts`:

```kotlin
// CAMBIAR ESTAS LÍNEAS:
buildConfigField("String", "BASE_URL", "\"https://TU-NUEVO-SERVIDOR.com/\"")
buildConfigField("String", "HTTP_USERNAME", "\"TU_USUARIO\"")
buildConfigField("String", "HTTP_PASSWORD", "\"TU_PASSWORD\"")
```

2. **Recompilar:**
```bash
./gradlew clean assembleDebug
```

3. **¡Listo!** La app ahora apunta al nuevo servidor.

## 🔒 Seguridad

### ⚠️ IMPORTANTE:
- **NUNCA** hagas commit de credenciales reales
- Usa `local.properties` para credenciales sensibles
- Agrega `local.properties` a `.gitignore`

### ✅ Buenas Prácticas:
```gitignore
# En .gitignore
local.properties
secrets.properties
```

## 🧪 Probar Configuración

### Verificar que funciona:

1. **Compilar e instalar** la app
2. **Revisar logs** para confirmación:
```bash
adb logcat | grep -E "(BASE_URL|HTTP_USERNAME|Token FCM)"
```
3. **Buscar estos mensajes:**
```
✅ Token FCM registrado exitosamente en WordPress!
📨 Respuesta: Dispositivo registrado correctamente
```

## 📱 Diferentes APKs para Diferentes Servidores

```bash
# Generar APK para desarrollo
./gradlew assembleDevelopmentDebug

# Generar APK para staging  
./gradlew assembleStagingDebug

# Generar APK para producción
./gradlew assembleProductionRelease
```

Cada APK tendrá configuración diferente automáticamente.

---

## 🆘 Troubleshooting

### Problema: "BuildConfig no encontrado"
**Solución:** Asegúrate de tener `buildConfig = true` en `build.gradle.kts`

### Problema: Credenciales incorrectas
**Solución:** Verifica que las credenciales en WordPress coincidan exactamente

### Problema: Endpoint no encontrado
**Solución:** Confirma que el plugin GICAACCOUNT esté activo en WordPress