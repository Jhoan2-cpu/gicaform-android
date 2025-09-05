# 🚀 Migración de Servidor - Guía Completa GICA 2025

Esta guía te permite migrar tu aplicación Android a cualquier servidor WordPress nuevo de forma sistemática y sin errores.

## 📋 **TABLA DE CONTENIDOS**

1. [Preparativos](#preparativos)
2. [Configuración Android](#configuración-android)
3. [Configuración WordPress](#configuración-wordpress)
4. [Verificación y Testing](#verificación-y-testing)
5. [Troubleshooting](#troubleshooting)
6. [Checklist Final](#checklist-final)

---

## 🔧 **PREPARATIVOS**

### **📝 Información que necesitas recopilar:**

Antes de migrar, asegúrate de tener esta información del nuevo servidor:

```
✅ URL del servidor: https://nuevo-servidor.com/
✅ Usuario HTTP Básico: usuario_nuevo
✅ Password HTTP Básico: password_nueva
✅ API Key FCM: gica_mobile_2024 (o nueva)
✅ Credenciales de admin WordPress
✅ Acceso SSH/FTP (opcional)
```

### **🗂️ Estructura de endpoints esperados:**

Tu servidor WordPress debe tener estos endpoints activos:

```
📍 API REST: /wp-json/gicaform/v1/
   ├── /auth/login          (Login de usuarios)
   ├── /auth/register       (Registro de usuarios)  
   └── /users/              (Gestión de usuarios)

📍 FCM Plugin: /wp-content/plugins/GICAACCOUNT/
   └── mobile-api-public.php (Registro de tokens FCM)
```

---

## 📱 **CONFIGURACIÓN ANDROID**

### **Paso 1: Actualizar build.gradle.kts**

Abre el archivo `app/build.gradle.kts` y actualiza **TODAS** estas secciones:

#### **1.1 Configuración por defecto (línea ~22)**
```kotlin
defaultConfig {
    // ... otras configuraciones ...
    
    // ⚠️ CAMBIAR ESTAS LÍNEAS:
    buildConfigField("String", "BASE_URL", "\"https://NUEVO-SERVIDOR.com/\"")
    buildConfigField("String", "API_PATH", "\"wp-json/gicaform/v1/\"")
    buildConfigField("String", "FCM_ENDPOINT", "\"wp-content/plugins/GICAACCOUNT/mobile-api-public.php\"")
    buildConfigField("String", "HTTP_USERNAME", "\"NUEVO_USUARIO\"")
    buildConfigField("String", "HTTP_PASSWORD", "\"NUEVA_PASSWORD\"")
    buildConfigField("String", "FCM_API_KEY", "\"gica_mobile_2024\"")
}
```

#### **1.2 Build type debug (línea ~38)**
```kotlin
debug {
    // ⚠️ CAMBIAR ESTAS LÍNEAS:
    buildConfigField("String", "BASE_URL", "\"https://NUEVO-SERVIDOR.com/\"")
    buildConfigField("String", "HTTP_USERNAME", "\"NUEVO_USUARIO\"")
    buildConfigField("String", "HTTP_PASSWORD", "\"NUEVA_PASSWORD\"")
}
```

#### **1.3 Product flavor development (línea ~63)**
```kotlin
create("development") {
    dimension = "server"
    // ⚠️ CAMBIAR ESTAS LÍNEAS:
    buildConfigField("String", "BASE_URL", "\"https://NUEVO-SERVIDOR.com/\"")
    buildConfigField("String", "HTTP_USERNAME", "\"NUEVO_USUARIO\"")
    buildConfigField("String", "HTTP_PASSWORD", "\"NUEVA_PASSWORD\"")
}
```

### **Paso 2: Configurar otros ambientes (opcional)**

Si planeas tener múltiples servidores, configura también:

#### **2.1 Servidor de staging**
```kotlin
create("staging") {
    dimension = "server"
    buildConfigField("String", "BASE_URL", "\"https://staging-servidor.com/\"")
    buildConfigField("String", "HTTP_USERNAME", "\"staging_user\"")
    buildConfigField("String", "HTTP_PASSWORD", "\"staging_pass\"")
}
```

#### **2.2 Servidor de producción**
```kotlin
create("production") {
    dimension = "server"
    buildConfigField("String", "BASE_URL", "\"https://produccion-servidor.com/\"")
    buildConfigField("String", "HTTP_USERNAME", "\"prod_user\"")
    buildConfigField("String", "HTTP_PASSWORD", "\"prod_pass\"")
}
```

### **Paso 3: Recompilar la aplicación**

```bash
# Limpiar build anterior
./gradlew clean

# Compilar nueva versión
./gradlew assembleDebug

# O para producción
./gradlew assembleRelease
```

---

## 🌐 **CONFIGURACIÓN WORDPRESS**

### **Paso 1: Instalación del plugin GICAACCOUNT**

#### **Opción A: Subir plugin manualmente**
1. Descargar el plugin GICAACCOUNT
2. Subir vía WordPress Admin: `Plugins > Añadir nuevo > Subir plugin`
3. Activar el plugin

#### **Opción B: Clonar desde repositorio**
```bash
# Entrar al directorio de plugins
cd wp-content/plugins/

# Clonar o copiar el plugin GICAACCOUNT
# (sustituye con la fuente correcta del plugin)
```

### **Paso 2: Configurar autenticación HTTP básica**

#### **2.1 En .htaccess (Apache)**
```apache
# Agregar al archivo .htaccess en la raíz de WordPress
AuthType Basic
AuthName "Área Restringida"
AuthUserFile /ruta/completa/.htpasswd
Require valid-user
```

#### **2.2 Crear archivo .htpasswd**
```bash
# Generar usuario y password encriptados
htpasswd -c .htpasswd NUEVO_USUARIO
# Introducir NUEVA_PASSWORD cuando se solicite
```

#### **2.3 En Nginx**
```nginx
# Agregar al bloque server de nginx.conf
auth_basic "Área Restringida";
auth_basic_user_file /ruta/completa/.htpasswd;
```

### **Paso 3: Configurar plugin GICAACCOUNT**

#### **3.1 Verificar configuración FCM**
Ir a WordPress Admin y verificar que:

```php
// En la configuración del plugin, verificar:
$api_key = "gica_mobile_2024";
$allowed_origins = ["https://tu-dominio.com"];
$debug_mode = true; // Para testing inicial
```

#### **3.2 Verificar base de datos**

El plugin debe crear estas tablas automáticamente:
```sql
-- Verificar que estas tablas existan:
wp_gica_mobile_fcm_tokens    -- Para tokens FCM
wp_gica_users                -- Para usuarios de la app
```

### **Paso 4: Configurar permisos de archivos**

```bash
# Dar permisos correctos a los archivos del plugin
chmod -R 755 wp-content/plugins/GICAACCOUNT/
chmod 644 wp-content/plugins/GICAACCOUNT/mobile-api-public.php
```

---

## ✅ **VERIFICACIÓN Y TESTING**

### **Paso 1: Verificar endpoints manualmente**

#### **1.1 Probar endpoint de login**
```bash
curl -X POST https://NUEVO-SERVIDOR.com/wp-json/gicaform/v1/auth/login \
  -H "Authorization: Basic $(echo -n 'NUEVO_USUARIO:NUEVA_PASSWORD' | base64)" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test_user",
    "password": "test_password",
    "device_info": {
      "platform": "android",
      "app_version": "1.0.0",
      "device_id": "test_device"
    }
  }'
```

**Respuesta esperada:**
```json
{
  "success": true,
  "data": {
    "user": {...},
    "token": "..."
  }
}
```

#### **1.2 Probar endpoint FCM**
```bash
curl -X POST https://NUEVO-SERVIDOR.com/wp-content/plugins/GICAACCOUNT/mobile-api-public.php \
  -H "Authorization: Basic $(echo -n 'NUEVO_USUARIO:NUEVA_PASSWORD' | base64)" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "action=gica_mobile_register_token&api_key=gica_mobile_2024&fcm_token=test_token&device_id=test_device&device_info=Test%20Device&app_version=1.0"
```

**Respuesta esperada:**
```json
{
  "success": true,
  "message": "Device registered successfully",
  "data": {
    "device_id": "test_device",
    "action": "registered",
    "record_id": 1
  }
}
```

### **Paso 2: Instalar y probar la app**

#### **2.1 Instalar APK**
```bash
# Instalar en dispositivo conectado
adb install app/build/outputs/apk/debug/app-debug.apk
```

#### **2.2 Monitorear logs en tiempo real**
```bash
# Filtrar logs relevantes
adb logcat | grep -E "(MainActivity|FCMService|okhttp|BASE_URL|HTTP_USERNAME)"
```

#### **2.3 Logs esperados exitosos**
```
MainActivity: 🚀 MainActivity iniciada - onCreate()
MainActivity: 📱 Dispositivo: [DEVICE_INFO]
MainActivity: 🔔 Iniciando proceso de notificaciones...
MainActivity: ✅ Permiso de notificaciones ya concedido
MainActivity: 🔥 Iniciando obtención de token FCM...
MainActivity: 🎯 Token FCM obtenido exitosamente!
okhttp.OkHttpClient: --> POST https://NUEVO-SERVIDOR.com/wp-content/plugins/GICAACCOUNT/mobile-api-public.php
okhttp.OkHttpClient: Authorization: Basic [credentials_encoded]
okhttp.OkHttpClient: <-- 200 https://NUEVO-SERVIDOR.com/wp-content/plugins/GICAACCOUNT/mobile-api-public.php
MainActivity: ✅ Token FCM registrado exitosamente en WordPress!
```

---

## 🆘 **TROUBLESHOOTING**

### **❌ Error HTTP 401 (No autorizado)**

**Síntomas:**
```
okhttp.OkHttpClient: <-- 401 https://servidor.com/...
MainActivity: 🔐 PROBLEMA: WordPress requiere autenticación diferente
```

**Soluciones:**
1. **Verificar credenciales HTTP básicas:**
   ```bash
   # Probar credenciales manualmente
   curl -u NUEVO_USUARIO:NUEVA_PASSWORD https://NUEVO-SERVIDOR.com/
   ```

2. **Verificar archivo .htpasswd:**
   ```bash
   # Ver contenido del archivo
   cat .htpasswd
   # Debe mostrar: NUEVO_USUARIO:$encrypted_password
   ```

3. **Recrear credenciales:**
   ```bash
   htpasswd -c .htpasswd NUEVO_USUARIO
   ```

### **❌ Error HTTP 404 (No encontrado)**

**Síntomas:**
```
okhttp.OkHttpClient: <-- 404 https://servidor.com/wp-json/gicaform/v1/auth/login
MainActivity: 🔍 PROBLEMA: Endpoint no encontrado - verificar URL
```

**Soluciones:**
1. **Verificar plugins activos:**
   - Ir a WordPress Admin > Plugins
   - Asegurar que GICAACCOUNT esté activo

2. **Verificar estructura de permalinks:**
   - Ir a Configuración > Enlaces permanentes
   - Elegir "Nombre de entrada" o "Personalizada"

3. **Verificar archivos del plugin:**
   ```bash
   ls -la wp-content/plugins/GICAACCOUNT/
   # Debe mostrar mobile-api-public.php
   ```

### **❌ Error HTTP 500 (Error interno)**

**Síntomas:**
```
okhttp.OkHttpClient: <-- 500 https://servidor.com/...
MainActivity: 💥 PROBLEMA: Error interno de WordPress - revisar logs del servidor
```

**Soluciones:**
1. **Revisar logs de WordPress:**
   ```bash
   tail -f wp-content/debug.log
   ```

2. **Verificar permisos de archivos:**
   ```bash
   chmod 755 wp-content/plugins/GICAACCOUNT/
   chmod 644 wp-content/plugins/GICAACCOUNT/mobile-api-public.php
   ```

3. **Verificar base de datos:**
   ```sql
   SHOW TABLES LIKE 'wp_gica%';
   -- Debe mostrar las tablas del plugin
   ```

### **❌ Token FCM no se genera**

**Síntomas:**
```
MainActivity: 🔥 Iniciando obtención de token FCM...
(No hay logs posteriores de token)
```

**Soluciones:**
1. **Verificar google-services.json:**
   - Archivo debe estar en `app/google-services.json`
   - Debe corresponder al proyecto Firebase correcto

2. **Verificar permisos:**
   ```bash
   # Verificar que POST_NOTIFICATIONS esté en AndroidManifest.xml
   grep "POST_NOTIFICATIONS" app/src/main/AndroidManifest.xml
   ```

3. **Limpiar y recompilar:**
   ```bash
   ./gradlew clean
   ./gradlew assembleDebug
   ```

---

## 📋 **CHECKLIST FINAL**

### **✅ Configuración Android completada:**
- [ ] build.gradle.kts actualizado en **3 lugares**
- [ ] Credenciales coinciden en **todos** los build variants
- [ ] URL termina en `/` (slash final)
- [ ] Aplicación recompilada con `./gradlew clean assembleDebug`
- [ ] APK instalada en dispositivo de prueba

### **✅ Configuración WordPress completada:**
- [ ] Plugin GICAACCOUNT instalado y activo
- [ ] Autenticación HTTP básica configurada
- [ ] Archivo .htpasswd creado con credenciales correctas
- [ ] Permisos de archivos configurados (755/644)
- [ ] Tablas de base de datos creadas automáticamente

### **✅ Testing completado:**
- [ ] Endpoint de login responde 200
- [ ] Endpoint FCM responde 200  
- [ ] App genera token FCM exitosamente
- [ ] Token se registra en WordPress
- [ ] Login de usuarios funciona
- [ ] No hay errores 401/404/500 en logs

### **✅ Documentación actualizada:**
- [ ] Credenciales guardadas en lugar seguro
- [ ] URLs documentadas para el equipo
- [ ] Procedimiento de rollback definido

---

## 🔒 **SEGURIDAD Y BUENAS PRÁCTICAS**

### **🔐 Gestión de credenciales:**

```bash
# ✅ HACER: Usar variables de entorno
export HTTP_USERNAME="usuario_seguro"
export HTTP_PASSWORD="password_compleja_123!"

# ❌ NO HACER: Hardcodear en el código
buildConfigField("String", "HTTP_PASSWORD", "\"password123\"")
```

### **🗂️ Usar local.properties para desarrollo:**

Crear archivo `local.properties` en la raíz del proyecto:
```properties
# local.properties (no hacer commit de este archivo)
server.base.url=https://desarrollo.midominio.com/
server.http.username=dev_user
server.http.password=dev_password_segura
server.fcm.api.key=gica_mobile_dev_2024
```

Modificar `build.gradle.kts` para usar estas variables:
```kotlin
// Leer desde local.properties
val localProperties = Properties()
if (rootProject.file("local.properties").exists()) {
    localProperties.load(FileInputStream(rootProject.file("local.properties")))
    
    buildConfigField("String", "BASE_URL", "\"${localProperties.getProperty("server.base.url")}\"")
    buildConfigField("String", "HTTP_USERNAME", "\"${localProperties.getProperty("server.http.username")}\"")
    buildConfigField("String", "HTTP_PASSWORD", "\"${localProperties.getProperty("server.http.password")}\"")
}
```

### **📝 Agregar a .gitignore:**
```gitignore
# Archivos sensibles - NO hacer commit
local.properties
secrets.properties
*.keystore
*.jks
```

---

## 🚀 **MIGRACIÓN RÁPIDA (RESUMEN)**

Para una migración rápida, sigue estos pasos esenciales:

1. **Cambiar 3 líneas en 3 lugares** de `app/build.gradle.kts`:
   ```kotlin
   buildConfigField("String", "BASE_URL", "\"https://NUEVO-SERVIDOR/\"")
   buildConfigField("String", "HTTP_USERNAME", "\"NUEVO_USER\"")  
   buildConfigField("String", "HTTP_PASSWORD", "\"NUEVA_PASS\"")
   ```

2. **Recompilar:**
   ```bash
   ./gradlew clean assembleDebug
   ```

3. **Configurar WordPress:**
   - Instalar plugin GICAACCOUNT
   - Configurar HTTP Basic Auth con las mismas credenciales

4. **Probar:**
   - Instalar APK
   - Verificar logs: `adb logcat | grep MainActivity`
   - Buscar: `✅ Token FCM registrado exitosamente`

**¡Y listo!** Tu app ahora funciona con el nuevo servidor. 🎉

---

## 📞 **SOPORTE**

Si encuentras problemas durante la migración:

1. **Revisar logs detallados:** `adb logcat | grep -E "(MainActivity|FCMService|okhttp)"`
2. **Probar endpoints manualmente** con curl
3. **Verificar configuración** paso a paso con este checklist
4. **Consultar troubleshooting** para errores específicos

---

*Guía creada para GICA 2025 - Versión 1.0*