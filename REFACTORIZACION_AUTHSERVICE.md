# 📱 Refactorización de Autenticación - Lab06

## 📋 Descripción General

Se ha refactorizado completamente el sistema de autenticación de la aplicación implementando una clase centralizada `AuthService` que contiene todos los métodos de autenticación.

---

## 🎯 Características Implementadas

### 1. **Clase AuthService** ✅
Ubicación: `app/src/main/java/.../services/AuthService.java`

Métodos disponibles:
- ✅ `loginWithEmail(email, password, callback)` - Inicio de sesión con correo
- ✅ `registerUser(nombre, email, password, dni, callback)` - Registro con integración a microservicio
- ✅ `sendPasswordResetEmail(email, callback)` - Recuperación de contraseña
- ✅ `logout()` - Cierre de sesión
- ✅ `getCurrentUser()` - Obtener usuario actual
- ✅ `isUserAuthenticated()` - Verificar autenticación
- ✅ `getCurrentUserId()` - Obtener UID

### 2. **Modelos DTO** ✅
- `RegistroRequest` - Datos enviados al microservicio (DNI, Correo)
- `RegistroResponse` - Respuesta del microservicio

### 3. **Cliente HTTP con Retrofit** ✅
- `ApiClient` - Configuración de Retrofit
- `ApiService` - Interfaz para el endpoint `/registro`

### 4. **Integración con Microservicio** ✅
El flujo de registro funciona así:

```
1. Usuario completa formulario (Nombre, DNI, Correo, Contraseña)
2. App envía POST a: http://<IP-PC>:8080/registro
   Payload: { "dni": "12345678", "correo": "user@example.com" }
3. Si microservicio responde HTTP 200: Crear usuario en Firebase
4. Si microservicio responde error: Mostrar mensaje de error
```

---

## ⚙️ Configuración Requerida

### 1. **Cambiar IP del Microservicio**

Edita `ApiClient.java`:

```java
// ANTES:
private static final String BASE_URL = "http://192.168.1.100:8080/";

// DESPUÉS (ejemplo):
private static final String BASE_URL = "http://192.168.x.xxx:8080/";
```

Reemplaza `192.168.x.xxx` con la IP de tu computadora.

### 2. **Permisos en AndroidManifest.xml** ✅
Ya están agregados:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### 3. **Dependencias en build.gradle** ✅
Ya están agregadas:
```gradle
implementation 'com.squareup.retrofit2:retrofit:2.11.0'
implementation 'com.squareup.retrofit2:converter-gson:2.11.0'
implementation 'com.google.code.gson:gson:2.10.1'
```

---

## 📱 Cambios en Activities

### **RegisterActivity**
**Antes:**
```java
private FirebaseAuth mAuth;
// Solo email y contraseña
```

**Después:**
```java
private AuthService authService;
// Ahora incluye: Nombre, DNI, Email, Contraseña

// Uso:
authService.registerUser(nombre, email, password, dni, new AuthService.AuthCallback() {
    @Override
    public void onSuccess(String message) { /* éxito */ }
    @Override
    public void onError(String error) { /* error */ }
});
```

### **LoginActivity**
**Cambios principales:**
- ✅ Usa `AuthService` en lugar de Firebase directo
- ✅ Nuevo método para recuperación de contraseña
- ✅ Nuevo botón "¿Olviste tu contraseña?"

**Nuevo método:**
```java
private void showForgotPasswordDialog() {
    // Muestra diálogo para recuperar contraseña
    authService.sendPasswordResetEmail(email, callback);
}
```

---

## 🎨 Cambios en UI

### **activity_register.xml**
Se agregaron dos nuevos campos:
- ✅ **Nombre completo** (campo de texto)
- ✅ **DNI** (campo numérico)

### **activity_login.xml**
Se agregó:
- ✅ Botón **"¿Olvidaste tu contraseña?"**

---

## 📊 Estructura de Carpetas Creadas

