package com.example.lab06_20206311.services;

import android.content.Context;
import android.util.Log;

import com.example.lab06_20206311.models.RegistroRequest;
import com.example.lab06_20206311.models.RegistroResponse;
import com.example.lab06_20206311.network.ApiClient;
import com.example.lab06_20206311.network.ApiService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Servicio centralizado de autenticación
 * Maneja login, registro, recuperación de contraseña y cierre de sesión
 */
public class AuthService {

    private static final String TAG = "AuthService";
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private ApiService apiService;
    private Context context;

    // Interfaces de callback para operaciones asincrónicas
    public interface AuthCallback {
        void onSuccess(String message);
        void onError(String error);
    }

    public interface UserCallback {
        void onSuccess(FirebaseUser user);
        void onError(String error);
    }

    /**
     * Constructor - Inicializa Firebase Auth
     */
    public AuthService(Context context) {
        this.context = context;
        this.mAuth = FirebaseAuth.getInstance();
        this.mDb = FirebaseFirestore.getInstance();
        this.apiService = ApiClient.getClient().create(ApiService.class);
    }

    /**
     * Inicia sesión con correo y contraseña
     */
    public void loginWithEmail(String email, String password, AuthCallback callback) {
        Log.d(TAG, "Iniciando login con: " + email);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "Login exitoso con UID: " + user.getUid());
                        callback.onSuccess("Inicio de sesión exitoso");
                    } else {
                        String errorMessage = task.getException() != null ? 
                            task.getException().getMessage() : "Error desconocido";
                        Log.e(TAG, "Error en login: " + errorMessage);
                        callback.onError(errorMessage);
                    }
                });
    }

    /**
     * Registra un nuevo usuario
     * 1. Envía DNI y correo al microservicio de registro
     * 2. Si es exitoso, crea usuario en Firebase y guarda datos en Firestore
     */
    public void registerUser(String nombre, String email, String password, String dni, AuthCallback callback) {
        Log.d(TAG, "Iniciando registro: email=" + email + ", dni=" + dni);

        // Primero, enviar al microservicio
        RegistroRequest request = new RegistroRequest(dni, email);

        apiService.registroUsuario(request).enqueue(new retrofit2.Callback<RegistroResponse>() {
            @Override
            public void onResponse(retrofit2.Call<RegistroResponse> call, retrofit2.Response<RegistroResponse> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Microservicio respondió exitosamente");
                    // Si el microservicio responde OK, crear usuario en Firebase
                    createUserInFirebase(nombre, email, password, dni, callback);
                } else {
                    // Microservicio respondió con error
                    String errorMessage = "Error en registro";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage = response.errorBody().string();
                            // Intenta parsear como RegistroResponse para obtener mensaje
                            RegistroResponse errorResponse = new com.google.gson.Gson()
                                    .fromJson(errorMessage, RegistroResponse.class);
                            if (errorResponse != null && errorResponse.getMensaje() != null) {
                                errorMessage = errorResponse.getMensaje();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parseando respuesta del servidor", e);
                    }
                    Log.e(TAG, "Microservicio rechazó: " + errorMessage);
                    callback.onError(errorMessage);
                }
            }

            @Override
            public void onFailure(retrofit2.Call<RegistroResponse> call, Throwable t) {
                String errorMessage = "Error de conexión: " + (t.getMessage() != null ? t.getMessage() : "desconocido");
                Log.e(TAG, "Error conectando con microservicio", t);
                callback.onError(errorMessage);
            }
        });
    }

    /**
     * Crea usuario en Firebase y guarda datos adicionales en Firestore
     */
    private void createUserInFirebase(String nombre, String email, String password, String dni, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Log.d(TAG, "Usuario creado en Firebase: " + user.getUid());
                        // Guardar datos adicionales en Firestore
                        saveUserDataToFirestore(user.getUid(), nombre, email, dni, callback);
                    } else {
                        String errorMessage = task.getException() != null ? 
                            task.getException().getMessage() : "Error al crear usuario";
                        Log.e(TAG, "Error creando usuario en Firebase: " + errorMessage);
                        callback.onError(errorMessage);
                    }
                });
    }

    /**
     * Guarda datos del usuario en Firestore
     */
    private void saveUserDataToFirestore(String userId, String nombre, String email, String dni, AuthCallback callback) {
        java.util.Map<String, Object> userData = new java.util.HashMap<>();
        userData.put("nombre", nombre);
        userData.put("email", email);
        userData.put("dni", dni);
        userData.put("fechaRegistro", new java.util.Date());

        mDb.collection("users").document(userId).set(userData)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Datos guardados en Firestore");
                    callback.onSuccess("Registro exitoso");
                })
                .addOnFailureListener(e -> {
                    String errorMessage = e.getMessage() != null ? e.getMessage() : "Error guardando datos";
                    Log.e(TAG, "Error guardando en Firestore: " + errorMessage);
                    callback.onError(errorMessage);
                });
    }

    /**
     * Envía correo de recuperación de contraseña
     */
    public void sendPasswordResetEmail(String email, AuthCallback callback) {
        Log.d(TAG, "Enviando correo de recuperación a: " + email);

        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "Correo de recuperación enviado");
                        callback.onSuccess("Correo de recuperación enviado a " + email);
                    } else {
                        String errorMessage = task.getException() != null ? 
                            task.getException().getMessage() : "Error desconocido";
                        Log.e(TAG, "Error enviando correo: " + errorMessage);
                        callback.onError(errorMessage);
                    }
                });
    }

    /**
     * Cierra la sesión del usuario actual
     */
    public void logout() {
        Log.d(TAG, "Cerrando sesión");
        mAuth.signOut();
    }

    /**
     * Obtiene el usuario actual
     */
    public FirebaseUser getCurrentUser() {
        return mAuth.getCurrentUser();
    }

    /**
     * Verifica si hay usuario autenticado
     */
    public boolean isUserAuthenticated() {
        return mAuth.getCurrentUser() != null;
    }

    /**
     * Obtiene el UID del usuario actual
     */
    public String getCurrentUserId() {
        FirebaseUser user = mAuth.getCurrentUser();
        return user != null ? user.getUid() : null;
    }
}
