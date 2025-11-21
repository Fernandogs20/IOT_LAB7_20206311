# 🚀 QUICK START GUIDE - AuthService

## ⚡ En 5 Minutos

### Paso 1: Obtén tu IP (1 min)
```powershell
# En PowerShell Windows:
ipconfig

# Busca: IPv4 Address: 192.168.X.X
```

### Paso 2: Edita ApiClient.java (1 min)
```java
// Archivo: app/src/main/java/.../network/ApiClient.java
// Línea 12:

private static final String BASE_URL = "http://TU_IP_AQUI:8080/";
// Ejemplo: private static final String BASE_URL = "http://192.168.50.100:8080/";
```

### Paso 3: Sincroniza Gradle (2 min)
```
En Android Studio: File → Sync Now
```

### Paso 4: ¡Listo! (1 min)
```
Ejecuta la app: Run → Run 'app'
```

---

## 📋 Checklist Pre-Lanzamiento

- [ ] IP configurada en `ApiClient.java`
- [ ] Microservicio corriendo en `http://TU_IP:8080`
- [ ] Endpoint `/registro` responde POST
- [ ] Firebase configurado en la app
- [ ] Permisos de internet agregados (ya están ✅)
- [ ] Dependencias de Retrofit agregadas (ya están ✅)

---

## 🎯 Funcionalidades Disponibles

| Función | Activity | Botón |
|---------|----------|-------|
| **Registro** | RegisterActivity | "Registrarse" |
| **Login** | LoginActivity | "Iniciar Sesión" |
| **Password Recovery** | LoginActivity | "¿Olvidaste tu contraseña?" |
| **Logout** | MainActivity | "Cerrar Sesión" |

---

## 📱 Campos en Registro

```
[Campo] Nombre Completo
[Campo] DNI (8+ dígitos)
[Campo] Correo Electrónico
[Campo] Contraseña (6+ caracteres)
[Campo] Confirmar Contraseña
[Botón] Registrarse
```

---

## 🔄 Flujo de Uso

### 1️⃣ Nuevo Usuario
```
App → Registrarse → Llenar Formulario → Validar → Microservicio → Firebase → Login → ¡Bienvenido!
```

### 2️⃣ Usuario Existente
```
App → Iniciar Sesión → Credenciales → Firebase → MainActivity → ¡Acceso Otorgado!
```

### 3️⃣ Olvide Contraseña
```
App → ¿Olvidaste Contraseña? → Ingresar Email → Firebase Envía Email → Check Email
```

---

## 🆘 Errores Comunes

### ❌ "Unable to resolve host"
```
✓ Verifica IP en ApiClient.java
✓ Microservicio debe estar corriendo
✓ Ambos en misma red WiFi
```

### ❌ "El DNI ya existe"
```
✓ Usa otro DNI para registrarte
✓ O consulta admin para limpiar datos
```

### ❌ "Invalid email"
```
✓ Email debe tener formato: usuario@dominio.com
✓ Verifica que no tenga espacios
```

---

## 📚 Archivos Importantes

| Archivo | Propósito |
|---------|-----------|
| `AuthService.java` | Lógica central de autenticación |
| `ApiClient.java` | Configuración de URL del microservicio |
| `RegisterActivity.java` | Pantalla de registro con DNI |
| `LoginActivity.java` | Pantalla de login + password reset |
| `CONFIGURACION_IP.md` | Guía detallada de configuración |
| `DOCUMENTACION_TECNICA.md` | Documentación técnica completa |

---

## 💾 Copia de Datos

### En Firestore se guardan:
```json
/users/{userId}/
{
  "nombre": "Juan Pérez",
  "email": "juan@example.com",
  "dni": "12345678",
  "fechaRegistro": "2025-11-18T..."
}
```

### En Microservicio se valida:
- ✅ DNI no duplicado
- ✅ Email no duplicado (también Firebase)

---

## 🎓 Ejemplo de Código

### En tu Activity:
```java
public class MiActivity extends AppCompatActivity {
    
    private AuthService authService;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mi_layout);
        
        // Inicializar
        authService = new AuthService(this);
        
        // Usar
        authService.loginWithEmail(
            "user@example.com",
            "password123",
            new AuthService.AuthCallback() {
                @Override
                public void onSuccess(String message) {
                    Toast.makeText(MiActivity.this, message, Toast.LENGTH_SHORT).show();
                }
                
                @Override
                public void onError(String error) {
                    Toast.makeText(MiActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                }
            }
        );
    }
}
```

---

## 🌐 API REST Esperada

### Endpoint: POST /registro

**Request:**
```json
{
  "dni": "12345678",
  "correo": "user@example.com"
}
```

**Response (200 OK):**
```json
{
  "id": "1",
  "dni": "12345678",
  "correo": "user@example.com",
  "mensaje": "Usuario registrado exitosamente",
  "success": true
}
```

**Response (400 Bad Request):**
```json
{
  "error": "El DNI ya existe",
  "mensaje": "El DNI ya existe",
  "success": false
}
```

---

## ✨ Features Implementados

✅ Registro con DNI  
✅ Integración con Microservicio  
✅ Inicio de Sesión  
✅ Recuperación de Contraseña  
✅ Cierre de Sesión  
✅ Validaciones completas  
✅ Manejo de errores  
✅ Guardado en Firestore  
✅ UI moderna con Material Design  
✅ Callbacks asincronos  

---

## 📞 Preguntas Frecuentes

**P: ¿Dónde cargo la IP?**  
R: En `ApiClient.java`, línea 12: `private static final String BASE_URL = "..."`

**P: ¿Qué puerto usa?**  
R: Puerto 8080 (configurable en ApiClient)

**P: ¿Se guarda la contraseña?**  
R: No, la maneja Firebase Auth. En Firestore solo se guardan Nombre, DNI, Email

**P: ¿Puedo cambiar IP en runtime?**  
R: Sí, usa: `ApiClient.setBaseUrl("http://nueva_ip:8080/");`

**P: ¿Funciona con emulador?**  
R: Sí, usa IP: `10.0.2.2:8080` para acceder a localhost

---

## 🎉 ¡Listo para Comenzar!

```
1. Configura IP en ApiClient.java
2. Inicia tu microservicio
3. Ejecuta la app
4. Haz clic en "Registrarse"
5. ¡Disfruta!
```

---

**Tiempo de configuración: ~5 minutos**  
**Documentos adicionales:**
- `CONFIGURACION_IP.md` - Guía detallada
- `DOCUMENTACION_TECNICA.md` - Referencia técnica
- `REFACTORIZACION_AUTHSERVICE.md` - Cambios realizados
