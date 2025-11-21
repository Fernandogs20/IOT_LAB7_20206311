package com.example.lab06_20206311.network;

import com.example.lab06_20206311.models.RegistroRequest;
import com.example.lab06_20206311.models.RegistroResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Interfaz de servicio API con Retrofit
 * Define los endpoints disponibles
 */
public interface ApiService {

    /**
     * Endpoint para registrar un usuario en el microservicio
     * Envía DNI y correo para validación
     */
    @POST("registro")
    Call<RegistroResponse> registroUsuario(@Body RegistroRequest request);
}