```
app/src/main/java/com/example/lab06_20206311/
├── services/
│   └── AuthService.java (Nueva)
├── network/
│   ├── ApiClient.java (Nueva)
│   └── ApiService.java (Nueva)
├── models/
│   ├── RegistroRequest.java (Nueva)
│   ├── RegistroResponse.java (Nueva)
│   └── Tarea.java (Existente)
└── activities/
    ├── LoginActivity.java (Refactorizada)
    ├── RegisterActivity.java (Refactorizada)
    └── MainActivity.java (Sin cambios)
```

---

## 🔄 Flujo de Registro Completo

```
┌─────────────────────────┐
│ RegisterActivity        │
│ (Formulario con DNI)    │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────┐
│ AuthService.register()  │
└────────┬────────────────┘
         │
         ▼
┌─────────────────────────────────────────────┐
│ POST /registro                              │
│ { "dni": "12345678", "correo": "..." }    │
│ → Microservicio                             │
└────────┬────────────────────────────────────┘
         │
    ┌────┴────┐
    │          │
    ▼          ▼
 HTTP 200    HTTP 400+
 (OK)        (Error)
    │          │
    ▼          ▼
┌──────────┐  ┌──────────────┐
│ Firebase │  │ Mostrar      │
│ Register │  │ Mensaje Error│
└──────────┘  └──────────────┘
```

---

## 💡 Ejemplo de Uso en Tu App

```java
// En LoginActivity o donde necesites
authService = new AuthService(this);

// Registrar usuario
authService.registerUser(
    "Juan Pérez",
    "juan@example.com",
    "password123",
    "12345678",
    new AuthService.AuthCallback() {
        @Override
        public void onSuccess(String message) {
            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show();
            goToMainActivity();
        }

        @Override
        public void onError(String error) {
            Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
        }
    }
);

// Iniciar sesión
authService.loginWithEmail(
    "juan@example.com",
    "password123",
    new AuthService.AuthCallback() {
        @Override
        public void onSuccess(String message) {
            goToMainActivity();
        }

        @Override
        public void onError(String error) {
            Toast.makeText(this, "Error: " + error, Toast.LENGTH_LONG).show();
        }
    }
);

// Cerrar sesión
authService.logout();
```

---

## ⚠️ Importante: Configurar IP del Microservicio

1. Obtén la IP de tu computadora:
   - **Windows**: `ipconfig` (busca IPv4 Address)
   - **Mac/Linux**: `ifconfig` o `hostname -I`

2. Edita `ApiClient.java`:
   ```java
   private static final String BASE_URL = "http://TU_IP_AQUI:8080/";
   ```

3. Asegúrate que:
   - El microservicio esté corriendo en puerto 8080
   - El dispositivo/emulador pueda acceder a la IP
   - Ambos estén en la misma red

---

## ✨ Validaciones Incluidas

### **En RegisterActivity:**
- ✅ Nombre no vacío
- ✅ DNI válido (mínimo 8 caracteres)
- ✅ Email válido
- ✅ Contraseña mínimo 6 caracteres
- ✅ Contraseñas coinciden
- ✅ Respuesta del microservicio correcta

### **En LoginActivity:**
- ✅ Email no vacío
- ✅ Email válido (patrón)
- ✅ Contraseña no vacía

---

## 🐛 Troubleshooting

### ❌ Error: "El microservicio no responde"
**Solución:**
1. Verifica que el microservicio esté corriendo
2. Verifica la IP correcta en `ApiClient.java`
3. Comprueba que el puerto sea 8080
4. En Android Studio, ve a Android Monitor para ver logs

### ❌ Error: "Permission denied"
**Solución:**
Verifica que los permisos estén en `AndroidManifest.xml`:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

### ❌ Error: "Firebase user creation failed"
**Solución:**
Asegúrate que Firebase esté correctamente configurado:
1. Verifica `google-services.json`
2. Verifica dependencias de Firebase en `build.gradle`

---

## 📝 Notas

- AuthService maneja automáticamente callbacks asincronos
- Todos los errores se propagan al usuario vía callbacks
- Los datos adicionales (Nombre, DNI) se guardan en Firestore
- Firebase y microservicio se integran seamlessly

---

**¿Preguntas?** Revisa los comentarios en el código de `AuthService.java` 📚
