# ⚡ Cambio de Servidor - Guía Rápida

## 🎯 **SOLO 3 PASOS PARA CAMBIAR SERVIDOR**

### **Paso 1: Abrir archivo de configuración**
📁 Abrir: `app/build.gradle.kts`

### **Paso 2: Cambiar estas 3 líneas (líneas 22-26)**
```kotlin
buildConfigField("String", "BASE_URL", "\"https://TU-NUEVO-SERVIDOR.com/\"")
buildConfigField("String", "HTTP_USERNAME", "\"TU_USUARIO\"")
buildConfigField("String", "HTTP_PASSWORD", "\"TU_PASSWORD\"")
```

### **Paso 3: Recompilar**
```bash
./gradlew clean assembleDebug
```

---

## 📝 **EJEMPLO COMPLETO**

### **Cambiar de:**
```kotlin
buildConfigField("String", "BASE_URL", "\"https://servidor-viejo.com/\"")
buildConfigField("String", "HTTP_USERNAME", "\"usuario_viejo\"")
buildConfigField("String", "HTTP_PASSWORD", "\"password_vieja\"")
```

### **A:**
```kotlin
buildConfigField("String", "BASE_URL", "\"https://servidor-nuevo.com/\"")
buildConfigField("String", "HTTP_USERNAME", "\"usuario_nuevo\"")
buildConfigField("String", "HTTP_PASSWORD", "\"password_nueva\"")
```

---

## 🔧 **UBICACIONES EXACTAS A CAMBIAR**

En `app/build.gradle.kts`, cambiar en **3 lugares**:

### **1. Configuración por defecto (línea ~22)**
```kotlin
defaultConfig {
    // ...
    buildConfigField("String", "BASE_URL", "\"https://NUEVO-SERVIDOR/\"")
    buildConfigField("String", "HTTP_USERNAME", "\"NUEVO_USER\"")
    buildConfigField("String", "HTTP_PASSWORD", "\"NUEVO_PASS\"")
}
```

### **2. Build type debug (línea ~40)**
```kotlin
debug {
    buildConfigField("String", "BASE_URL", "\"https://NUEVO-SERVIDOR/\"")
    buildConfigField("String", "HTTP_USERNAME", "\"NUEVO_USER\"")
    buildConfigField("String", "HTTP_PASSWORD", "\"NUEVO_PASS\"")
}
```

### **3. Flavor development (línea ~66)**
```kotlin
create("development") {
    buildConfigField("String", "BASE_URL", "\"https://NUEVO-SERVIDOR/\"")
    buildConfigField("String", "HTTP_USERNAME", "\"NUEVO_USER\"")
    buildConfigField("String", "HTTP_PASSWORD", "\"NUEVO_PASS\"")
}
```

---

## ✅ **CHECKLIST RÁPIDO**

- [ ] **URL completa** con `https://` y `/` final
- [ ] **Usuario** entre comillas dobles
- [ ] **Password** entre comillas dobles
- [ ] **Cambiado en los 3 lugares** mencionados
- [ ] **Clean + rebuild** del proyecto

---

## 🚀 **COMANDOS PARA RECOMPILAR**

### **Limpieza completa:**
```bash
./gradlew clean
```

### **Generar APK de desarrollo:**
```bash
./gradlew assembleDebug
```

### **Generar APK de producción:**
```bash
./gradlew assembleRelease
```

---

## 📱 **VERIFICAR QUE FUNCIONA**

Después de instalar la app, revisar logs:
```bash
adb logcat | grep -E "(BASE_URL|HTTP_USERNAME|Token FCM)"
```

### **Logs esperados:**
```
POST https://TU-NUEVO-SERVIDOR/wp-content/plugins/GICAACCOUNT/mobile-api-public.php
Authorization: Basic [credenciales_codificadas]
✅ Token FCM registrado exitosamente en WordPress!
```

---

## 🆘 **SOLUCIÓN RÁPIDA A PROBLEMAS**

### **Error: "BuildConfig not found"**
**Solución:** Hacer Clean + Rebuild
```bash
./gradlew clean build
```

### **Error: HTTP 401**
**Solución:** Verificar credenciales en WordPress coincidan exactamente

### **Error: "URL malformed"**
**Solución:** Asegúrate que la URL tenga `https://` y termine en `/`

---

## 📋 **PLANTILLA PARA COPIAR/PEGAR**

```kotlin
// REEMPLAZAR ESTAS 3 LÍNEAS EN LOS 3 LUGARES MENCIONADOS:
buildConfigField("String", "BASE_URL", "\"https://SERVIDOR/\"")
buildConfigField("String", "HTTP_USERNAME", "\"USUARIO\"")
buildConfigField("String", "HTTP_PASSWORD", "\"PASSWORD\"")
```

### **Ejemplo real:**
```kotlin
buildConfigField("String", "BASE_URL", "\"https://mi-empresa.com/\"")
buildConfigField("String", "HTTP_USERNAME", "\"admin\"")
buildConfigField("String", "HTTP_PASSWORD", "\"mi_password_segura\"")
```

---

## ⏱️ **TIEMPO ESTIMADO: 2 MINUTOS**

1. **30 segundos** - Cambiar 3 líneas en 3 lugares
2. **1 minuto** - Clean + Rebuild  
3. **30 segundos** - Instalar APK

**¡Y listo! Tu app ahora funciona con el nuevo servidor.** 🎉