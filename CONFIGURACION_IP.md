# ⚙️ CONFIGURACIÓN DE IP - ANTES DE EJECUTAR

## 🎯 PASO 1: Obtén tu IP Local

### En Windows:
```bash
# Abre PowerShell y ejecuta:
ipconfig

# Busca "IPv4 Address" en la sección de tu red wifi/ethernet
# Ejemplo: 192.168.1.100
```

### En Mac/Linux:
```bash
# Abre terminal y ejecuta:
ifconfig
# O
hostname -I

# Busca la IP local (no 127.0.0.1)
```

---

## 📝 PASO 2: Edita ApiClient.java

**Ruta del archivo:**
```
app/src/main/java/com/example/lab06_20206311/network/ApiClient.java
```

**Busca esta línea:**
```java
private static final String BASE_URL = "http://192.168.1.100:8080/";
```

**Reemplázala con tu IP:**
```java
// Ejemplo: Si tu IP es 192.168.50.25
private static final String BASE_URL = "http://192.168.50.25:8080/";
```

---

## ✅ PASO 3: Verifica Microservicio

Asegúrate de que:
- ✅ Microservicio está corriendo en `http://TU_IP:8080`
- ✅ Endpoint `/registro` existe
- ✅ Acepta POST con body: `{ "dni": "...", "correo": "..." }`

---

## 🧪 PASO 4: Prueba la Conexión (Opcional)

Con **Postman** o **curl**:

```bash
# Reemplaza tu IP en el comando
curl -X POST http://192.168.X.X:8080/registro \
  -H "Content-Type: application/json" \
  -d '{"dni":"12345678","correo":"test@test.com"}'

# Debe responder con HTTP 200 o 400 (no error de conexión)
```

---

## 🚀 PASO 5: Ejecuta la Aplicación

```
1. En Android Studio: Run → Run 'app'
2. Abre el formulario de registro
3. Ingresa datos con DNI válido
4. Verifica que el microservicio responda correctamente
```

---

## 📊 Respuestas Esperadas

### ✅ Éxito (HTTP 200)
```json
{
  "id": "1",
  "dni": "12345678",
  "correo": "user@example.com",
  "mensaje": "Usuario registrado exitosamente",
  "success": true
}
```

**Resultado:** Usuario creado en Firebase ✅

### ❌ Error (HTTP 400)
```json
{
  "error": "El DNI ya existe",
  "mensaje": "El DNI ya existe",
  "success": false
}
```

**Resultado:** Mensaje mostrado al usuario ❌

---

## 🐛 Troubleshooting

### Error: "Unable to resolve host"
```
Causa: IP incorrecta o microservicio no accesible
Solución: 
  1. Verifica que TU_IP sea correcta
  2. Verifica que microservicio esté corriendo
  3. Ambos en la misma red
```

### Error: "Connection refused"
```
Causa: Microservicio no corre en puerto 8080
Solución: Inicia microservicio en puerto 8080
```

### Error: "Name or service not known"
```
Causa: IP no válida
Solución: Ejecuta ipconfig y copia IP correcta
```

---

## 💡 Tips

1. **Si usas emulador**: Usa `10.0.2.2` en lugar de `localhost`
   ```java
   // Para emulador
   private static final String BASE_URL = "http://10.0.2.2:8080/";
   ```

2. **Si usas dispositivo físico**: Asegúrate que esté conectado a la misma red WiFi

3. **Para cambiar IP en runtime:**
   ```java
   // En MainActivity o donde necesites
   ApiClient.setBaseUrl("http://192.168.1.50:8080/");
   ```

---

**Una vez configurado, ¡la app debería funcionar correctamente!** 🎉
